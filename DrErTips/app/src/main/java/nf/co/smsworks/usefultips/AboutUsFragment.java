package nf.co.smsworks.usefultips;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by madan on 5/9/17.
 */

public class AboutUsFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater systemService = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=systemService.inflate(R.layout.about_frag, null);
        WebView webView= (WebView) view.findViewById(R.id.information);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/info.html");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(browserIntent);
                return true;
            }
        });
        view.findViewById(R.id.close_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                hideFragment();
                ((MainActivity)getActivity()).fragmentActive=false;
                break;
        }
    }

    public void hideFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
