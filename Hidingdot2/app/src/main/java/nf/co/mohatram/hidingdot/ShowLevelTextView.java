package nf.co.mohatram.hidingdot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by madan on 5/4/17.
 */

public class ShowLevelTextView extends TextView {
    public ShowLevelTextView(Context context) {
        super(context);
    }

    public ShowLevelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowLevelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLevel(int level){
        setText("Level "+level);
    }
}
