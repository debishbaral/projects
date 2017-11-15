package nf.co.mohatram.grescrabble;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;


public class QuestionSwitcher extends ViewSwitcher {
    public boolean buttonClickable = true;
    private List<QuestionAnswerModel> questionAnswers;
    private List<AnswerModel> givenAnswerModelList;
    private int noOfQuestions = 10;
    private int currentQuestion = 0; //starts with 1
    private QuestionListener questionListener;


    public QuestionSwitcher(Context context, List<QuestionAnswerModel> questionAnswers) {
        super(context);
        givenAnswerModelList = new ArrayList<>();
        this.questionAnswers = questionAnswers;
        initialize();
    }


    public int getNoOfQuestions() {
        return noOfQuestions;
    }

    public void setNextQuestionAnswer(QuestionAnswerModel questionAnswer) {
        Log.e("Next answer", "Called next answer"+ currentQuestion);

        View view = getNextView();
        TextView questionView = (TextView) view.findViewById(R.id.question_tv);
        if(questionView==null)Log.e("NullView", "Nullview");

        questionView.setText(questionAnswer.getQuestion());

        ListView answers = (ListView) view.findViewById(R.id.answer_list_tv);
        AnswerAdaptor adapter = (AnswerAdaptor) answers.getAdapter();
        if(adapter==null){
            adapter=new AnswerAdaptor(getContext());
            answers.setAdapter(adapter);
        }
        adapter.setAnswerModels(questionAnswer.getAnswers());
        adapter.notifyDataSetChanged();
        buttonClickable = true;
        showNext();
    }

    private void initialize() {
        Log.e("Initialize", "Initialize called");
        this.setInAnimation(getContext(), R.anim.slide_right_in);
        this.setOutAnimation(getContext(), R.anim.slide_right_exit);
        this.noOfQuestions = questionAnswers.size();
        currentQuestion = 0;
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = layoutInflater.inflate(R.layout.question_view, null);
        View v2 = layoutInflater.inflate(R.layout.question_view, null);
        addView(v1);
        addView(v2);
    }

    public void next() {
        Log.e("Next", "next: is executing"+currentQuestion );
        currentQuestion++;
        if (currentQuestion > noOfQuestions) {
            if (questionListener != null) {
                questionListener.onQuestionFinished(questionAnswers, givenAnswerModelList);
            }
            return;
        }
        setNextQuestionAnswer(questionAnswers.get(currentQuestion - 1));
    }


    private void animateAnswer(View clickedView) {
        final View animationBtn = clickedView;
        int colorTo = Color.argb(255, 19, 199, 0); //red
        int colorFrom = Color.argb(255, 199, 19, 0); //green
        final ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimator.setDuration(300);

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationBtn.setBackgroundColor((Integer) colorAnimator.getAnimatedValue());
            }
        });
        colorAnimator.addListener(initializeForDrawingNextView());
        colorAnimator.start();
    }

    private Animator.AnimatorListener initializeForDrawingNextView() {

        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                next();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }


    public void executeQuestionFinishedHandler() {
        currentQuestion = noOfQuestions + noOfQuestions;
        next();
        Log.e("Time", "Time finished");
    }


    public void onItemClick(View v, int position) {
        View currentView = getCurrentView();
        ListView lv= (ListView) currentView.findViewById(R.id.answer_list_tv);
        AnswerModel item = (AnswerModel) lv.getAdapter().getItem(position);
        QuestionAnswerModel current=questionAnswers.get(currentQuestion-1);
        if (questionListener!=null){
            questionListener.onAnswerGiven(current, item);
        }
        givenAnswerModelList.add(item);
        animateAnswer(v);
    }

    public void setQuestionListener(QuestionListener questionListener) {
        this.questionListener = questionListener;
    }


    public interface QuestionListener {
        void onAnswerGiven(QuestionAnswerModel questionAnswerModel, AnswerModel givenAnswer);
        void onQuestionFinished(List<QuestionAnswerModel> questionAnswerModels, List<AnswerModel> givenAnswers);
    }

    public class AnswerAdaptor extends BaseAdapter {
        private List<AnswerModel> answerModels;
        private Context context;

        public AnswerAdaptor(Context context) {
            this.context = context;
            answerModels = new ArrayList<>();
        }

        public void setAnswerModels(List<AnswerModel> answerModels) {
            this.answerModels.clear();
            for (AnswerModel am : answerModels) {
                this.answerModels.add(am);
            }
        }

        @Override
        public int getCount() {
            return answerModels.size();
        }

        @Override
        public AnswerModel getItem(int position) {
            return answerModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final Button button = new Button(context);
            button.setFocusable(false);
            button.setText(getItem(position).answer);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuestionSwitcher.this.onItemClick( button, position);
                }
            });
            return button;
        }
    }
}
