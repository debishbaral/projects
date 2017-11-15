package nf.co.mohatram.grescrabble;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;
import org.mads.iotapipub.rmi.handler.CallHandler;
import org.mads.iotapipub.rmi.network.IClientListener;
import org.mads.iotapipub.rmi.network.RMIClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;

public class QuestionViewerActivity extends AppCompatActivity implements QuestionSwitcher.QuestionListener {
    public static final int time_for_each_question = 5000;
    public IQuestionServer questionServer;
    private CallHandler callHandler;
    private RMIClient rmiClient;
    private GameLifecycleThread gameLifecycleThread;
    private TextView timer;
    private TextView noOfQuestionsTv;
    private TransitionDrawable transitionDrawable;
    private List<QuestionAnswerModel> questionAnswerModels;
    private CountDownTimer countDownTimer;
    private QuestionSwitcher questionSwitcher;
    private LinearLayout questionSwitcherContainer;
    private View loadingView;
    private int totalNoOfQuestions;
    private int currentQuestion = 0;
    private long startTime;
    private long totalTime;
    private List<AnswerModel> givenAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        setContentView(layoutInflater.inflate(R.layout.activity_question_viewer, null));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
        }


        initializeView();
        makeRemoteProxy();
    }

    private void initializeView() {
        timer = (TextView) findViewById(R.id.tv_timer);
        transitionDrawable = (TransitionDrawable) timer.getBackground();
        noOfQuestionsTv = (TextView) findViewById(R.id.tv_no_of_questions);
        loadingView = findViewById(R.id.loading_view_ll);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void makeRemoteProxy() {
        Intent intent = getIntent();
        final String address = intent.getStringExtra("address");
        NetworkThread.networkThread.handler.post(new Runnable() {
            @Override
            public void run() {

                Log.e("Making remote proxy", "Making");
                callHandler = new CallHandler();
                try {
                    ServiceInfo serviceInfo = new ServiceInfo();
                    serviceInfo.put(ServerRegistrationService.key_name_of_player, getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
                    rmiClient = new RMIClient(address, RemoteServer.server_port, callHandler);
                    rmiClient.addClientListener(new IClientListener() {
                        @Override
                        public void onDisconnected() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(QuestionViewerActivity.this, "Server is disconnected", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    questionServer = (IQuestionServer) rmiClient.getGlobal(IQuestionServer.class);
                    Log.e("Making remote proxy", "Created");
                    questionServer.mInitialized(serviceInfo);

                    Log.e("Making remote proxy", "Called");
                    if (address.equals("127.0.0.1")) {
                        HostGameActivity.all_clients_active = true;
                        HostGameActivity.start_time = System.currentTimeMillis();
                    }
                    gameLifecycleThread = new GameLifecycleThread();
                    gameLifecycleThread.start();

                } catch (final IOException e) {
                    Log.e("Making remote proxy", "Called and error", e);
                    //e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QuestionViewerActivity.this, "Unable to create server proxy" + e, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    //e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onAnswerGiven(QuestionAnswerModel questionAnswerModel, AnswerModel givenAnswer) {
        currentQuestion++;
        noOfQuestionsTv.setText(currentQuestion + "/" + totalNoOfQuestions);
    }

    @Override
    public void onQuestionFinished(List<QuestionAnswerModel> questionAnswerModels, List<AnswerModel> givenAnswers) {

        //hide further questions
        totalTime = System.currentTimeMillis() - startTime;
        this.givenAnswers = givenAnswers;

        synchronized (gameLifecycleThread) {
            gameLifecycleThread.notify();
        }

        countDownTimer.cancel(true);
        questionSwitcherContainer.setVisibility(View.GONE);

        loadingView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void initializeQuestionSwitcher() {
        questionSwitcherContainer = (LinearLayout) findViewById(R.id.question_switcher_container);
        questionSwitcher = new QuestionSwitcher(this, questionAnswerModels);
        questionSwitcher.setQuestionListener(this);
        questionSwitcher.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        questionSwitcherContainer.addView(questionSwitcher);
        questionSwitcher.next();
        loadingView.setVisibility(View.GONE);
        noOfQuestionsTv.setText(currentQuestion + "/" + totalNoOfQuestions);
    }

    public class GameLifecycleThread extends Thread {

        @Override
        public void run() {
            Log.e("Lifecycle", "Lifecycle");

            if (countDownTimer!=null){
                countDownTimer.cancel(true);
                countDownTimer=null;
            }
            while (!questionServer.areAllClientsActive()) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            final int timeForEachQuestion;
            try {
                Log.e("QuestionAnswerModel", "Loading question set");
                questionAnswerModels = questionServer.getQuestionAnswerModels();
                /*for (QuestionAnswerModel qaMode :
                        questionAnswerModels) {
                    Log.e("Question", qaMode.getQuestion());
                }*/
                timeForEachQuestion=questionServer.getTimeForEachQuestion();

                QuestionViewerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startTime = System.currentTimeMillis();
                        totalNoOfQuestions = questionAnswerModels.size();
                        initializeQuestionSwitcher();
                        countDownTimer = new CountDownTimer(timeForEachQuestion, questionAnswerModels.size(), QuestionViewerActivity.this);
                        countDownTimer.execute();
                        Log.e("Timer", "Countdown timer started");


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        }).start();
                    }
                });
            }catch (Exception e){
                Log.e("Unable", "", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionViewerActivity.this, "Unable to get result.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                return;
            }


            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                Log.e("UnInterrupted", "", e);
            }

            Log.e("UnInterrupted", "Thread is uninterrupted");

            //continue to work from here
            //send result to server
            questionServer.takeAnswer(givenAnswers, totalTime);

            List<HostGameActivity.TakenAnswer> friendAnswers = null;
            try {
                //wait for others to finish
                while (!questionServer.answerGivenByAll()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        Log.e("Error", "Error", e);
                    }
                }

                //get backs other user results
                Log.e("Went further", "Yes correct result");

                friendAnswers = questionServer.requestAnswer();

                for (HostGameActivity.TakenAnswer ta : friendAnswers) {
                    Log.e("Player found", ta.playerName);
                }
            } catch (Exception e) {
                Log.e("Error", "", e);
                friendAnswers=new ArrayList<>();
                HostGameActivity.TakenAnswer takenAnswer=new HostGameActivity.TakenAnswer();
                takenAnswer.time=startTime-System.currentTimeMillis();
                takenAnswer.answerModels=givenAnswers;
                takenAnswer.playerName=getIntent().getStringExtra(ServerRegistrationService.key_name_of_player);
                friendAnswers.add(takenAnswer);
            }

            //see the result of player
            ResultDisplayActivity.questionAnswerModels = questionAnswerModels;
            ResultDisplayActivity.takenAnswers = friendAnswers;

            Intent intent = new Intent(QuestionViewerActivity.this, ResultDisplayActivity.class);
            startActivity(intent);
            QuestionViewerActivity.this.finish();
        }
    }



    public class CountDownTimer extends AsyncTask<Void, Boolean, Void> {

        private final QuestionViewerActivity activity;
        private int timeRemaining;

        public CountDownTimer(int timeForEachQuestion, int noOfQuestions, QuestionViewerActivity activity) {
            super();
            this.activity = activity;
            this.timeRemaining = noOfQuestions * timeForEachQuestion;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (timeRemaining > 0) {
                try {
                    Thread.sleep(1000);
                    timeRemaining -= 1000;
                    publishProgress(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(true); //true is for finished time
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            //updating progress
            Log.e("Progress", "Updating progess");
            int min, sec;
            Log.e("Timer", activity.timer.toString());
            sec = (timeRemaining / 1000) % 60;
            min = timeRemaining / 60000;
            if (!values[0]) {
                activity.timer.setText(min + ":" + sec + "s");
                if (timeRemaining < 30000) {
                    activity.transitionDrawable.startTransition(400);
                    activity.transitionDrawable.reverseTransition(400);
                }
            } else {
                activity.questionSwitcher.executeQuestionFinishedHandler();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
