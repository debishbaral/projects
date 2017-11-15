package nf.co.smsworks.usefultips;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class InnerActivity extends AppCompatActivity implements TipsViewHolder.OnItemClickListener{

    private ListModel listModel=new ListModel();
    public TipsXMLModel tipsXMLModel=new TipsXMLModel();

    private RecyclerView recyclerView;

    private TipsAdaptor tipsAdaptor;
    private List<ListModel> modelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner);

        listModel.decodeFromIntent(getIntent());
        makeTipsXMLModel();
        tipsXMLModel.logCat();
        initViews();
    }

    private void initViews() {
        /*Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_inner);
        setSupportActionBar(toolbar);*/
        recyclerView= (RecyclerView) findViewById(R.id.rv_inner_ac_list);
        modelList=new ArrayList<>();
        tipsXMLModel.getTitleListModels(modelList);
        tipsAdaptor=new TipsAdaptor(this, modelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tipsAdaptor);

        contentFragment=new ContentFragment();

        getSupportActionBar().setTitle(listModel.title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void makeTipsXMLModel() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(getAssets().open(listModel.fileName));
            tipsXMLModel.decodeFromDOM(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ContentFragment contentFragment;
    @Override
    public void onItemClicked(View v, int position) {

        if (fragmentActive){
            onBackPressed();
        }
        currentPosition=position;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).add(R.id.fragment_container_a_inner, contentFragment).commit();
        fragmentActive=true;
    }

    public int currentPosition=0;

    public boolean fragmentActive=false;
    @Override
    public void onBackPressed() {
        if (!fragmentActive)super.onBackPressed();
        else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).remove(contentFragment).commit();
            fragmentActive=false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
        System.gc();
    }
}
