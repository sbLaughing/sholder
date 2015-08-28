package laughing.sholder.Bean;

import java.util.ArrayList;
import java.util.List;


public class MonthBillDTO {

	private String month;
	private String year;
	private float incomeSum = 0;
	private float paySum = 0;
	private int incomeCount = 0;
	private int paySumCount = 0;
	private ArrayList<DayBillDTO> mData = null;

	private boolean isEmpty = true;

	public MonthBillDTO(String month, String year) {
		super();
		this.month = month;
		this.year = year;

		mData = new ArrayList<DayBillDTO>();
	}

	public List<BillBean> toOpenData(){
		List<BillBean> data = new ArrayList<BillBean>();
		for(DayBillDTO day :mData){
			data.addAll(day.getData());
		}

		return data;
	}

	public float getMaxDayAccount() {
		float temp = 0.0f;
		for (DayBillDTO day : mData) {
			if (day.getPaySum() > temp) {
				temp = day.getPaySum();
			}
		}

		return temp;
	}

	public boolean ifSameDay(int position1, int position2) {
		BillBean a = getAccountByPosition(position1);
		BillBean b = getAccountByPosition(position2);

		if (a != null && b != null) {
			return a.getDate().equals(b.getDate());
		}
		return false;
	}

	public DayBillDTO getDayBillByPosition(int position) {
		DayBillDTO mDayBill = null;
		int n = 0;
		if (position < getAccountNumber()) {
			int savePosition = position + 1;
			for (DayBillDTO dayBill : mData) {
				if ((n = dayBill.getAccountNumber()) < (savePosition)) {
					savePosition -= n;
				} else {
					mDayBill = dayBill;
					break;
				}
			}
		}
		return mDayBill;
	}

	public void delAccountDTOByPosition(int position) {
		Integer temp = position;
		if (mData != null && !mData.isEmpty())
			for (DayBillDTO dayBill : mData) {
				if (temp >= dayBill.getAccountNumber()) {
					temp -= dayBill.getAccountNumber();
				} else {
					dayBill.removeByIndex(temp);
					break;
				}
			}
		update();

	}

	public BillBean getAccountByPosition(int position) {
		BillBean account = null;
		Integer temp = position;
		if (mData != null && !mData.isEmpty())
			for (DayBillDTO dayBill : mData) {
				if (temp >= dayBill.getAccountNumber()) {
					temp -= dayBill.getAccountNumber();
				} else {
					account = dayBill.getAccountByPosition(temp);
					break;
				}
			}

		return account;
	}

	public void updateAccount(int position, BillBean account) {
		getDayBillByPosition(position).replaceAccount(account);
		update();
	}

	public int getAccountNumber() {
		int result = 0;
		for (DayBillDTO dayBill : mData) {
			result += dayBill.getAccountNumber();
		}
		return result;

	}

	public void add(DayBillDTO dayBill) {
		if (dayBill.getAccountNumber()==0)
			return;
		mData.add(dayBill);
		this.isEmpty = false;
		this.incomeSum += dayBill.getIncomeSum();
		this.paySum += dayBill.getPaySum();
		incomeCount += dayBill.incomeCount;
		paySumCount += dayBill.paySumCount;

	}

	public String getMonth() {
		return month;
	}

	public String getEngMonth() {
		String result = null;
		switch (Integer.valueOf(month)) {
		case 1:
			result = "Jan";
			break;
		case 2:
			result = "Feb";
			break;
		case 3:
			result = "Mar";
			break;
		case 4:
			result = "Apr";
			break;
		case 5:
			result = "May";
			break;
		case 6:
			result = "Jun";
			break;
		case 7:
			result = "Jul";
			break;
		case 8:
			result = "Aug";
			break;
		case 9:
			result = "Sep";
			break;
		case 10:
			result = "Oct";
			break;
		case 11:
			result = "Nov";
			break;
		case 12:
			result = "Dec";
			break;
		}

		return result;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public float getIncomeSum() {
		return incomeSum;
	}

//	public void setIncomeSum(float incomeSum) {
//		this.incomeSum = incomeSum;
//	}

	public float getPaySum() {
		return paySum;
	}

//	public void setPaySum(float paySum) {
//		this.paySum = paySum;
//	}

	public void update() {
		this.paySum = 0;
		this.incomeSum = 0;
		paySumCount =0;
		incomeCount =0;
		if (mData != null) {
			for (DayBillDTO dayBill : mData) {
				dayBill.update();
				this.paySum += dayBill.getPaySum();
				this.incomeSum += dayBill.getIncomeSum();
				incomeCount +=dayBill.incomeCount;
				paySumCount +=dayBill.paySumCount;
			}
		}
	}

	public int getIncomeCount(){
		return incomeCount;
	}
	public int getpaySumCount(){
		return paySumCount;
	}

}
