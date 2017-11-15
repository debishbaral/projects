package nf.co.mohatram.grescrabble;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;

/**
 * Created by madan on 8/8/17.
 */

public class ResultDisplayFragment extends Fragment {
    private View view;
    private TextView timeTv;
    private TextView noOfCorrectAnswersTv;
    private ListView questionAnswerSolutionLv;
    private List<QuestionAnswerModel> questionAnswerModels;
    private HostGameActivity.TakenAnswer takenAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.information_fragment, null);
        timeTv= (TextView) view.findViewById(R.id.time_taken_tv);
        questionAnswerSolutionLv= (ListView) view.findViewById(R.id.list_question_answer_given_lv);
        noOfCorrectAnswersTv= (TextView) view.findViewById(R.id.no_of_correct_answers_tv);
        questionAnswerSolutionLv= (ListView) view.findViewById(R.id.list_question_answer_given_lv);
        bindContents();
        return view;
    }



    public void addContent(List<QuestionAnswerModel> questionAnswerModels, HostGameActivity.TakenAnswer takenAnswer){

        /*int noOfCorrectAnswers=0;

        for (AnswerModel am: takenAnswer.answerModels){
            if (am.correct) noOfCorrectAnswers++;
        }

        noOfCorrectAnswersTv.setText("Correct answers: "+noOfCorrectAnswers);
        nameTv.setText("Name: "+takenAnswer.playerName);
        nameTv.setVisibility(View.GONE);
        timeTv.setText("Time: "+(takenAnswer.time/1000)/60+":"+(takenAnswer.time/1000)%60+"SS");

        QuestionListAdaptor questionListAdaptor=new QuestionListAdaptor(getContext(), questionAnswerModels, takenAnswer);
        questionAnswerSolutionLv.setAdapter(questionListAdaptor);*/
        this.questionAnswerModels = questionAnswerModels;
        this.takenAnswer = takenAnswer;
    }

    public void bindContents(){
        int noOfCorrectAnswers=0;

        for (AnswerModel am: takenAnswer.answerModels){
            if (am.correct) noOfCorrectAnswers++;
        }

        noOfCorrectAnswersTv.setText("Correct answers: "+noOfCorrectAnswers);
        timeTv.setText("Time: "+(takenAnswer.time/1000)/60+":"+(takenAnswer.time/1000)%60+"s");

        Log.e("Adaptor added", "question adaptor");
        QuestionListAdaptor questionListAdaptor=new QuestionListAdaptor(getContext(), questionAnswerModels, takenAnswer);
        questionAnswerSolutionLv.setAdapter(questionListAdaptor);
        questionListAdaptor.notifyDataSetChanged();
    }
}
