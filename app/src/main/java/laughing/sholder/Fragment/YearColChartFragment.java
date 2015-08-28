package laughing.sholder.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import laughing.sholder.Bean.MonthBillDTO;
import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.R;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class YearColChartFragment extends Fragment {

    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = true;


    /**
     * custom define by miao.yu
     */
    private String TAG = "YearColChartFragment";
    private List<MonthBillDTO> mData;
    private Handler handler = null;
    private Context mContext;

    public static final int LOAD_DATA_FINISH = 0x01;


    public YearColChartFragment() {
        super();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case LOAD_DATA_FINISH: {
                        generateSubcolumnsData();
                    }
                }
            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_yearcolchart,
                container, false);
        mContext = getActivity().getApplicationContext();
        chart = (ColumnChartView) rootView.findViewById(R.id.yearcolchart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        loadData();
        return rootView;
    }

    public void loadData() {
//        if(!isResumed()){
//            return;
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mData = AccountDBManager.getInstance().
                        getMonthBillList(Calendar.getInstance().get(Calendar.YEAR),null,false);
                int size = mData.size();
                for(int i = 0;i<size;i++){
                }

                handler.sendEmptyMessage(LOAD_DATA_FINISH);
            }
        }).start();


    }

    /**
     * Generates columns with subcolumns, columns have larger separation than
     * subcolumns.
     */
    private void generateSubcolumnsData() {
        int dataSize = mData.size();
        if (mData == null || dataSize == 0) {
            if (chart.getVisibility() != View.INVISIBLE)
                chart.setVisibility(View.INVISIBLE);
            return;
        }

        int count = 0;
        for (int i = 0; i < dataSize; i++) {
            count += mData.get(i).getAccountNumber();
        }
        if(count == 0){
            if (chart.getVisibility() != View.INVISIBLE)
                chart.setVisibility(View.INVISIBLE);
            return;
        }

        if (chart.getVisibility() != View.VISIBLE)
            chart.setVisibility(View.VISIBLE);
        int numSubcolumns = 2;
        int numColumns = mData.size();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        int payColor = Color.parseColor("#FFBB33");
        int incomeColor = Color.parseColor("#99CC00");

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        MonthBillDTO monthData = null;

        for (int i = 0; i < numColumns; ++i) {
            monthData = mData.get(i);
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                if (j == 0) {
                    values.add(new SubcolumnValue(monthData.getPaySum(),
                            payColor));
                } else {
                    values.add(new SubcolumnValue(monthData.getIncomeSum(),
                            incomeColor));
                }
            }

            axisValues.add(new AxisValue(i).setLabel(monthData.getEngMonth()));
            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        data = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis(axisValues);
            Axis axisY = new Axis().setHasLines(true);
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setColumnChartData(data);

    }

    private class ValueTouchListener implements
            ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex,
                                    SubcolumnValue value) {
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
}
