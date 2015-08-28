package laughing.sholder.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import laughing.sholder.Adapter.ListCardViewAdapter;
import laughing.sholder.Bean.MonthBillDTO;
import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.R;

/**
 * Created by LaughingSay on 2015/8/7.
 *
 */
public class ListBillActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbill);
        mContext = getApplicationContext();
        handler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar!=null){
            toolbar.setTitle(" ");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_36dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListBillActivity.this.finish();
                }
            });
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_listbill);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);


        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<MonthBillDTO> data =
                        AccountDBManager.getInstance().getMonthBillList(2015,null,true);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setAdapter(new ListCardViewAdapter(ListBillActivity.this, data));
                    }
                });
            }
        }).start();



    }
}
