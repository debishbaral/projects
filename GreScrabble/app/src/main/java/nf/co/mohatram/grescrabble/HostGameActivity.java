package nf.co.mohatram.grescrabble;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.badlogic.gdx.utils.Json;

import org.mads.iotapipub.discovery.listeners.IRegistrationListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;
import org.mads.iotapipub.rmi.exception.RMIException;
import org.mads.iotapipub.rmi.handler.CallLookup;
import org.mads.iotapipub.rmi.network.IServerListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;

public class HostGameActivity extends AppCompatActivity implements IRegistrationListener, IQuestionServer, IServerListener {

    public static boolean server_started = false;
    public static boolean all_clients_active = false;
    public static long start_time = 0;
    public static Map<String, TakenAnswer> takenAnswerMap = Collections.synchronizedMap(new HashMap<String, TakenAnswer>());
    private ServerRegistrationService serverRegistrationService;
    private ListView clientList;
    private NameListAdaptor clientListAdaptor;
    private List<ServiceInfo> serviceInfos;
    private Map<String, ServiceInfo> socketServiceInfoMap = new HashMap<>();
    private List<QuestionAnswerModel> randomQuestionSet = new ArrayList<>();
    private List<TakenAnswer> takenAnswers = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private int questionType;
    private Json json = new Json();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);


        databaseHelper = GreQuestionApplication.get().getDatabaseHelper();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
        }

        makeRandomQuestionSet();

        serviceInfos = new ArrayList<>();
        clientListAdaptor = new NameListAdaptor(this, serviceInfos, ServerRegistrationService.key_name_of_player);
        clientList = (ListView) findViewById(R.id.player_list_lv);

        clientList.setAdapter(clientListAdaptor);
        serverRegistrationService = new ServerRegistrationService(getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));


        final RemoteServer remoteServer = GreQuestionApplication.get().getRemoteServer();
        if (server_started) {
            NetworkThread.networkThread.handler.post(new Runnable() {
                @Override
                public void run() {
                    remoteServer.setRemoteInterface(HostGameActivity.this);
                    System.gc();
                }
            });
        }else {
            NetworkThread.networkThread.handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        remoteServer.setRemoteInterface(HostGameActivity.this);
                        remoteServer.setServerListener(new IServerListener() {
                            @Override
                            public void onClientConnected(Socket socket) {

                            }

                            @Override
                            public void onClientDisconnected(Socket socket) {

                            }
                        });
                        remoteServer.startServer();
                        server_started = true;
                    } catch (final RMIException | IOException e) {
                        Log.e("Error", "", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HostGameActivity.this, "Unable to start server " + e, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            });
        }


        String gameMode = getIntent().getStringExtra("game_mode");
        if (gameMode.contains("single")) {
            startGame(null);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeRandomQuestionSet() {

        //choose category
        questionType = getIntent().getIntExtra("question_type", DatabaseHelper.QuestionType.analogy);
        Log.e("Selecting questions", "Error here" + getIntent().getIntExtra("question_type", DatabaseHelper.QuestionType.analogy));
        Cursor questions = databaseHelper.getQuestionForType(questionType);
        int noOfQuestions = questions.getCount();
        if (noOfQuestions > max_no_of_questions) {
            noOfQuestions = max_no_of_questions;
        }
        List<Integer> integers = generateRandomArray(noOfQuestions);

        Log.e("Questions", questions.getCount() + ", " + questionType);

        questions.moveToFirst();

        randomQuestionSet.clear();
        for (int i = 0; i < noOfQuestions; i++) {
            int offset = integers.get(i);
            questions.move(offset);
            String fileName = questions.getString(questions.getColumnIndex(DatabaseHelper.col_question_file));

            try {
                InputStream inputStream = getAssets().open("questions/" + fileName);
                QuestionAnswerModel questionAnswerModel = json.fromJson(QuestionAnswerModel.class, inputStream);
                randomQuestionSet.add(questionAnswerModel);
            } catch (Exception e) {
                Log.e("Unable to parse file", "", e);
            }
            questions.moveToFirst();
        }

    }

    public static final int max_no_of_questions = 10;

    public List<Integer> generateRandomArray(int range) {
        List<Integer> arr = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            arr.add(i);
        }

        Collections.shuffle(arr);

        return arr;
    }


    @Override
    protected void onResume() {
        super.onResume();
        serverRegistrationService.registerService(this);
        serviceInfos.clear();
        HostGameActivity.all_clients_active = false;
        takenAnswerMap.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        serverRegistrationService.unregisterService(this);
        finish();
    }

    @Override
    public void onServiceRegistrationSuccess(ServiceInfo serviceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HostGameActivity.this, "Service registered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onServiceRegistrationFailed(ServiceInfo serviceInfo, final int i, final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HostGameActivity.this, "Service registration failed +" + i + e, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onServiceUnregistrationSuccess(ServiceInfo serviceInfo) {

    }

    @Override
    public void onServiceUnregistrationFailed(ServiceInfo serviceInfo, int i, Exception e) {

    }

    @Override
    public void onClientRequest(ServiceInfo serviceInfo) {

    }

    @Override
    public void onClientConnected(Socket socket) {
        RemoteServer remoteServer = GreQuestionApplication.get().getRemoteServer();
        if (!remoteServer.isInitializationActive()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClientDisconnected(Socket socket) {
        final ServiceInfo serviceInfo;
        String s = socket.getInetAddress().toString();
        serviceInfo = socketServiceInfoMap.get(s);
        serviceInfos.remove(serviceInfo);
        Log.e("Disconnect", s);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HostGameActivity.this, serviceInfo.get(clientListAdaptor.getKey()) + " is disconnected.", Toast.LENGTH_SHORT).show();
                clientListAdaptor.notifyDataSetChanged();
                clientList.invalidateViews();
            }
        });
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, QuestionViewerActivity.class);
        intent.putExtra(ServerRegistrationService.key_name_of_player, getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
        intent.putExtra("address", "127.0.0.1");
        startActivity(intent);
    }

    @Override
    public void mInitialized(final ServiceInfo serviceInfo) {
        TakenAnswer takenAnswer = new TakenAnswer();
        String hostAddress = CallLookup.getCurrentSocket().getInetAddress().getHostAddress();
        takenAnswer.playerName = serviceInfo.get(ServerRegistrationService.key_name_of_player);
        Log.e("Address", hostAddress);
        takenAnswerMap.put(hostAddress, takenAnswer);

        Socket currentSocket = CallLookup.getCurrentSocket();

        String s = currentSocket.getInetAddress().toString();
        socketServiceInfoMap.put(s, serviceInfo);
        serviceInfos.add(serviceInfo);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clientListAdaptor.notifyDataSetChanged();
                clientList.invalidate();
            }
        });

    }

    @Override
    public boolean areAllClientsActive() {
        return all_clients_active;
    }

    @Override
    public List<QuestionAnswerModel> getQuestionAnswerModels() {
        return randomQuestionSet;
    }

    @Override
    public boolean answerGivenByAll() {
        int noOfQuestions = randomQuestionSet.size();
        long timeElapsed = System.currentTimeMillis() - start_time;

        if (timeElapsed > noOfQuestions * QuestionViewerActivity.time_for_each_question)
            return true;

        try {
            for (String thread : takenAnswerMap.keySet()) {
                TakenAnswer takenAnswer = takenAnswerMap.get(thread);
                if (!takenAnswer.given) return false;
            }

        } catch (NullPointerException e) {
            Log.e("NPE", "", e);
            return false;
        }
        return true;
    }

    @Override
    public synchronized void takeAnswer(List<AnswerModel> answerModels, long time) {
        Log.e("CurrentThread", Thread.currentThread().toString() + ", " + CallLookup.getCurrentSocket().toString());
        TakenAnswer takenAnswer;
        takenAnswer = takenAnswerMap.get(CallLookup.getCurrentSocket().getInetAddress().getHostAddress());


        if (takenAnswer == null) {
            Log.e("Error", "Why null");
        }
        takenAnswer.answerModels = answerModels;
        takenAnswer.time = time;
        takenAnswer.given = true;

        for (AnswerModel am : answerModels) {
            Log.e("Answers", am.answer + ", " + am.correct);
        }
    }

    @Override
    public synchronized List<TakenAnswer> requestAnswer() {
        takenAnswers.clear();

        for (String key : takenAnswerMap.keySet()) {
            takenAnswers.add(takenAnswerMap.get(key));
        }
        return takenAnswers;
    }

    @Override
    public int getTimeForEachQuestion() {
        return (int) DatabaseHelper.getTimeForQuestionType(questionType);
    }

    public static class TakenAnswer implements Serializable {
        public List<AnswerModel> answerModels = null;
        public long time = 0;
        public boolean given = false;
        public String playerName;
    }

}

