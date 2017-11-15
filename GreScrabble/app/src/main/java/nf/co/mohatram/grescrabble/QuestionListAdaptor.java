package nf.co.mohatram.grescrabble;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;

/**
 * Created by madan on 8/8/17.
 */

public class QuestionListAdaptor extends BaseAdapter {

    private Context context;
    private List<QuestionAnswerModel> questionAnswerModels;
    private HostGameActivity.TakenAnswer takenAnswer;

    public QuestionListAdaptor(Context context, List<QuestionAnswerModel> questionAnswerModels, HostGameActivity.TakenAnswer takenAnswer){
        this.context = context;
        this.questionAnswerModels = questionAnswerModels;
        Log.e("Running", questionAnswerModels.size()+"");
        this.takenAnswer = takenAnswer;
    }
    @Override
    public int getCount() {
        return questionAnswerModels.size();
    }

    @Override
    public Object getItem(int position) {
        return questionAnswerModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);

        View  convertView = layoutInflater.inflate(R.layout.question_answer_list, null);

        TextView questionTv= (TextView) convertView.findViewById(R.id.question_tv);
        TextView correctAnswerTv= (TextView) convertView.findViewById(R.id.correct_answer_tv);
        TextView givenAnswerTv= (TextView) convertView.findViewById(R.id.given_answer_tv);

        QuestionAnswerModel questionAnswerModel = questionAnswerModels.get(position);

        AnswerModel givenAnswer;
        try {
            givenAnswer = takenAnswer.answerModels.get(position);
        }catch (Exception e){
            givenAnswer=new AnswerModel();
            givenAnswer.answer="none";
            givenAnswer.correct=false;
        }
        String question=questionAnswerModel.getQuestion();
        String correctAnswer = questionAnswerModel.getCorrectAnswer().answer;
        String givenAnswerS=givenAnswer.answer;

        questionTv.setText(question);
        correctAnswerTv.setText(correctAnswer);
        givenAnswerTv.setText(givenAnswerS);

        if (givenAnswer.correct) {
            givenAnswerTv.setVisibility(View.GONE);
        }

        return convertView;
    }
}
