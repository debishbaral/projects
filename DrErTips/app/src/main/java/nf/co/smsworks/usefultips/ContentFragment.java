package nf.co.smsworks.usefultips;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by madan on 6/23/17.
 */

public class ContentFragment extends Fragment {

    private View mainView;
    private LayoutInflater layoutInflater;


    TipsXMLModel.Title title;
    TipsXMLModel xmlModel;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        InnerActivity innerActivity= (InnerActivity) activity;
        title=innerActivity.tipsXMLModel.titleList.get(innerActivity.currentPosition);
        xmlModel=innerActivity.tipsXMLModel;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.content_fragment, null);
        this.layoutInflater=inflater;
        setTitleModel(title);
        View shareBtn = mainView.findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent=new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, makeClipBoardText());
                sendIntent.setType("text/plain");
                getActivity().startActivity(sendIntent);
            }
        });

        View cpy_btb = mainView.findViewById(R.id.copy_btn);
        cpy_btb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData tips = ClipData.newPlainText("Tips", makeClipBoardText());
                clipBoard.setPrimaryClip(tips);
                Toast.makeText(getActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        return mainView;
    }


    StringBuffer stringBuffer=new StringBuffer();
    private String makeClipBoardText(){
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append("=>").append(xmlModel.name).append("\n").append("=>").append(title.desc).append("\n");
        for (String tips: title.tipsList){
            stringBuffer.append(tips);
        }

        stringBuffer.append("\n").append("Get complete tips at: \n").append("https://play.google.com/store/apps/details?id=nf.co.smsworks.drertips");
        return stringBuffer.toString();
    }

    public void setTitleModel(TipsXMLModel.Title titleModel){
        ViewGroup imagesCollection = (ViewGroup) mainView.findViewById(R.id.images_collection);
        ViewGroup tipsCollection= (ViewGroup) mainView.findViewById(R.id.tips_container);
        TextView tipsTitle = (TextView) mainView.findViewById(R.id.tips_title);
        imagesCollection.removeAllViews();
        tipsCollection.removeAllViews();

        tipsTitle.setText(titleModel.desc.trim());

        for (String image: titleModel.images){
            View inflate = layoutInflater.inflate(R.layout.image_view, null);
            try {
                ((ViewGroup) inflate.getParent()).removeView(inflate);
            }catch (Exception e){}

            Picasso.with(getContext()).load("file:///android_asset/"+image).error(R.drawable.ic_launcher).into(((ImageView)inflate.findViewById(R.id.image_view)));
            imagesCollection.addView(inflate);
        }

        for (String tip: titleModel.tipsList){
            View inflate = layoutInflater.inflate(R.layout.text_view, null);
            try {
                ((ViewGroup) inflate.getParent()).removeView(inflate);
            }catch (Exception e){}

            ((TextView)inflate.findViewById(R.id.text_view)).setText(tip.trim());
            tipsCollection.addView(inflate);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
