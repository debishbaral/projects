package com.example.madan.wifimessaging;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Created by madan on 1/5/17.
 */

public class MessageAdaptor extends RecyclerView.Adapter<RecyclerViewHolder> {
    public static final int ME=0;
    public static final int YOU=1;
    public Context context;
    public ArrayList<MessageComponents> messageComponents;

    public MessageAdaptor(Context context, ArrayList<MessageComponents> messageComponents) {
        this.context = context;
        this.messageComponents = messageComponents;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(viewType==YOU)view = layoutInflater.inflate(R.layout.message_row, parent, false);
        else view=layoutInflater.inflate(R.layout.message_row_me, parent, false);

        RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(view, context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.icon.setImageDrawable(messageComponents.get(position).drawable);
        holder.name.setText(messageComponents.get(position).name);
        holder.message.setText(messageComponents.get(position).message);

        //setAnimation(holder.container, position);
    }

    private void setAnimation(View container, int position) {
        if(position<messageComponents.size() && position>=0){
            Animation animation;
            if(getItemViewType(position)==YOU){
                animation=AnimationUtils.loadAnimation(context,R.anim.slide_in_right);
            }else {
                animation=AnimationUtils.loadAnimation(context,R.anim.slide_in_left);
            }
            animation.setInterpolator(new DecelerateInterpolator());

            container.startAnimation(animation);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageComponents components=messageComponents.get(position);
        String name=messageComponents.get(position).name;
        int id=messageComponents.get(position).id;
        if(name.contains(Setting.USER_NAME) && id==Setting.USER_ID){
            return ME;
        }else {
            return YOU;
        }
    }

    @Override
    public int getItemCount() {
        return messageComponents.size();
    }
}
