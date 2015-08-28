package laughing.sholder.Widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import laughing.sholder.Adapter.RippleAnimatorAdapter;

/**
 * Created by LaughingSay on 2015/8/4.
 */
public class RippleButton extends TextView {

    private boolean showRipple = true;
    private RippleAnimatorAdapter rippleAnimator;


    public RippleButton(Context context) {
        super(context);
        init();
    }

    public RippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        rippleAnimator = new RippleAnimatorAdapter(this).setBlackBackground();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (rippleAnimator!=null && showRipple) {
            rippleAnimator.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(rippleAnimator!=null && showRipple){
            rippleAnimator.onMeasure();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(rippleAnimator!=null && showRipple){
            rippleAnimator.onDraw(canvas);
        }
        super.onDraw(canvas);
    }
}
