package nf.co.mohatram.hidingdot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

/**
 * Created by madan on 4/29/17.
 */

public class RotatingCircularButton extends CircleButton {
    public RotatingCircularButton(Context context) {
        super(context);
        init();
    }

    public RotatingCircularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotatingCircularButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init(){
        animating=true;
        final Animation clkWise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clkwise);
        clkWise.setInterpolator(new OvershootInterpolator());
        final Animation antiClkWise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anti_clkwise);
        antiClkWise.setInterpolator(new OvershootInterpolator());
        Animation.AnimationListener animationListenerClkwise=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAnimating())RotatingCircularButton.this.startAnimation(antiClkWise);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation.AnimationListener animationListenerAntiClkwise=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAnimating())RotatingCircularButton.this.startAnimation(clkWise);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        clkWise.setAnimationListener(animationListenerClkwise);
        antiClkWise.setAnimationListener(animationListenerAntiClkwise);

        this.startAnimation(antiClkWise);
    }

    private boolean animating;

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }
}
