package laughing.sholder.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Switch;

import java.util.ArrayList;

import laughing.sholder.Activity.MainActivity;
import laughing.sholder.Adapter.ViewPagerAdapter;
import laughing.sholder.Bean.BillBean;
import laughing.sholder.R;
import laughing.sholder.Utils.CommonUtils;
import laughing.sholder.Widget.SlidingTabLayout;

/**
 * Created by LaughingSay on 2015/7/29.
 *
 */
public class ShowEditFragment extends Fragment {

    public static final int STRIPS_ANIMATION_DURATION = 200;

    private Context mContext;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout slidingTabLayout;
    private com.rey.material.widget.Switch rightSwitch;
    private MainActivity.OnEditFragmentBack editBackListener;
    private NewBillFragment tempFragment;
    private View leftBtn;
    private ArrayList<Fragment> mFragments;

    /**
     *
     */
    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();
    private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();

    /**
     * Listener
     */
//    private AnimatorListenerAdapter switchEndListener;

    private String[] tabs = new String[]{"支出"/*, "收入", "转账"*/};

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_showedit,
                container, false);
        mContext = getActivity().getApplicationContext();
        afterViews();
        return rootView;
    }


    private void afterViews() {

        slidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs_edit);
        rightSwitch = (Switch) rootView.findViewById(R.id.right_switch);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager_edit);
        TextView tv = (TextView) rootView.findViewById(R.id.temp_tv);
        rootView.findViewById(R.id.editbar_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.closeSoftInput(mContext,v);
                if (adapter.getItem(viewPager.getCurrentItem()) instanceof NewBillFragment) {
                    tempFragment = (NewBillFragment) adapter.getItem(viewPager.getCurrentItem());
                    int x = (v.getRight() + v.getLeft()) / 2;
                    int y = (v.getTop() + v.getBottom() / 2);
                    BillBean tempBill;
                    if ((tempBill = tempFragment.saveBill())!=null) {
                        editBackListener.onBack(x, y,tempBill);
                    }else{
                        Toast.makeText(mContext,"请输入金额",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        leftBtn = rootView.findViewById(R.id.editbar_close);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.closeSoftInput(mContext,v);
                int x = (v.getRight() + v.getLeft()) / 2;
                int y = (v.getTop() + v.getBottom() / 2);
                editBackListener.onBack(x, y,null);
            }
        });
        rightSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                showChangeResumeAnimation(0,b);
                ((NewBillFragment)mFragments.get(0)).onSwitchStateChanged(b);
            }
        });

        configListener();



        mFragments= new ArrayList<Fragment>();
        mFragments.add(new NewBillFragment().init(false, null));
//        mFragments.add(new NewBillFragment().init(true, null));
//        mFragments.add(new NewBillFragment().init(true,null));


        adapter = new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(),
                tabs,
                mFragments);
        viewPager.setAdapter(adapter);

        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        startShowAnimation();
    }

    public void setEditBackListener(MainActivity.OnEditFragmentBack lis) {
        this.editBackListener = lis;
    }

    private void configListener(){
//        switchEndListener = new AnimatorListenerAdapter(){
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                slidingTabLayout.changeTabTitle(index ,title);
//                tempView.animate().scaleX(1).scaleY(1).setDuration(STRIPS_ANIMATION_DURATION)
//                        .setInterpolator(mOvershootInterpolator)
//                        .start();
//
//            }
//        };
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
                    .setStartDelay(i * STRIPS_ANIMATION_DURATION / 2)
                    .setDuration(STRIPS_ANIMATION_DURATION);
        }
        return this;
    }

    public void performBack(){
        this.leftBtn.performClick();
    }

    public void resetFragment(){
        rightSwitch.setChecked(false);
        for(Fragment f : mFragments){
            if(f instanceof NewBillFragment){
                ((NewBillFragment)f).reset();
            }
        }

    }

    public void showChangeResumeAnimation(int index, boolean checked){
        String title = null;
        if(checked){
            title = "收入";
        }else{
            title = "支出";
        }
        slidingTabLayout.changeTabTitle(index ,title);
//        tempView = slidingTabLayout.getTabStripChildren().get(index);
//        this.index = index;
//        this.title = title;
//        tempView.setPivotY(50);
//        tempView.setPivotX(50);
//        tempView.animate().scaleX(0).scaleY(0)
//                .setDuration(STRIPS_ANIMATION_DURATION)
//                .setInterpolator(mDecelerateInterpolator)
//                .setListener(switchEndListener)
//                .start();


    }


}
