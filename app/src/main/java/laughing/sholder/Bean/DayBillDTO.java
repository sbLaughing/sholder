package laughing.sholder.Bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DayBillDTO {
	
	public class SortByTime implements Comparator<BillBean>{

		@Override
		public int compare(BillBean lhs, BillBean rhs) {
			
			return (-lhs.getTime().compareTo(rhs.getTime()));
		}
		
	}

	int incomeCount = 0;
	int paySumCount = 0;
	private String day;
	private String month;
	private String year;
	private String week = null;
	private float incomeSum = 0;
	private float paySum = 0;
	private ArrayList<BillBean> mData = null;
	
	public DayBillDTO(String day, String month, String year) {
		if(day.length()==1){
			day = "0"+day;
		}
		this.day = day;

		if(month.length()==1){
			month = "0"+month;
		}
		this.month = month;

		this.year = year;

		mData = new ArrayList<BillBean>();
	}
	
	public void sortSelfByTime(){
		if(mData!=null){
			Collections.sort(mData, new SortByTime());
		}
	}

	public ArrayList<BillBean> getData(){
		return this.mData;
	}
	
	public void update(){
		this.paySum = 0;
		this.incomeSum = 0;
		paySumCount= 0;
		incomeCount= 0;
		for(BillBean a : mData){
			if(a.getFlag()==1) {
				this.paySum += a.getAccount();
				this.paySumCount +=1;
			}
			else {
				this.incomeSum += a.getAccount();
				this.incomeCount +=1;
			}
			
		}
	}
	
	public void replaceAccount(BillBean account){
		for(BillBean ac : mData){
			if(ac.getDateBaseId()==account.getDateBaseId()){
				if(ac.replace(account))
					Log.i(null, "repalce success");
			}
		}
	}


	public String getWeek() {
		return week;
	}
	
	public int getAccountNumber(){
		return mData.size();
	}

	public void add(BillBean account){
		if(week == null){
			week = account.getWeek();
		}
		this.mData.add(account);
		if(account.getFlag() == 1){
			this.paySum += account.getAccount();
			paySumCount +=1;
		}else{
			this.incomeSum += account.getAccount();
			incomeCount +=1;
		}
	}
	

	public void removeByIndex(int index){
		mData.remove(index);
		update();
	}
	
	public BillBean getAccountByPosition(int index){
		return mData.get(index);
	}

	public float getIncomeSumFromData() {
		float result = 0;
		float temp;
		for (BillBean account : mData) {
			if ((temp = account.getAccount()) > 0) {
				result += temp;
			}
		}
		return result;
	}

	public float getPaySumFromData() {
		float result = 0;
		float temp;
		if (mData != null) {
			for (BillBean account : mData) {
				if ((temp = account.getAccount()) < 0) {
					result += temp;
				}
			}
		}
		return result;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMonth() {
		return month;
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


}
