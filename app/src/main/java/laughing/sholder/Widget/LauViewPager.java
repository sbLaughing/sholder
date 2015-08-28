package laughing.sholder.Widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import laughing.sholder.Activity.MainActivity;

/**
 * Created by LaughingSay on 2015/8/13.
 *
 */
public class LauViewPager extends ViewPager {

    MainActivity.ViewPagerInterceptListener listener;

    public LauViewPager(Context context) {
        super(context);
    }

    public LauViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewPageInterceptListener(MainActivity.ViewPagerInterceptListener lis){
        this.listener = lis;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(listener.getSwitchChecked()){
            return false;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }
}
