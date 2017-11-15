package nf.co.mohatram.grescrabble;

import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.util.List;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;

/**
 * Created by madan on 6/30/17.
 */

public interface IQuestionServer{
    void mInitialized(ServiceInfo serviceInfo);
    boolean areAllClientsActive();
    List<QuestionAnswerModel> getQuestionAnswerModels();
    boolean answerGivenByAll();

    void takeAnswer(List<AnswerModel> answerModels, long time);

    List<HostGameActivity.TakenAnswer> requestAnswer();

    int getTimeForEachQuestion();
}
