package laughing.sholder.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.List;

import laughing.sholder.Activity.DetailedBillActivity;
import laughing.sholder.Bean.MonthBillDTO;
import laughing.sholder.R;
import laughing.sholder.Utils.CommonUtils;

/**
 * Created by LaughingSay on 2015/8/7.
 *
 */
public class ListCardViewAdapter extends RecyclerView.Adapter<ListCardViewAdapter.MyViewHolder> {

    public static final String YEAR_KEY = "year";
    public static final String MONTH_KEY = "month";


    private LayoutInflater mInflater;
    private Context mContext;
    private List<MonthBillDTO> mData;
    private Activity mActivity;
    private int[] imageResource = {R.mipmap.winter_jan,R.mipmap.spring_feb,R.mipmap.spring_march,
                                    R.mipmap.spring_april,R.mipmap.spring_may,R.mipmap.summer_june,
                                    R.mipmap.summer_july,R.mipmap.summer_augist,R.mipmap.autumn_sep,
                                    R.mipmap.autumn_oct,R.mipmap.autumn_nov,R.mipmap.winter_dec
                                    };

    public ListCardViewAdapter(Activity activity, List<MonthBillDTO> datas){

        this.mData = datas;
        mActivity = activity;
        this.mContext = mActivity.getApplicationContext();
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_listbill,parent,false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MonthBillDTO monthBill = mData.get(position);

        final ImageView mImageView = holder.imageView;
        final int month = Integer.valueOf(monthBill.getMonth());
        final int year = Integer.valueOf(monthBill.getYear());
        mImageView.setBackgroundResource(imageResource[month]);
        holder.monthTv.setText(monthBill.getEngMonth());
        holder.incomeTv.setText("收入:¥"+monthBill.getIncomeSum());
        holder.incomeCountTv.setText("共"+monthBill.getIncomeCount()+"笔");
        holder.resumeTv.setText("支出:¥"+monthBill.getPaySum());
        holder.resumeCountTv.setText("共"+monthBill.getpaySumCount()+"笔");



        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, DetailedBillActivity.class);
                intent.putExtra(YEAR_KEY, year);
                intent.putExtra(MONTH_KEY, month);
                Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) mImageView.getBackground()).getBitmap(),
                        CommonUtils.getScreenWidth(mContext),
                        mImageView.getHeight(),
                        true
                );
                ActivityTransitionLauncher
                        .with(mActivity)
                        .image(bmp)
                        .from(mImageView)
                        .launch(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    class MyViewHolder extends  RecyclerView.ViewHolder{

        View contentView;
        ImageView imageView;
        TextView monthTv;
        TextView resumeTv;
        TextView incomeTv;
        TextView resumeCountTv;
        TextView incomeCountTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            monthTv = (TextView) itemView.findViewById(R.id.image_month_tv);
            resumeTv = (TextView) itemView.findViewById(R.id.card_resume_tv);
            resumeCountTv = (TextView) itemView.findViewById(R.id.card_resume_count_tv);
            incomeTv = (TextView) itemView.findViewById(R.id.card_income_tv);
            incomeCountTv = (TextView) itemView.findViewById(R.id.card_income_count_tv);

        }
    }
}


