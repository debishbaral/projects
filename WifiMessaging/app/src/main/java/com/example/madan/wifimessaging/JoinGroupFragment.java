package com.example.madan.wifimessaging;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by madan on 1/5/17.
 */
public class JoinGroupFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    public static ArrayList<NameIpPair> groupList = new ArrayList<>();
    private GroupListAdaptor groupListAdaptor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("madan", "Working");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent intent = new Intent(getActivity(), WmsDiscoveryService.class);
        context.startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_group_fragment, null);
        listView = (ListView) view.findViewById(R.id.join_group_list_container);
        groupListAdaptor = new GroupListAdaptor(groupList, getActivity());

        listView.setAdapter(groupListAdaptor);
        listView.setOnItemClickListener(this);
        return view;
    }

    public void onBackPressed() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((MainActivity) getActivity()).showFabMenu();
                MainActivity.MainActivityState.joinGroupFragmentActive=false;
                ((MainActivity) getActivity()).removeFragment(JoinGroupFragment.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        getView().startAnimation(animation);
    }

    public static void onServiceResolved(NsdServiceInfo serviceInfo) {
        String serviceName = serviceInfo.getServiceName();
        groupList.add(new NameIpPair(serviceName, serviceInfo.getHost().getHostAddress()));
    }

    public static void onServiceLost(NsdServiceInfo serviceInfo) {
        String name = serviceInfo.getServiceName();

        int i = 0;
        for (NameIpPair nameIpPair : groupList) {
            if (nameIpPair.name.equals(name)) {
                break;
            }
            i++;
        }
        groupList.remove(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        groupList.clear();
        if(Global.wmsDiscoveryService!=null) Global.wmsDiscoveryService.stopSelf();
        MainActivity.MainActivityState.joinGroupFragmentActive=false;
    }

    public void updateGroupList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupListAdaptor.notifyDataSetChanged();
                listView.invalidate();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        NameIpPair item = groupListAdaptor.getItem(i);
        final String address=item.hostAddress;
        Setting.GROUP_NAME=item.name;
        ((MainActivity)getActivity()).getLooperThread().getHandler().post(new Runnable() {
            @Override
            public void run() {
                String groupName=groupListAdaptor.getItem(i).name;
                ((MainActivity)getActivity()).joinGroup(address, MainActivity.SERVER_PORT, getActivity(), groupName);
            }
        });
        onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.MainActivityState.joinGroupFragmentActive=false;
    }

    public static class NameIpPair {
        public String name;
        public String hostAddress;

        public NameIpPair(String name, String hostAddress) {
            this.name = name;
            this.hostAddress = hostAddress;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).closeAndHideFabMenu();
    }
}
