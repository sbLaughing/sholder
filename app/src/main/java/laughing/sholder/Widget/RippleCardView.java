package laughing.sholder.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import laughing.sholder.Adapter.RippleAnimatorAdapter;

/**
 * Created by LaughingSay on 2015/8/12.
 */
public class RippleCardView extends CardView {

    private boolean showRipple = true;
    private RippleAnimatorAdapter rippleAnimator;

    public RippleCardView(Context context) {
        super(context);
    }

    public RippleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
