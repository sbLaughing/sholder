package laughing.sholder.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;

import laughing.sholder.Adapter.DetailCardViewAdapter;
import laughing.sholder.Adapter.ListCardViewAdapter;
import laughing.sholder.Bean.BillBean;
import laughing.sholder.Bean.MonthBillDTO;
import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.R;
import laughing.sholder.Utils.MyVibrate;

/**
 * Created by LaughingSay on 2015/8/7.
 */
public class DetailedBillActivity extends AppCompatActivity {

    private static final int DURATION = 300;
    private static final int StaggeredCols = 2;

    private ExitActivityTransition exitActivityTransition;
    private Handler handler;

    private RecyclerView mRecyclerView;
    private TextView monthTv;
    private Toolbar toolbar;
    private int year;
    private int month;
    private MonthBillDTO monthDto;
    private DetailCardViewAdapter mAdapter;
    private View.OnLongClickListener longClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailedbill);
        Intent recIntent = getIntent();

        exitActivityTransition = ActivityTransition
                .with(recIntent)
                .duration(DURATION)
                .to(findViewById(R.id.image_view))
                .start(savedInstanceState);


        year = recIntent.getIntExtra(ListCardViewAdapter.YEAR_KEY, 0);
        month = recIntent.getIntExtra(ListCardViewAdapter.MONTH_KEY, 0);
        handler = new Handler();

        monthTv = (TextView) findViewById(R.id.detail_acticity_month_tv);
        configToolbar();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_detailed);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(StaggeredCols,
                StaggeredGridLayoutManager.VERTICAL));
        longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MyVibrate.Vibrate(DetailedBillActivity.this,100);
                final View tempView = v;
                new AlertDialog.Builder(DetailedBillActivity.this,
                        R.style.AppTheme_Dialog_Alert)
                        .setTitle("确定删除这条记录?")
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final BillBean tempBill = (BillBean) tempView.getTag();
                                        AccountDBManager.getInstance().deleteAccount((BillBean) (tempView.getTag()));
                                        Float account = tempBill.getAccount();
                                        //如果这个账单记录是属于收入部分，那么我们把account设置为负数，例如收入100，在这里account=-100；
                                        if(tempBill.getFlag()==0){
                                            account=-account;
                                        }
                                        //result是从资产记录中读取出来的，按照逻辑，此时若是删除了一条支出，则应该 记录+|account|
                                        //如果删除了一条收入，则应该 记录-|account|
                                        Float result=MainActivity.getMyAssets(DetailedBillActivity.this)+account;
                                        Log.e(null,"account="+account+"  result = "+result);
                                        MainActivity.setAssets(DetailedBillActivity.this,result);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.deleteBillById(tempBill.getDateBaseId());
                                            }
                                        });
                                    }
                                }).start();
                            }
                        })
                        .show();
                return false;
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                monthDto = AccountDBManager.getInstance().getMonthBillList(
                        year, month, false).get(0);
                Log.e(null,"before new Adapter");
                mAdapter = new DetailCardViewAdapter(DetailedBillActivity.this,monthDto,longClickListener);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showStartAnimation();
                    }
                }, DURATION * 2);

            }
        }).start();

    }

    private void configToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar!=null){
            toolbar.setTitle(" ");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_36dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    private void showStartAnimation(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(mAdapter);
        AlphaAnimation a = new AlphaAnimation(0, 1);
        a.setDuration(DURATION);
        mRecyclerView.startAnimation(a);

        monthTv.setText(monthDto.getEngMonth());
        monthTv.startAnimation(a);
    }

    @Override
    public void onBackPressed() {
        toolbar.setVisibility(View.INVISIBLE);
        monthTv.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        exitActivityTransition.exit(this);
    }

}
