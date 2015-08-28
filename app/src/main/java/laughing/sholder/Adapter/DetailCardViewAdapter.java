package laughing.sholder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import laughing.sholder.Bean.BillBean;
import laughing.sholder.Bean.MonthBillDTO;
import laughing.sholder.R;

/**
 * Created by LaughingSay on 2015/8/7.
 *
 */
public class DetailCardViewAdapter extends RecyclerView.Adapter<DetailCardViewAdapter.MyViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<BillBean> mData;
    private View.OnLongClickListener listener;
    private int[] colors = {
            R.color.my_text_color,
            R.color.light_blue,
            R.color.light_gray,
            R.color.light_green,
            R.color.orange_red,
            R.color.orange

    };
    private int blackColor;
    private int redColor;

    public DetailCardViewAdapter(Context context, MonthBillDTO data, View.OnLongClickListener longLis){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = data.toOpenData();
        this.listener = longLis;
        blackColor = mContext.getResources().getColor(colors[0]);
        redColor = mContext.getResources().getColor(colors[4]);
    }

    public void deleteBillById(int id){
        for(int i=0;i<mData.size();i++){
            if(id==mData.get(i).getDateBaseId()){
                mData.remove(i);
                this.notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cardview_detailedbill,parent,false);
        view.setOnLongClickListener(listener);
        Log.e(null, "after set long click listener for view");
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        int colorLength = colors.length;
        int temp = (int) Math.round(Math.random() * colorLength);
        if(temp<1)
            temp = 1;
        else if(temp >6)
            temp = 6;

        BillBean bill = mData.get(position);
        Float number = bill.getAccount();
        if(bill.getFlag()==1){
            holder.showMoneyTv.setText("¥"+number.toString());
            holder.showMoneyTv.setTextColor(blackColor);
        }else{
            holder.showMoneyTv.setText("+¥"+number.toString());
            holder.showMoneyTv.setTextColor(redColor);
        }

        holder.showUseTv.setText(bill.getUse());
        holder.showDateTv.setText(bill.getDay()+"日 "+bill.getTime());
        holder.contentView.setTag(bill);
        if(bill.getPicturePath()==null){
            ViewGroup.LayoutParams params = holder.bgImageView.getLayoutParams();
            params.height = 80+ String.valueOf(number/10).length()*40+(int)(number%10);
            holder.bgImageView.setLayoutParams(params);
            holder.bgImageView.setBackgroundColor(
                    mContext.getResources().getColor(colors[temp-1]));
        }else{

        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        View contentView;
        TextView showMoneyTv;
        TextView showUseTv;
        TextView showDateTv;
        ImageView bgImageView;
        ImageView leftImageView;
        ImageView rightImageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            showMoneyTv = (TextView) itemView.findViewById(R.id.detail_show_money_tv);
            showUseTv = (TextView) itemView.findViewById(R.id.detail_show_use_tv);
            showDateTv = (TextView) itemView.findViewById(R.id.detail_show_date_tv);
            bgImageView = (ImageView) itemView.findViewById(R.id.image_view_detailed_card_view);
            leftImageView = (ImageView) itemView.findViewById(R.id.detail_left_image_view);
            rightImageView = (ImageView) itemView.findViewById(R.id.detail_right_image_view);

        }
    }
}
