package com.example.madan.wifimessaging;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.Random;

/**
 * Created by madan on 1/5/17.
 */

public class EditNameFragment extends android.support.v4.app.Fragment implements View.OnClickListener, TextWatcher {
    private ImageView userImageView;
    private EditText userNameInputET;
    private Button saveButton, changeColor;

    private String playerName;
    public int r, g, b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerName=getArguments().getString(getArguments().getString("key"));
        r=getArguments().getInt("RED");
        g=getArguments().getInt("GREEN");
        b=getArguments().getInt("BLUE");
        if(r==-1||g==-1||b==-1) generateNewColor();

        Log.e("Check man", playerName+r+"x"+g+"x"+b);

        //create drawable for the text

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).closeAndHideFabMenu();
        //drawable color
    }

    public void generateNewColor(){
        Random random=new Random();
        r=random.nextInt(255); g=random.nextInt(255); b=random.nextInt(255);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_info_layout, null);
        userImageView= (ImageView) view.findViewById(R.id.user_image_view);
        userNameInputET=(EditText) view.findViewById(R.id.user_name_et);
        userNameInputET.setText(playerName);
        saveButton= (Button) view.findViewById(R.id.user_info_save_btn);
        changeColor= (Button) view.findViewById(R.id.user_info_change_color_btn);
        changeColor.setOnClickListener(this);

        saveButton.setOnClickListener(this);
        userNameInputET.addTextChangedListener(this);
        drawToImageView();

        ViewCompat.setElevation(userImageView, 10);
        ViewCompat.setElevation(userNameInputET, 10);
        ViewCompat.setElevation(saveButton, 10);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_info_save_btn:
                //save information and back it
                ((MainActivity)getActivity()).saveSharedPref(Setting.KEY_FOR_USER_NAME, playerName, "RED", r+"", "GREEN", g+"", "BLUE", b+"");
                onBackPressed();
                break;
            case  R.id.user_info_change_color_btn:
                generateNewColor();
                drawToImageView();
                break;
        }
    }


    public void onBackPressed() {
        Animation animation= AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainActivity.MainActivityState.editNameFragmentActive=false;
                ((MainActivity)getActivity()).showFabMenu();
                ((MainActivity)getActivity()).removeFragment(EditNameFragment.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        getView().startAnimation(animation);
    }

    private void drawToImageView(){
        if(playerName==null||playerName.length()<=2){
            playerName=playerName+"  ";
        }
        TextDrawable drawable=TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .fontSize((int) MainActivity.dpToPixel(getActivity(), 48))
                .bold()
                .toUpperCase()
                .width((int) MainActivity.dpToPixel(getActivity(), 96))
                .height((int) MainActivity.dpToPixel(getActivity(), 96))
                .endConfig()
                .buildRound(playerName.substring(0,2), Color.rgb(r, g, b));
        userImageView.setImageDrawable(drawable);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        playerName= new StringBuffer().append(charSequence).toString();
        drawToImageView();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
