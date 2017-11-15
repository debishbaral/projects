package nf.co.mohatram.grescrabble;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.mads.iotapipub.discovery.listeners.IDiscoveryListener;
import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class JoinGameActivity extends AppCompatActivity implements IDiscoveryListener, AdapterView.OnItemClickListener {

    private ClientDiscoveryService clientDiscoveryService;
    private ListView hostList;
    private NameListAdaptor hostListAdaptor;
    private List<ServiceInfo> serviceInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        serviceInfos = new ArrayList<>();
        clientDiscoveryService = new ClientDiscoveryService(getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
        hostList = (ListView) findViewById(R.id.hosList_lv);
        hostListAdaptor = new NameListAdaptor(this, serviceInfos, ServerRegistrationService.key_name_of_player);

        hostList.setAdapter(hostListAdaptor);
        hostList.setOnItemClickListener(this);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clientDiscoveryService.startDiscovery(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clientDiscoveryService.stopDiscovery(this);
    }

    @Override
    public void onStartDiscoveryFailed(ServiceInfo serviceInfo, int i, Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JoinGameActivity.this, "Unable to start discovery.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onStopDiscoveryFailed(ServiceInfo serviceInfo, int i, Exception e) {

    }

    @Override
    public void onStartDiscoverySuccess(ServiceInfo serviceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JoinGameActivity.this, "Searching for host.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStopDiscoverySuccess(ServiceInfo serviceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JoinGameActivity.this, "Stopping host search", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onDiscoverAttempt(ServiceInfo serviceInfo, int i) {

    }

    @Override
    public void onServiceFound(List<ServiceInfo> list, ServiceInfo serviceInfo) {
        serviceInfos.add(serviceInfo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hostListAdaptor.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCorruptedServiceFound(ServiceInfo serviceInfo, @Nullable Exception e) {

    }

    @Override
    public void onDiscoveryFailed(ServiceInfo serviceInfo, int i, @Nullable Exception e) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ServiceInfo serviceInfo = serviceInfos.get(position);
        String address = serviceInfo.getSocketDetails().address;

        Intent intent=new Intent(this, QuestionViewerActivity.class);
        intent.putExtra("address", address);
        intent.putExtra(ServerRegistrationService.key_name_of_player, getIntent().getStringExtra(ServerRegistrationService.key_name_of_player));
        startActivity(intent);
    }
}
