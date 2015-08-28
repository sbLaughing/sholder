package laughing.sholder.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import laughing.sholder.R;
import laughing.sholder.Utils.CommonUtils;

/**
 * Created by LaughingSay on 2015/8/2.18:50
 */
public class RippleAnimatorAdapter {

    private static DecelerateInterpolator mDecelerateInterpolator ;
    private int realDuration;

    private float mRadius;
    private float startRadius;
    private float maxRadius;
    private int viewHeight;
    private int viewWidth;
    private int screenWidth;
    private int screenHeight;
    private Paint mPaint ;
    private int mPaintX;
    private int mPaintY;

    private View mView;
    private Property<RippleAnimatorAdapter,Float> mRadiusProperty;
    private Property<RippleAnimatorAdapter,Integer> mBackgroundColorProperty;
    private ObjectAnimator alphaAnimator;
    private ObjectAnimator radiusAnimator;

    private boolean isAnimating = false;
    private int startColor;
    private int endColor;

    private Context mContext;

    public RippleAnimatorAdapter(View view){
        mView = view;
        mContext = view.getContext();
        startColor = mContext.getResources().getColor(R.color.ripple_white_start);
        endColor = mContext.getResources().getColor(R.color.ripple_white_end);
        mDecelerateInterpolator = new DecelerateInterpolator();
        mPaint = new Paint();
        CommonUtils common = CommonUtils.getInstance();
        screenWidth = common.getScreenWidth(mContext);
        screenHeight = common.getScreenHeight(mContext);



        mRadiusProperty = new Property<RippleAnimatorAdapter, Float>(Float.class, "radius") {
            @Override
            public Float get(RippleAnimatorAdapter object) {
                return object.mRadius;
            }

            @Override
            public void set(RippleAnimatorAdapter object, Float value) {
                object.mRadius = value;
                mView.invalidate();
            }
        };

        mBackgroundColorProperty = new Property<RippleAnimatorAdapter, Integer>(Integer.class,"bg_color") {
            @Override
            public Integer get(RippleAnimatorAdapter object) {
                return object.mPaint.getColor();
            }

            @Override
            public void set(RippleAnimatorAdapter object, Integer value) {
                object.mPaint.setColor(value);
            }
        };
    }

    public RippleAnimatorAdapter setBlackBackground(){
        startColor = mContext.getResources().getColor(R.color.ripple_black_start);
        endColor = mContext.getResources().getColor(R.color.ripple_black_end);
        return this;
    }


    public void onTouchEvent(MotionEvent event){
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:{

                if(!isAnimating){
                    mPaintX = (int) event.getX();
                    mPaintY = (int) event.getY();
                    startAnimation();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:{
            }
            case MotionEvent.ACTION_CANCEL:{
                break;
            }
            case MotionEvent.ACTION_UP:{

                }
                break;
            }
        }



    public void onDraw(final Canvas canvas){
        canvas.save();
        canvas.drawCircle(mPaintX,mPaintY,mRadius,mPaint);
        canvas.restore();
    }


    public void onMeasure(){
        viewHeight = mView.getMeasuredHeight();
        viewWidth = mView.getMeasuredWidth();
        startRadius = Math.min(viewHeight,viewWidth)/4;
        maxRadius = Math.max(viewHeight,viewWidth)*4/5;
        int size = Math.max(viewHeight,viewWidth);
        realDuration = size * 3;

        if(viewWidth / screenWidth < 2){
            realDuration = (int) (size * 1.5);
        }

        if(viewWidth / screenWidth > 0.9){
            realDuration = (int) (size*0.8);
        }


    }

    private void startAnimation(){

        isAnimating = true;
        radiusAnimator = ObjectAnimator.ofFloat(this,
                mRadiusProperty,
                startRadius,
                maxRadius);

        radiusAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });

        alphaAnimator = ObjectAnimator.ofObject(this, mBackgroundColorProperty,
                new ArgbEvaluator(),
                startColor,endColor);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                alphaAnimator,
                radiusAnimator
        );
        set.setDuration(realDuration);
        set.setInterpolator(mDecelerateInterpolator);
        set.start();
    }



}
