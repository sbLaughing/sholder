package laughing.sholder.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Switch;

import laughing.sholder.Bean.BillBean;
import laughing.sholder.Database.AccountDBHelper;
import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.FabAnimator.AnimatorPath;
import laughing.sholder.FabAnimator.PathEvaluator;
import laughing.sholder.FabAnimator.PathPoint;
import laughing.sholder.Fragment.ShowChartsFragment;
import laughing.sholder.Fragment.ShowEditFragment;
import laughing.sholder.R;
import laughing.sholder.Utils.CommonUtils;
import laughing.sholder.Widget.RevealLayout;

public class MainActivity extends AppCompatActivity {


    /**
     * some configure
     */
    private final int ANIMATOR_DURATION = 250;
    private final int FAB_REVEAL_X_DIS = 250;
    private int FAB_C1_X ;
    private int FAB_C1_Y;
    private int FAB_C2_X;
    private int FAB_C2_Y;
    private int FAB_FINAL_X;
    private int FAB_FINAL_Y;
    private int fabWidth;
    private int fabHeight;
    private float FAB_SCALE_SIZE = 15f;
    private boolean fabConfiged = false;
    private boolean isAnimating = false;


    /**
     * set it to true when fab has start revealed animation;
     */
    private boolean revealed = false;
    private int screenWidth;
    private int screenHeight;


    private EditText assetEdit;
    private AlertDialog.Builder assetsBuilder;
    private TextView assetsTv;
    private Switch drawerSwitch;
    private DrawerLayout mDrawerLayout;
    private View leftLayout ;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RevealLayout mRevealLayout;
    private FragmentManager fm ;
    private Context mContext;

    /**
     * Listeners
     */
    private View.OnClickListener fabClickListener = null;
    private Animator.AnimatorListener fabScaledListener = null;
    private OnEditFragmentBack mEditFragmentBackListener = null;
    private OnBackAnimationEnd mOnBackAnimationEnd = null;
    private View.OnClickListener mAssetsTvClickListener = null;
    private ViewPagerInterceptListener mViewPagerInterceptListener = null;

    /**
     * Fragments
     */
    private ShowChartsFragment mShowChartsFragment = null;
    private ShowEditFragment mShowEditFragment  = null;


    /**
     * Interface called when showEditFragment has click close or check
     */
    public interface OnEditFragmentBack{
        public void onBack(int x, int y,@Nullable BillBean billBean);
    }

    /**
     * Interface called when the back ripple animation end
     */
    public interface OnBackAnimationEnd{
        public void onBackAnimationEnd();
    }

    public interface ViewPagerInterceptListener{
        public boolean getSwitchChecked();
    }

    private void initializedInstance(){
        AccountDBManager.initializeInstance(new AccountDBHelper(mContext));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        //对数据库进行初始化
        initializedInstance();

        //set it to false case the device circle round;
        fabConfiged = false;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftLayout = findViewById(R.id.drawer_left_layout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        fab = (FloatingActionButton) findViewById(R.id.float_btn);
        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);

        configDivice();
        configListener();
        configFragment();
        configDrawerLayout();
        fab.setOnClickListener(fabClickListener);
        mRevealLayout.setAnimationEndListener(mOnBackAnimationEnd);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(assetsTv!=null){
            updateAssetsTv(getMyAssets(mContext));
        }
    }

    public void configDrawerLayout(){
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //setting drawertoggle
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(fab!=null){
                    ViewHelper.setAlpha(fab, 1 - slideOffset);
                    if(slideOffset == 1){
                        fab.setClickable(false);
                    }else if(slideOffset == 0){
                        fab.setClickable(true);
                    }
                }
            }
        };

        assetsTv = (TextView) leftLayout.findViewById(R.id.assets_tv);
        assetsTv.setOnClickListener(mAssetsTvClickListener);
//        updateAssetsTv(getMyAssets(MainActivity.this));
        final TextView chartsTv = (TextView) leftLayout.findViewById(R.id.charts_modle_tv);

        drawerToggle.syncState();
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerSwitch = (Switch) leftLayout.findViewById(R.id.drawer_right_switch);
        drawerSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {
                chartsTv.setText("看图模式:   "+(b?"开":"关"));
            }
        });


    }


    public void configDivice(){
        CommonUtils commonUtils = CommonUtils.getInstance();
        screenWidth = commonUtils.getScreenWidth(mContext);
        screenHeight = commonUtils.getScreenHeight(mContext);
        /**
         * 设置Fab轨迹
         */
        FAB_C1_X = -screenWidth/16;
        FAB_C1_Y = screenHeight /6;
        FAB_C2_X = -screenWidth*9/32;
        FAB_C2_Y = screenHeight/4;

        //终点的xy坐标，是跟起点比的相对坐标
        FAB_FINAL_X = -screenWidth/2;
        FAB_FINAL_Y = screenHeight/4;
    }

    /**
     * config Fragments
     */
    public void configFragment(){
        mShowChartsFragment = new ShowChartsFragment().init(mViewPagerInterceptListener);
        mShowEditFragment = new ShowEditFragment();
        mShowEditFragment.setEditBackListener(mEditFragmentBackListener);
        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.show_charts_fragment_container, mShowChartsFragment)
                .show(mShowChartsFragment)
                .add(R.id.show_charts_fragment_container, mShowEditFragment)
                .hide(mShowEditFragment)
                .commit();

    }

    public void configListener() {

        if(assetsBuilder == null){
            assetsBuilder = new AlertDialog.Builder(MainActivity.this,
                    R.style.AppTheme_Dialog_Alert);

            assetsBuilder.setTitle("修改总资产");
            assetsBuilder.setNegativeButton("CANCLE", null);
            assetsBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(assetEdit!=null){
                        if(setAssets(MainActivity.this,assetEdit.getText().toString().trim())){
                            updateAssetsTv(getMyAssets(mContext));
                        }
                    }
                }
            });

        }


        mAssetsTvClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetEdit = (EditText) LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_drawer,null);
                String temp = getMyAssets(mContext).toString();
                assetEdit.setText(temp);
                assetEdit.setSelection(temp.length());
                assetsBuilder.setView(assetEdit);
//                mDrawerLayout.closeDrawers();
                assetsBuilder.show();
            }
        };

        mViewPagerInterceptListener = new ViewPagerInterceptListener() {
            @Override
            public boolean getSwitchChecked() {
                if(drawerSwitch!=null){
                    return drawerSwitch.isChecked();
                }
                else{
                    return false;
                }
            }
        };



        /**
         *在记账界面按了返回键之后绿色揭示动画结束后，所做的事
         */

        mOnBackAnimationEnd = new OnBackAnimationEnd() {
            @Override
            public void onBackAnimationEnd() {
                mRevealLayout.animate()
                        .alpha(0.1f)
                        .setInterpolator(new DecelerateInterpolator())
                        .setDuration(ANIMATOR_DURATION).
                        setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mRevealLayout.setVisibility(View.INVISIBLE);
                                mRevealLayout.setAlpha(1f);
                                isAnimating = false;
                            }
                        }).start();

                mShowEditFragment.resetFragment();
                toolbar.setVisibility(View.VISIBLE);
                fm.beginTransaction()
                        .show(mShowChartsFragment/*.resetView()*/)
                        .hide(mShowEditFragment)
                        .commit();
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                AnimatorPath path = new AnimatorPath();
                path.moveTo(0, 0);

                final ObjectAnimator animator = ObjectAnimator.ofObject(
                        MainActivity.this,
                        "fabLoc",
                        new PathEvaluator(),
                        path.getPoints().toArray());
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        revealed = false;
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageResource(R.mipmap.ic_edit_white);
                        fab.setClickable(true);
                        AnimationSet set = new AnimationSet(true);
                        Animation b = new AlphaAnimation(0,10);
                        b.setDuration(ANIMATOR_DURATION);
                        Animation a = new ScaleAnimation(0,1,0,1,50,50);
                        a.setDuration(ANIMATOR_DURATION);
                        set.addAnimation(a);
                        set.addAnimation(b);
                        set.setInterpolator(new OvershootInterpolator());
                        fab.startAnimation(set);
                    }
                });
            }
        };





        /**
         * after floatActionButton scaled animation end
         */
        fabScaledListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                toolbar.setVisibility(View.GONE);
                fm.beginTransaction()
                        .show(mShowEditFragment.startShowAnimation())
                        .hide(mShowChartsFragment)
                        .commit();
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                fab.animate().alpha(0.1f)
                        .setInterpolator(new DecelerateInterpolator())
                        .setDuration(ANIMATOR_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                fab.setVisibility(View.GONE);
                                fab.setAlpha(1f);
                                fab.setScaleX(1);
                                fab.setScaleY(1);
                                fab.clearAnimation();
                            }
                        })
                        .start();
            }
        };


        /**
         * FAB start curve animation
         */
        fabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fabConfiged){

                    fabWidth = fab.getWidth();
                    fabHeight = fab.getHeight();
                    FAB_SCALE_SIZE = Math.max(2*screenHeight / fabHeight, 2*screenWidth / fabWidth);
                    fabConfiged = true;
                }

                fab.setClickable(false);
                fab.setImageResource(R.color.transparent);


                final float startY = fab.getY();
                AnimatorPath path = new AnimatorPath();
                path.moveTo(0, 0);
                path.curveTo(FAB_C1_X, FAB_C1_Y, FAB_C2_X, FAB_C2_Y, FAB_FINAL_X, FAB_FINAL_Y);


                final ObjectAnimator animator = ObjectAnimator.ofObject(
                        MainActivity.this,
                        "fabLoc",
                        new PathEvaluator(),
                        path.getPoints().toArray());
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(ANIMATOR_DURATION);
                animator.start();

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if(Math.abs(startY-fab.getY()) > Math.abs(FAB_FINAL_Y*4/5)){
                            if(!revealed){
                                fab.animate().scaleXBy(FAB_SCALE_SIZE)
                                        .scaleYBy(FAB_SCALE_SIZE)
//                                        .alpha(0f)
                                        .setListener(fabScaledListener)
                                        .setDuration(ANIMATOR_DURATION);
                                revealed = true;
                            }
                        }
                    }
                });
            }
        };

        /**
         * reset fab animation
         */
        mEditFragmentBackListener = new OnEditFragmentBack() {

            @Override
            public void onBack(int x, int y, @Nullable BillBean billBean) {
                if(billBean!=null){
                    Float temp = getMyAssets(mContext);
                    Float result = billBean.getAccount();
                    //在这里，是新加记录，如果新加的是收入，我把它设置为-account，然后用总资产去减他
                    if(billBean.getFlag()==0){
                        result =-result;
                    }
                    setAssets(MainActivity.this,temp-result);
                    updateAssetsTv(temp-result);
                }

                mRevealLayout.setVisibility(View.VISIBLE);
                mRevealLayout.show(x,y,300);
            }
        };

    }

    public static Float getMyAssets(Context context){
        SharedPreferences sp = context.getSharedPreferences("MainConfig",
                Context.MODE_PRIVATE);
        return sp.getFloat("assets",0.0f);
    }

    public static boolean setAssets(Context context,String trim) {
        Float asset =null;
        try{
            asset = Float.valueOf(trim);
        }catch (NumberFormatException e){
            return false;
        }
        return setAssets(context,asset);
    }

    private void updateAssetsTv(Float asset){
        assetsTv.setText("总资产:  ¥ "+asset);
    }

    public static boolean setAssets(Context context,Float asset){
        SharedPreferences.Editor editor = context.getSharedPreferences(
                "MainConfig", Context.MODE_PRIVATE).edit();
        editor.putFloat("assets", asset);
        editor.commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mShowEditFragment.isVisible() && mShowChartsFragment.isHidden()){
//            fm.beginTransaction().hide(mShowEditFragment).show(mShowChartsFragment).commitAllowingStateLoss();
            if(!isAnimating){
                mShowEditFragment.performBack();
                isAnimating = true;
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_month_list) {
            Intent intent = new Intent(MainActivity.this,ListBillActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * setting fabLoc for ObjectAnimator
     * @param newLoc
     */
    public void setFabLoc(PathPoint newLoc) {
        fab.setTranslationX(newLoc.getMx());
        fab.setTranslationY(newLoc.getMy());
    }



}
