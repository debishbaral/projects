package nf.co.smsworks.usefultips;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by madan on 6/23/17.
 */

public class TipsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private OnItemClickListener onItemClickListener;

    public ImageView tipsImage;
    public TextView tipsTitle;
    public TextView tipsDescription;

    public TipsViewHolder(View itemView) {
        super(itemView);
        tipsImage= (ImageView) itemView.findViewById(R.id.tips_image);
        tipsTitle= (TextView) itemView.findViewById(R.id.tips_title);
        tipsDescription= (TextView) itemView.findViewById(R.id.tips_desc);
        itemView.setOnClickListener(this);
    }

    public TipsViewHolder(View itemView, OnItemClickListener onItemClickListener){
        this(itemView);
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener!=null){
            onItemClickListener.onItemClicked(v, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClicked(View v, int position);
    }

}
