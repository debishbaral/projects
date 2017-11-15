package nf.co.mohatram.grescrabble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.mads.iotapipub.discovery.serviceinfo.ServiceInfo;

import java.util.List;

/**
 * Created by madan on 6/29/17.
 */

public class NameListAdaptor extends BaseAdapter {
    private final Context context;
    private final List<ServiceInfo> serviceInfos;

    public String getKey() {
        return key;
    }

    private String key;

    public NameListAdaptor(Context context, List<ServiceInfo> serviceInfos, String key){
        this.context = context;
        this.serviceInfos = serviceInfos;
        this.key = key;
    }
    @Override
    public int getCount() {
        return serviceInfos.size();
    }

    @Override
    public ServiceInfo getItem(int position) {
        return serviceInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return serviceInfos.get(position).hashCode();
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View convertView=layoutInflater.inflate(R.layout.name_list_row, null);

        TextView textView= (TextView) convertView.findViewById(R.id.name_tv);
        textView.setText(getItem(position).get(key));
        return convertView;
    }
}
