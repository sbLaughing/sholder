package laughing.sholder.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import laughing.sholder.Adapter.SheetRecyclerAdapter;
import laughing.sholder.Bean.BillBean;
import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.R;
import laughing.sholder.Utils.CommonUtils;
import laughing.sholder.Widget.ClearableEditText;

/**
 * Created by LaughingSay on 2015/7/30.
 */
public class NewBillFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_UPDATE_UI = 0X11;
    public static final int LOAD_DATA_FINISH = 0x12;
//    public static final int REQUEST_RELOAD_USETAGS =0x12;

    private View rootView;
    private TextView useTv;
    private ClearableEditText moneyEt;
    private EditText remarkEt;
    private TextView dateTv;
    private TextView timeTv;
    private Context mContext;
    private Activity mActivity;
    private boolean isIncome = false;

    /**
     *
     */
    private ArrayList<String> useTags;
    private BillBean bill;
    private boolean isReedit = false;
    private Handler handler;

    /**
     * custom view
     */

    AlertDialog.Builder newTagDialog;
    com.rey.material.widget.EditText clearableEditText;
    Dialog mBottomSheetDialog;
    SheetRecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;

    /**
     *
     */
    ItemTouchHelper.Callback callback;
    View.OnClickListener cardViewClickListener;



    public interface ItemTouchHelpAdapter {
        boolean onItemMove(int fromPosition, int toPosition);

        String onItemDismiss(int position);

    }


    public NewBillFragment() {
        super();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REQUEST_UPDATE_UI: {
                        updateUI();
                        break;
                    }
                    case LOAD_DATA_FINISH:{
                        configDialog();
                    }
                }
            }
        };
    }

    public NewBillFragment init(boolean isIncome, BillBean billBean) {
        this.isIncome = isIncome;
        if (billBean != null) {
            isReedit = true;
            this.bill = billBean;
        } else {
            bill = new BillBean(isIncome);
        }
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_newbill,
                container, false);
        afterViews();
        return rootView;
    }

    private void afterViews() {
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        useTv = (TextView) rootView.findViewById(R.id.use_tv);
        remarkEt = (EditText) rootView.findViewById(R.id.remark_et);
        moneyEt = (ClearableEditText) rootView.findViewById(R.id.money_et);
        dateTv = (TextView) rootView.findViewById(R.id.date_tv);
        timeTv = (TextView) rootView.findViewById(R.id.time_tv);

        configListener();
//        configDialog();
        loadData(true);

    }


    /**
     *
     */
    private void loadData(final boolean configDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                useTags = AccountDBManager.getInstance().getUseTags(isIncome);
                Log.i(null,useTags.toString());

                if (useTags.size() > 0 && bill.getUse() == null) {
                    bill.setUse(useTags.get(0));
                }else if(useTags.size() == 0 && bill.getUse() == null){
                    bill.setUse("其他");
                }
                if (bill.getDate() == null || bill.getTime() == null) {
                    bill.setCalendar(Calendar.getInstance());
                }
                handler.sendEmptyMessage(REQUEST_UPDATE_UI);
                if(configDialog){
                    handler.sendEmptyMessage(LOAD_DATA_FINISH);
                }


            }
        }).start();

    }

    private void addNewTag(final String useTag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (AccountDBManager.getInstance().addNewUseTag(useTag, isIncome)) {
                    bill.setUse(useTag);
                    handler.sendEmptyMessage(REQUEST_UPDATE_UI);
                    recyclerAdapter.addData(useTag);
                    recyclerAdapter.notifyDataSetChanged();
//                    recyclerAdapter.notifyItemInserted(0);
//                    recyclerView.setAdapter(recyclerAdapter);
                }
            }
        }).start();
    }

    private void updateUI() {
        useTv.setText(bill.getUse());
        if (bill.getAccount() != null) {
            moneyEt.setText(bill.getAccount().toString());
        }
        remarkEt.setText(bill.getRemark());
        dateTv.setText(bill.getDate());
        timeTv.setText(bill.getTime());

    }


    public void configListener() {
        useTv.setOnClickListener(this);
        dateTv.setOnClickListener(this);
        timeTv.setOnClickListener(this);

        cardViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v instanceof TextView){
                    bill.setUse(((TextView)v).getText().toString());
                    mBottomSheetDialog.dismiss();
                    handler.sendEmptyMessage(REQUEST_UPDATE_UI);
                }
            }
        };


    }

    /**
     * 主要做的事：1、new一个Dialog，加载底部弹射框的View
     *           2、给view中的取消和新建两个按钮添加点击事件
     *           3、新建item的touch监听，新建Adapter
     *           4、给recyclerView设置itemtouch监听与Adapter
     *           5、并没有showDialog
     *
     */
    private void configDialog() {


        /**
         *  Bottom sheet dialog
         */
        mBottomSheetDialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
        View view = LayoutInflater.from(mContext).inflate(R.layout.bottomsheet_usetv, null);
        view.findViewById(R.id.bottomsheet_newbuildbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                newTagDialog = new AlertDialog.Builder(mActivity,
                        R.style.AppTheme_Dialog_Alert);
                newTagDialog.setTitle("新词条");
                clearableEditText = configCustomView();
                newTagDialog.setView(clearableEditText);
                newTagDialog.setNegativeButton("CANCLE", null);
                newTagDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(clearableEditText.getText().toString().length()<=9)
                            addNewTag(clearableEditText.getText().toString().trim());
                    }
                });
                newTagDialog.show();
            }
        });

        //取消按钮
        view.findViewById(R.id.bottomsheet_cancelbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerAdapter = new SheetRecyclerAdapter(mContext, useTags,isIncome,cardViewClickListener);
        recyclerView.setAdapter(recyclerAdapter);
        callback = new BsItemTouchHelperCallback(recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        //set the layout manager for recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mContext,
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(layoutManager);


        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                CommonUtils.getScreenHeight(mContext) / 2);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private com.rey.material.widget.EditText configCustomView() {
        com.rey.material.widget.EditText et = (com.rey.material.widget.EditText)
                LayoutInflater.from(mContext).inflate(R.layout.dialog_edit,null);
        return et;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.use_tv: {
                mBottomSheetDialog.show();
                break;
            }

            case R.id.date_tv: {
                com.rey.material.app.Dialog.Builder builder;

                builder = new DatePickerDialog.Builder() {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        bill.setDate(dialog.getFormattedDate(CommonUtils.getDateFormat()));
                        handler.sendEmptyMessage(REQUEST_UPDATE_UI);
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
                break;
            }

            case R.id.time_tv: {
                com.rey.material.app.Dialog.Builder builder;
                Calendar calendar = Calendar.getInstance();
                builder = new TimePickerDialog.Builder(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE)) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                        bill.setTime(dialog.getFormattedTime(CommonUtils.getTimeFormat()));
                        handler.sendEmptyMessage(REQUEST_UPDATE_UI);
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.positiveAction("OK")
                        .negativeAction("CANCEL");
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
                break;
            }
        }
    }

    public BillBean saveBill() {
        this.bill.setRemark(remarkEt.getText().toString());
        try {
            this.bill.setAccount(Float.valueOf(moneyEt.getText().toString()));
        } catch (NumberFormatException e) {
            return null;
        }
        AccountDBManager.getInstance().addNewAccount(this.bill);


        return bill;
    }

    /**
     * 当状态从现实showEditFragment切换到显示shoChartsFragment的时候
     * 在showEditFragment中被调用，主要用来回到原始状态
     */
    public void reset() {
        bill = new BillBean();
        moneyEt.getText().clear();
        remarkEt.getText().clear();

        loadData(false);
    }

    public void onSwitchStateChanged(boolean isIncome){
        if(isIncome){
            bill.setFlag(0);
        }else{
            bill.setFlag(1);
        }
        bill.setUse(null);
        this.isIncome = isIncome;
        loadData(true);
    }

    private class BsItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final ItemTouchHelpAdapter mAdapter;

        private BsItemTouchHelperCallback(ItemTouchHelpAdapter mAdapter) {
            this.mAdapter = mAdapter;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipFlags = ItemTouchHelper.START | ItemTouchHelper.END;

            return makeMovementFlags(dragFlags, swipFlags);

        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            String tag = mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            if(bill.getUse().equals(tag)){
                bill.setUse(null);
                loadData(false);
            }
        }


    }
}
