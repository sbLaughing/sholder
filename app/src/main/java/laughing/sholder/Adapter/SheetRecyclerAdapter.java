package laughing.sholder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.Fragment.NewBillFragment;
import laughing.sholder.R;

/**
 * Created by LaughingSay on 2015/8/6.
 *
 */
public class SheetRecyclerAdapter extends
        RecyclerView.Adapter<SheetRecyclerAdapter.
                UseBSViewHolder> implements NewBillFragment.ItemTouchHelpAdapter{

    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mData;
    private boolean isIncome;
    private View.OnClickListener mOnClickListener;

    public SheetRecyclerAdapter(Context context,
                                List<String> datas,
                                boolean isIncome,
                                View.OnClickListener lis){
        this.mContext = context;
        this.mData = datas;
        this.mInflater = LayoutInflater.from(mContext);
        this.isIncome = isIncome;
        this.mOnClickListener = lis;
    }

    public void setDatas(List<String> datas){
        this.mData = datas;
    }
    public void addData(String tag){
        mData.add(tag);
    }

    @Override
    public UseBSViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.listview_item_usetv,viewGroup,false);
        UseBSViewHolder holder = new UseBSViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(UseBSViewHolder useBSViewHolder, int i) {
        useBSViewHolder.textView.setText(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < toPosition){
            for(int i = fromPosition; i< toPosition;i++){
                Collections.swap(mData,i,i+1);
            }
        }else{
            for(int i=fromPosition;i>toPosition;i--){
                Collections.swap(mData,i,i-1);
            }
        }
        final String swapA = mData.get(fromPosition);
        final String swapB = mData.get(toPosition);
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccountDBManager.getInstance().swapTag(swapA, swapB);
            }
        }).start();

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public String onItemDismiss(int position) {
        final String tag = mData.get(position);
        mData.remove(position);
        notifyItemRemoved(position);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AccountDBManager.getInstance().deleteUseTag(tag,
                        isIncome?0:1);
            }
        }).start();
        return tag;
    }

    class UseBSViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public UseBSViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.item_tv);
            textView.setOnClickListener(mOnClickListener);
        }
    }
}


