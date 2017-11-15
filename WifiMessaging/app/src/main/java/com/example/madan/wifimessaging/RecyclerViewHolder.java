package com.example.madan.wifimessaging;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by madan on 1/5/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
   public ImageView icon;
    public TextView message;
    public TextView name;
    public View container;

    public RecyclerViewHolder(View itemView, Context context) {
        super(itemView);
        container=itemView;
        icon= (ImageView) itemView.findViewById(R.id.icon);
        message= (TextView) itemView.findViewById(R.id.message_tv);
        name=(TextView) itemView.findViewById(R.id.name_tv);
    }
}
