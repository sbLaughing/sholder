package laughing.sholder.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import laughing.sholder.Bean.BillBean;
import laughing.sholder.Bean.DayBillDTO;
import laughing.sholder.Bean.MonthBillDTO;
import laughing.sholder.Bean.PieChartDataBean;
import laughing.sholder.Utils.CommonUtils;

public class AccountDBManager {

    private static AccountDBManager instance;
    private static AccountDBHelper mHelper;
    private AtomicInteger mOpenCount = new AtomicInteger();

    private SQLiteDatabase db = null;
    private Calendar c = Calendar.getInstance();
    private String year = String.valueOf(c.get(Calendar.YEAR));


    public static synchronized void initializeInstance(AccountDBHelper helper){
        if(instance == null){
            instance = new AccountDBManager();
            mHelper = helper;

        }
    }

    public static synchronized AccountDBManager getInstance(){
        if(instance == null){
            throw new IllegalStateException(AccountDBManager.class.getSimpleName()+
            " is not initialized, call initialized(...) method first");
        }
        return instance;
    }

    private synchronized SQLiteDatabase openDatabase(){
        if(mOpenCount.incrementAndGet() == 1){
            db = mHelper.getWritableDatabase();
        }

        return db;
    }

    public synchronized void closeDatabase(){
        if(mOpenCount.decrementAndGet() == 0){
            db.close();
        }
    }

    private AccountDBManager(){}


    public boolean tableExist(String tableName) {
        boolean result = false;
        if (tableName != null) {
            Cursor cursor = null;
            try {
                String sql = "SELECT count(*) FROM sqlite_master WHERE type=? and name=?";
                cursor = db.rawQuery(sql, new String[]{"table",
                        "Account" + year});
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0)
                        result = true;
                }

            } catch (Exception e) {

            }
        }
        return result;
    }

    private static String getFormatTime(Calendar c) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd EEEE HH:mm");
        String string = formatter.format(c.getTimeInMillis());
        return string;
    }


    public void addNewAccount(BillBean account) {

        openDatabase();
        db.execSQL(
                "INSERT INTO Account"
                        + year
                        + "(_date, _time, use, account, flag, picture_path, remark)"
                        + " values(?,?,?,?,?,?,?)",
                new Object[]{
                        account.getDate(),
                        account.getTime(),
                        account.getUse(),
                        account.getAccount(),
                        account.getFlag(),
                        account.getPicturePath(),
                        account.getRemark()});
        closeDatabase();

    }

    /**
     *
     * @param year      查询哪一年
     * @param month     查询这年的具体的某月，如果正确，返回的List.size()=1; 取值1~12
     * @return          返回查询到的数据。
     */
    public ArrayList<MonthBillDTO> getMonthBillList(int year,@Nullable Integer month,boolean reverse) {

        openDatabase();
        MonthBillDTO monthBill = null;
        DayBillDTO dayBill = null;
        ArrayList<MonthBillDTO> monthBillList = null;
        Cursor cursor = null;
        String string;
        String monthString;
        BillBean account = null;
        String sql = null;
        CommonUtils common = CommonUtils.getInstance();

        monthBillList = new ArrayList<MonthBillDTO>();
        for (int j = 1; j <= 12; j++) {
            if (j < 10) {
                monthString = "0" + j;
            } else {
                monthString = "" + j;
            }

            //当进行精确的月份账单查询时，如果要进行读数据的月份与参数不符合，则不继续下去
            if(month!=null && (month <=12 && month >=1) && month!=j ){
                continue;
            }

            monthBill = new MonthBillDTO(monthString, String.valueOf(year));
            for (int i = common.getDayCountFromMonth(year, j); i > 0; i--) {
                if (i < 10) {
                    string = "0" + i;
                } else {
                    string = "" + i;
                }

                dayBill = new DayBillDTO(string, monthString,
                        String.valueOf(year));
                sql = "SELECT * FROM Account" + year
                        + " WHERE _date like ?";
                cursor = db.rawQuery(sql, new String[]{year + "-"
                        + monthString + "-" + string + "%"});

                while (cursor.moveToNext()) {
                    account = new BillBean();
                    account.setAccount(cursor.getFloat(cursor
                            .getColumnIndex("account")));
                    account.setDate(cursor.getString(cursor
                            .getColumnIndex("_date")));
                    account.setTime(cursor.getString(cursor
                            .getColumnIndex("_time")));
                    account.setPicturePath(cursor.getString(cursor
                            .getColumnIndex("picture_path")));
                    account.setRemark(cursor.getString(cursor
                            .getColumnIndex("remark")));
                    account.setUse(cursor.getString(cursor
                            .getColumnIndex("use")));
                    account.setDateBaseId(cursor.getInt(cursor
                            .getColumnIndex("_id")));
                    account.setFlag(cursor.getInt(cursor.getColumnIndex("flag")));

                    dayBill.add(account);
                }
                cursor.close();
                dayBill.sortSelfByTime();
                monthBill.add(dayBill);
                cursor.close();
            }

            //如果查找到的月份比本月大，而且这个月里面没有消费记录 则不添加到list里
            if (year == c.get(Calendar.YEAR)) {
                if ((j > (c.get(Calendar.MONTH) + 1)) && monthBill.getAccountNumber() == 0) {
                    continue;
                }
            }
            monthBillList.add(monthBill);
        }

        closeDatabase();
        if(reverse){
            Collections.reverse(monthBillList);
        }
        return monthBillList;
    }

    public PieChartDataBean getPieChartData(int year, boolean isIncome) {
        openDatabase();
        Map<String, Float> map = new HashMap<String, Float>();

        PieChartDataBean data = new PieChartDataBean();
        String use;
        Float count;
        Cursor cursor = null;
        String sql = "SELECT use, flag, account FROM account" + year;
        cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            if (isIncome && cursor.getInt(1) == 0) {
                use = cursor.getString(0);
                if (map.get(use) == null) {
                    count = cursor.getFloat(2);
                } else {
                    count = map.get(use) + cursor.getFloat(2);
                }
                map.put(use, count);
            } else if (!isIncome && cursor.getInt(1) == 1) {
                use = cursor.getString(0);
                if (map.get(use) == null) {
                    count = cursor.getFloat(2);
                } else {
                    count = map.get(use) + cursor.getFloat(2);
                }
                map.put(use, count);

            }
        }
        cursor.close();
        data.setMap(map);
        closeDatabase();;
        return data;
    }

    public boolean addNewUseTag(String string, boolean isIncome) {
        openDatabase();
        if (string == null || string.equals("")) {
            return false;
        }

        if (string.length() > 10) {
            string = string.substring(0, 11) + "...";
        }

        int resume = 1;
        if (isIncome)
            resume = 0;

        Cursor cursor = db.rawQuery("SELECT resume from UseList WHERE use=?",new String[]{string});
        cursor.moveToFirst();
        if(cursor.moveToNext()){
            if (cursor.getInt(0) == resume){
                cursor.close();
                return false;
            }

        }

        db.execSQL("INSERT INTO UseList (use,resume)"
                + "values(?,?)", new Object[]{string,resume});
        cursor.close();
        closeDatabase();
        return true;
    }

    public ArrayList<String> getUseTags(boolean isIncome) {
        openDatabase();
        ArrayList<String> list = new ArrayList<String>();
        list.clear();
        String string = null;
        Cursor cursor = null;
        String temp = null;
        if (isIncome) {
            temp = "0";
        } else {
            temp = "1";
        }
        //以my_order的升序方式读取tags
        cursor = db.rawQuery("SELECT use FROM UseList WHERE resume=? ORDER BY _id ASC",
                new String[]{temp});
        while (cursor.moveToNext()) {
            string = cursor.getString(0);
            list.add(string);
        }
        cursor.close();
        closeDatabase();
        return list;
    }


    public boolean swapTag(String swapA, String swapB){
        openDatabase();
        String sql = "SELECT count(1) FROM UseList WHERE use=? or use=?";
        Cursor cursor = db.rawQuery(sql,new String[]{swapA,swapB});
        cursor.moveToFirst();
        if(cursor.moveToNext()){
            if(cursor.getInt(0)!=2){
//                cursor.close();
                return false;
            }
        }
        int swapAId=0;
        int swapBId=0;
        sql = "SELECT _id FROM UseList WHERE use=?";
        cursor = db.rawQuery(sql,new String[]{swapA});
        if(cursor.moveToNext()){
            swapAId = cursor.getInt(0);
        }
        cursor = db.rawQuery(sql,new String[]{swapB});
        if(cursor.moveToNext()){
            swapBId = cursor.getInt(0);
        }
        if(swapAId!=0 && swapBId!=0){
            sql = "UPDATE UseList SET use=? WHERE _id=?";
            db.execSQL(sql,new Object[]{swapA,swapBId});
            db.execSQL(sql,new Object[]{swapB,swapAId});
            return true;
        }else{
            return false;
        }
    }


    public void deleteUseTag(String string,int resume) {
        Log.e("in delete","string="+string+" resume="+resume);
        openDatabase();
        db.execSQL("DELETE FROM UseList WHERE use=? and resume=?",
                new Object[]{string,resume});
        closeDatabase();
    }

    public void deleteAccount(BillBean account) {
        openDatabase();
        db.execSQL("DELETE FROM Account" + year + " WHERE _id=?",
                new Object[]{account.getDateBaseId()});
        closeDatabase();
    }

    public void updateAccount(BillBean account) {
        openDatabase();
        db.execSQL(
                "UPDATE Account" + year + " SET _date=?, _time=?, use=?,"
                        + "account=?, flag=?,"
                        + "picture_path=?, remark=? WHERE _id=?",
                new Object[]{account.getDate(), account.getTime(),
                        account.getUse(),
                        account.getAccount(), account.getFlag(),
                        account.getPicturePath(),
                        account.getRemark(), account.getDateBaseId()});
        closeDatabase();
    }



    public void PushPicturePath(String oldPath, String newPath) {
        openDatabase();
        String sql = "INSERT INTO ImageManager(old_image_path, new_image_path, _date)"
                + " VALUES(?,?,?)";
        db.execSQL(sql, new Object[]{oldPath, newPath, getFormatTime(c)});
        closeDatabase();
    }

    public String PopPicturePath(String path) {
        openDatabase();
        String result = null;
        String sql = "SELECT new_image_path FROM ImageManager WHERE old_image_path=?";
        Cursor cursor = db.rawQuery(sql, new String[]{path});
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("new_image_path"));
        }
        cursor.close();
        closeDatabase();
        return result;
    }

    public void addLoginHis(String email, String password) {
        openDatabase();
        Cursor cursor = db.rawQuery("SELECT count(1) FROM userconfig WHERE email=?", new String[]{email});
        cursor.moveToNext();
        if (cursor.getInt(0) > 0) {
            db.execSQL("UPDATE userconfig set password=? WHERE email=?", new Object[]{password, email});
        } else {
            db.execSQL("INSERT INTO userconfig(email,password) values(?,?)", new Object[]{email, password});
        }
        cursor.close();
        closeDatabase();
    }

    public String[] getLoginHis() {
        openDatabase();
        String email = null;
        String password = null;

        Cursor cursor = db.rawQuery("SELECT email,password FROM userconfig", null);
        if (cursor.moveToNext()) {
            email = cursor.getString(0);
            password = cursor.getString(1);
        }
        cursor.close();
        closeDatabase();
        return new String[]{email, password};
    }

    public void clearLoginHis() {
        openDatabase();
        db.execSQL("UPDATE userconfig SET password=?", new String[]{""});
        closeDatabase();
    }

}
