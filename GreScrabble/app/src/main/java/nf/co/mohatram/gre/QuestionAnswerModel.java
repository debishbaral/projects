package nf.co.mohatram.gre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerModel implements Serializable{
    private String question;
    private List<AnswerModel> answers;


    public QuestionAnswerModel(){
        answers=new ArrayList<>();
    }

    public QuestionAnswerModel setQuestion(String question){
        this.question=question;
        return this;
    }

    public QuestionAnswerModel addAnswers(AnswerModel... answerModels){
        for (AnswerModel ans: answerModels) {
            this.answers.add(ans);
        }
        return this;
    }

    public String getQuestion() {
        return question;
    }


    public AnswerModel getAnswer(int pos){
        return answers.get(pos);
    }

    public List<AnswerModel> getAnswers(){
        return answers;
    }

    public AnswerModel getCorrectAnswer(){
        for (AnswerModel answerModel : answers) {
            if (answerModel.correct) {
                return answerModel;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(question).append("\n");
        for (AnswerModel am :
                answers) {
            stringBuilder.append(am.answer).append(", ").append(am.correct).append("\n");
        }

        return stringBuilder.toString();
    }
}

