package nf.co.mohatram.hidingdot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madan on 4/30/17.
 */

public class LevelSelectorAdaptor extends RecyclerView.Adapter<LevelSelectorViewHolder> implements LevelSelectorViewHolder.OnItemClickListener{

    public List<LevelModel> levelModels;
    private LayoutInflater layoutInflater;
    private Context context;


    public LevelSelectorAdaptor(Context context){
        layoutInflater=LayoutInflater.from(context);
        this.context = context;
        levelModels = new ArrayList<>();
    }

    @Override
    public LevelSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.cell_grid, null);
        return new LevelSelectorViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(LevelSelectorViewHolder holder, int position) {
        LevelModel levelModel = levelModels.get(position);
        if (levelModel.levelLocked){
            holder.ivLocked.setVisibility(View.VISIBLE);
            holder.ivStarCount.setVisibility(View.VISIBLE);
            holder.tvLevelNo.setVisibility(View.INVISIBLE);
            holder.tvLevelNo.setText((position+1)+"");
            holder.ivStarCount.setImageResource(R.drawable.star0);
            return;
        }else {
            holder.ivLocked.setVisibility(View.INVISIBLE);
            holder.ivStarCount.setVisibility(View.VISIBLE);
            holder.tvLevelNo.setVisibility(View.VISIBLE);

            holder.tvLevelNo.setText((position+1)+"");

            switch (levelModel.noOfStars){
                case 0:
                    holder.ivStarCount.setImageResource(R.drawable.star0);
                    break;
                case 1:
                    holder.ivStarCount.setImageResource(R.drawable.star1);
                    break;
                case 2:
                    holder.ivStarCount.setImageResource(R.drawable.star2);
                    break;
                case 3:
                    holder.ivStarCount.setImageResource(R.drawable.star3);
                    break;
                default:
                    holder.ivStarCount.setImageResource(R.drawable.star0);
            }
        }

    }


    @Override
    public int getItemCount() {
        return levelModels.size();
    }

    LevelModel getItem(int position){
        try {
            return levelModels.get(position);
        }catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        if(MusicService.get()!=null)MusicService.get().playOnBtnClick();
        LevelModel selectorModel = getItem(position);

        if (selectorModel.levelLocked){
            Toast.makeText(context, "Level locked", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(context, GameActivity.class);
        selectorModel.encodeToIntent(intent);
        ((Activity)context).startActivityForResult(intent, LevelSelectorActivity.REQUEST_CODE);
        if(selectorModel.levelNo< 5 ) {
            Toast.makeText(context, "Move the blinking dot to blue box.", Toast.LENGTH_LONG).show();
        }
        /*Log.e("level selected", selectorModel.levelFileName);*/
    }

    LevelModel getItemWithId(int levelid) {
        for (LevelModel levelModel :
                levelModels) {
            if (levelModel.levelId==levelid) return levelModel;
        }
        return null;
    }
}
