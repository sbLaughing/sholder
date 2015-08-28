package laughing.sholder.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

import laughing.sholder.Activity.MainActivity;
import laughing.sholder.Adapter.ViewPagerAdapter;
import laughing.sholder.R;
import laughing.sholder.Widget.LauViewPager;
import laughing.sholder.Widget.SlidingTabLayout;

/**
 * Created by LaughingSay on 2015/7/29.
 */
public class ShowChartsFragment extends Fragment {

    private Context mContext;
    private LauViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private View rootView;
    private String[] tabs = new String[]{"全年统计", "支出统计", "收入统计"};
    ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private MainActivity.ViewPagerInterceptListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_showcharts,
                container, false);
        mContext = getActivity().getApplicationContext();
        afterViews();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        resetChildren(false);
    }

    /**
     * 这个方法会在ShowEditFragment返回的时候调用到
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            resetChildren(true);
        }
    }

    public void resetChildren(boolean animation){
        for(Fragment f : mFragments){
            if((f instanceof YearPieChartFragment)&& f.isVisible()){
                ((YearPieChartFragment)f).loadData();
            }else if((f instanceof  YearColChartFragment)&&f.isVisible()){
                ((YearColChartFragment) f).loadData();
            }
        }
        if(animation){
            this.startShowAnimation();
        }
    }

    public ShowChartsFragment init(MainActivity.ViewPagerInterceptListener listener){
        this.listener = listener;
        return this;
    }


    private void afterViews(){
        slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs_charts);
        viewPager = (LauViewPager) rootView.findViewById(R.id.viewpager_charts);
        mFragments.add(new YearColChartFragment());
        mFragments.add(new YearPieChartFragment().init(false));
        mFragments.add(new YearPieChartFragment().init(true));

        viewPager.setAdapter(new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(),
                tabs,
                mFragments
                ));
        viewPager.setViewPageInterceptListener(this.listener);

        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
    }

    public Fragment startShowAnimation() {
        ArrayList<View> list = slidingTabLayout.getTabStripChildren();
        View v = null;

        for (int i = 0; i < list.size(); i++) {
            v = list.get(i);
            v.setScaleX(0);
            v.setScaleY(0);
            v.setPivotX(50);
            v.setPivotY(50);
            v.animate().scaleX(1)
                    .scaleY(1)
                    .setInterpolator(new OvershootInterpolator())
                    .setStartDelay(i * ShowEditFragment.STRIPS_ANIMATION_DURATION / 2)
                    .setDuration(ShowEditFragment.STRIPS_ANIMATION_DURATION);
        }
        return this;
    }
}
