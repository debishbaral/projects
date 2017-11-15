package nf.co.mohatram.grescrabble;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nf.co.mohatram.gre.AnswerModel;
import nf.co.mohatram.gre.QuestionAnswerModel;

public class ResultDisplayActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView winnerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_display);

        winnerTextView= (TextView) findViewById(R.id.winner_name_tv);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        viewPager= (ViewPager) findViewById(R.id.pager);

        setWinner();
        tabLayout.setupWithViewPager(viewPager);


        FragmentPageAdaptor fragmentPageAdaptor=new FragmentPageAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPageAdaptor);

        for (HostGameActivity.TakenAnswer takenAnswer: takenAnswers){
            ResultDisplayFragment fragment=new ResultDisplayFragment();
            fragment.addContent(questionAnswerModels, takenAnswer);
            fragmentPageAdaptor.addFragment(fragment, takenAnswer.playerName);
        }
    }

    private void setWinner() {
        List<HostGameActivity.TakenAnswer> commonAnswers=new ArrayList<>();
        int maxCorrect=0;
        for(HostGameActivity.TakenAnswer takenAnswer: takenAnswers){
            int noOfCorrect=0;
            for (AnswerModel am: takenAnswer.answerModels){
                if (am.correct){
                    noOfCorrect++;
                }
            }

            if (noOfCorrect>maxCorrect){
                maxCorrect=noOfCorrect;
                commonAnswers.clear();
                commonAnswers.add(takenAnswer);
            }else if(noOfCorrect==maxCorrect){
                commonAnswers.add(takenAnswer);
            }
        }

        long minTime=Long.MAX_VALUE;
        for (HostGameActivity.TakenAnswer ta: commonAnswers){
            if (ta.time<minTime){
                minTime=ta.time;
            }
        }

        List<HostGameActivity.TakenAnswer> toremove=new ArrayList<>();
        for (HostGameActivity.TakenAnswer ta: commonAnswers){
            if (ta.time>minTime){
                toremove.add(ta);
            }
        }

        for (HostGameActivity.TakenAnswer ta: toremove){
            commonAnswers.remove(ta);
        }

        StringBuilder stringBuilder=new StringBuilder();
        if (commonAnswers.size()>1){
            stringBuilder.append("The winners are\n");
        }else {
            stringBuilder.append("The winner is\n");
        }

        for (int i=0; i<commonAnswers.size(); i++){
            HostGameActivity.TakenAnswer ta=commonAnswers.get(i);
            if (i==0){
                stringBuilder.append(ta.playerName);
            }else if (i<commonAnswers.size()-1){
                stringBuilder.append(", ").append(ta.playerName);
            }else {
                stringBuilder.append(" and ").append(ta.playerName);
            }

        }

        winnerTextView.setText(stringBuilder.toString());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public static class FragmentPageAdaptor extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public FragmentPageAdaptor(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public static List<HostGameActivity.TakenAnswer> takenAnswers;
    public static List<QuestionAnswerModel>  questionAnswerModels;
}
