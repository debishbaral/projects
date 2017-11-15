package nf.co.mohatram.hidingdot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by madan on 4/29/17.
 */

public class ZoomingAnimationBtn extends CircleButton {
    public ZoomingAnimationBtn(Context context) {
        super(context);
        init();
    }

    public ZoomingAnimationBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomingAnimationBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init(){
        final Animation zoomInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in_animation);
        final Animation zoomOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out_animation);

        final Animation.AnimationListener zoomOut=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ZoomingAnimationBtn.this.startAnimation(zoomInAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation.AnimationListener zoomIn=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ZoomingAnimationBtn.this.startAnimation(zoomOutAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        zoomInAnimation.setAnimationListener(zoomIn);
        zoomOutAnimation.setAnimationListener(zoomOut);

        this.startAnimation(zoomInAnimation);
    }
}
