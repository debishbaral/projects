package com.example.madan.wifimessaging;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by madan on 1/5/17.
 */

public class GroupListAdaptor extends BaseAdapter {
    private ArrayList<JoinGroupFragment.NameIpPair> groupInfo;
    private Context context;

    public GroupListAdaptor(ArrayList<JoinGroupFragment.NameIpPair> groupInfo, Context context) {
        this.groupInfo = groupInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return groupInfo.size();
    }

    @Override
    public JoinGroupFragment.NameIpPair getItem(int i) {
        Log.e("Logger_bi", groupInfo.get(i).name);
        return groupInfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        try {
            return groupInfo.get(i).hashCode();
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
    }

    private Random random = new Random();

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.group_name_row, null);
        }

        int pos=getItem(i).name.indexOf(WmsAdvertisingService.SERVICE_CONTENT);
        String name=getItem(i).name.substring(0, pos);

        ImageView icon = (ImageView) view.findViewById(R.id.drawable_icon_name);
        TextDrawable drawable = getTextDrawable(context, name, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        icon.setImageDrawable(drawable);

        TextView nameView= (TextView) view.findViewById(R.id.name_of_group);
        nameView.setText(name);
        return view;

    }

    public static TextDrawable getTextDrawable(Context context, String name, int r, int g, int b) {
        if (name == null || name.length() <= 2) {
            name = name + "  ";
        }
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .fontSize((int) MainActivity.dpToPixel(context, 20))
                .bold()
                .toUpperCase()
                .width((int) MainActivity.dpToPixel(context, 48))
                .height((int) MainActivity.dpToPixel(context, 48))
                .endConfig()
                .buildRound(name.substring(0, 2), Color.rgb(r, g, b));
        return drawable;
    }
}
