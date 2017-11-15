package nf.co.mohatram.grescrabble;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button joinBtn;
    private Button hostBtn;
    private Button singlePlayerBtn;
    private EditText playerNameEt;
    private Random random=new Random();
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        hostBtn=(Button) findViewById(R.id.btn_host_game);
        joinBtn=(Button) findViewById(R.id.btn_join_game);
        singlePlayerBtn=(Button) findViewById(R.id.btn_single_player);
        playerNameEt=(EditText) findViewById(R.id.et_player_name);
        radioGroup= (RadioGroup) findViewById(R.id.question_types);

        hostBtn.setOnClickListener(this);
        joinBtn.setOnClickListener(this);
        singlePlayerBtn.setOnClickListener(this);

        SharedPreferences sharedPreferences=getSharedPreferences("settings.xml", MODE_PRIVATE);
        String player_name = sharedPreferences.getString("player_name", "Player" + random.nextInt());
        playerNameEt.setText(player_name);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    private String playerName;
    @Override
    public void onClick(View v) {
        playerName = playerNameEt.getText().toString();
        SharedPreferences sharedPreferences=getSharedPreferences("settings.xml", MODE_PRIVATE);
        sharedPreferences.edit().putString("player_name", playerName).apply();

        switch (v.getId()){
            case R.id.btn_host_game: {
                startToHostGame();
                break;
            }
            case R.id.btn_join_game: {
                startJoiningGame();
                break;
            }

            case R.id.btn_single_player:{
                startSinglePlayerMode();
                break;
            }
        }
    }

    private void startSinglePlayerMode() {
        Intent intent=new Intent(this, HostGameActivity.class);
        intent.putExtra("game_mode", "single_player");
        intent.putExtra("question_type", getSelectedQuestionType());
        Log.e("SelectedType", getSelectedQuestionType()+"");
        intent.putExtra(ServerRegistrationService.key_name_of_player, playerName);
        startActivity(intent);
    }

    private void startJoiningGame() {
        Intent intent=new Intent(this, JoinGameActivity.class);
        intent.putExtra(ServerRegistrationService.key_name_of_player, playerName);
        startActivity(intent);
    }

    private void startToHostGame() {
        Intent intent=new Intent(this, HostGameActivity.class);
        intent.putExtra("game_mode", "multi_player");
        intent.putExtra("question_type", getSelectedQuestionType());
        intent.putExtra(ServerRegistrationService.key_name_of_player, playerName);
        startActivity(intent);
    }

    public int getSelectedQuestionType(){
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        int questionType;
        switch (checkedRadioButtonId){
            case R.id.antonyms:
                questionType=DatabaseHelper.QuestionType.antonyms;
                break;
            case R.id.word_game:
                questionType=DatabaseHelper.QuestionType.word_game;
                break;
            case R.id.sentence_completion:
                questionType=DatabaseHelper.QuestionType.sentence_completion;
                break;
            default:
                questionType=DatabaseHelper.QuestionType.analogy;

        }
        return questionType;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ResultDisplayActivity.takenAnswers=null;
        ResultDisplayActivity.questionAnswerModels=null;
    }
}
