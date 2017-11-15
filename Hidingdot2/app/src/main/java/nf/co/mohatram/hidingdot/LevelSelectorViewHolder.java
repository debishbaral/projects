package nf.co.mohatram.hidingdot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by madan on 4/30/17.
 */

public class LevelSelectorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvLevelNo;
    public ImageView ivLocked;
    public ImageView ivStarCount;
    public LevelSelectorViewHolder(View itemView) {
        super(itemView);
        tvLevelNo= (TextView) itemView.findViewById(R.id.tv_level_no);
        ivLocked=(ImageView) itemView.findViewById(R.id.iv_lock_icon);
        ivStarCount= (ImageView) itemView.findViewById(R.id.iv_stars_count);
        itemView.setOnClickListener(this);
    }

    public LevelSelectorViewHolder(View v, OnItemClickListener onItemClickListener){
        this(v);
        this.onItemClickListener=onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener!=null) onItemClickListener.onItemClicked(v, getAdapterPosition());
    }

    private OnItemClickListener onItemClickListener;


    /*public void animateUnlock(Animation animation) {
        if (tvLevelNo.getVisibility()!=View.VISIBLE) tvLevelNo.setVisibility(View.VISIBLE);
        ivLocked.startAnimation(animation);
    }*/

    public interface OnItemClickListener{
        void onItemClicked(View v, int position);
    }

}
