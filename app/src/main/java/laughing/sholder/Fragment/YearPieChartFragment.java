package laughing.sholder.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import laughing.sholder.Bean.PieChartDataBean;
import laughing.sholder.Database.AccountDBManager;
import laughing.sholder.R;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class YearPieChartFragment extends Fragment {


	private PieChartView chart;
	private PieChartData data;
	private PieChartDataBean mData;
	private Context mContext;
	private boolean isIncome;
	private Handler handler = null;
	public static final int LOAD_DATA_FINISH = 0x01;

	private boolean hasLabels = true;
	private boolean hasLabelsOutside = true;
	private boolean hasCenterCircle = true;
	private boolean isExploded = false;
	private boolean hasLabelForSelected = false;

	public YearPieChartFragment(){
	}

	public YearPieChartFragment init(boolean isIncome){
		this.isIncome = isIncome;
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what){
					case LOAD_DATA_FINISH:
					{
						generateData();
					}
				}

			}
		};
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.fragment_yearpiechart,
				container, false);
		mContext = getActivity().getApplicationContext();

		chart = (PieChartView) rootView.findViewById(R.id.yearpiechart);
		chart.setOnValueTouchListener(new ValueTouchListener());
		chart.setCircleFillRatio(0.7f);

		loadData();

		return rootView;
	}

	public void loadData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				mData = AccountDBManager.getInstance().
						getPieChartData(Calendar.getInstance().get(Calendar.YEAR), isIncome);
				handler.sendEmptyMessage(LOAD_DATA_FINISH);
			}
		}).start();
	}


	private void generateData() {
		if (mData == null || mData.getMap() == null)
			return;
		int numValues = mData.getMap().size();
		if(numValues == 0){
			chart.setVisibility(View.GONE);
		}else{
			chart.setVisibility(View.VISIBLE);
		}
		Iterator<Map.Entry<String, Float>> iter = mData.getMap().entrySet()
				.iterator();
		Map.Entry<String, Float> entry = null;
		Float sum = mData.getSum();

		List<SliceValue> values = new ArrayList<SliceValue>();
		for (int i = 0; i < numValues; ++i) {
			entry = iter.next();
			Float temp = entry.getValue() / sum * 100;
			SliceValue sliceValue = new SliceValue(temp, ChartUtils.pickColor(i % (ChartUtils.COLORS.length)))
					.setLabel(entry.getKey() + " "
							+ ((Math.round(temp * 10)) / 10) + "%");
			values.add(sliceValue);
		}

		data = new PieChartData(values);
		data.setHasLabels(hasLabels);
		data.setHasLabelsOnlyForSelected(hasLabelForSelected);
		data.setHasLabelsOutside(hasLabelsOutside);
		data.setHasCenterCircle(hasCenterCircle);

		if(mData.getMap().size()!=0){
			if(isIncome){
				data.setCenterText1("收入统计"+sum/1+"");
			}else{
				data.setCenterText1("支出统计"+sum/1+"");
			}

			
			// Get roboto-italic font.
//			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
//					"Roboto-Italic.ttf");
//			data.setCenterText1Typeface(tf);
//			// Get font size from dimens.xml and convert it to sp(library uses sp
//			// values).
			data.setCenterText1FontSize(ChartUtils.px2sp(getResources()
					.getDisplayMetrics().scaledDensity, (int) getResources()
					.getDimension(R.dimen.pie_chart_text1_size)));
		}

		chart.setPieChartData(data);


	}

	private void explodeChart() {
		isExploded = !isExploded;
		generateData();

	}

	private void toggleLabelsOutside() {
		// has labels have to be true:P
		hasLabelsOutside = !hasLabelsOutside;
		if (hasLabelsOutside) {
			hasLabels = true;
			hasLabelForSelected = false;
			chart.setValueSelectionEnabled(hasLabelForSelected);
		}

		if (hasLabelsOutside) {
			chart.setCircleFillRatio(0.7f);
		} else {
			chart.setCircleFillRatio(1.0f);
		}

		generateData();

	}

	private void toggleLabels() {
		hasLabels = !hasLabels;

		if (hasLabels) {
			hasLabelForSelected = false;
			chart.setValueSelectionEnabled(hasLabelForSelected);

			if (hasLabelsOutside) {
				chart.setCircleFillRatio(0.7f);
			} else {
				chart.setCircleFillRatio(1.0f);
			}
		}

		generateData();
	}

	private void toggleLabelForSelected() {
		hasLabelForSelected = !hasLabelForSelected;

		chart.setValueSelectionEnabled(hasLabelForSelected);

		if (hasLabelForSelected) {
			hasLabels = false;
			hasLabelsOutside = false;

			if (hasLabelsOutside) {
				chart.setCircleFillRatio(0.7f);
			} else {
				chart.setCircleFillRatio(1.0f);
			}
		}

		generateData();
	}

	private void prepareDataAnimation() {
		for (SliceValue value : data.getValues()) {
			value.setTarget((float) Math.random() * 30 + 15);
		}
	}

	private class ValueTouchListener implements PieChartOnValueSelectListener {

		@Override
		public void onValueSelected(int arcIndex, SliceValue value) {
			// Toast.makeText(getActivity(), "Selected: " + value,
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onValueDeselected() {
			// TODO Auto-generated method stub

		}

	}
}
