package nf.co.smsworks.usefultips;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by madan on 6/23/17.
 */

public class TipsAdaptor extends RecyclerView.Adapter<TipsViewHolder>  {
    private LayoutInflater layoutInflater;
    private List<ListModel> listModels;
    private Context context;

    public TipsAdaptor(Context context, List<ListModel> listModels){
        layoutInflater=LayoutInflater.from(context);
        this.context = context;
        this.listModels = listModels;
    }

    @Override
    public TipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.tips_list_even, null);
        return new TipsViewHolder(view, (TipsViewHolder.OnItemClickListener) context);
    }

    @Override
    public void onBindViewHolder(TipsViewHolder holder, int position) {
        ListModel listModel=listModels.get(position);
        Picasso.with(context).load("file:///android_asset/"+listModel.image).error(R.drawable.ic_launcher).into(holder.tipsImage);

        holder.tipsDescription.setText(listModel.desc);
        holder.tipsTitle.setText(listModel.title);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

   public ListModel getItem(int position){
        return listModels.get(position);
    }


}
