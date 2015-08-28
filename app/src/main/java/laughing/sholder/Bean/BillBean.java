package laughing.sholder.Bean;

import android.support.annotation.Nullable;

import java.util.Calendar;

import laughing.sholder.Utils.CommonUtils;

/**
 * Created by LaughingSay on 2015/7/26.
 */
public class BillBean {
    private static final long serialVersionUID = -828433277135374357L;

    private Integer DateBaseId=0;
    /**
     * format yyyy-MM-dd-EEEE
     */
    private String date = null;
    private String time = null;

    private String year=null;
    private String month=null;
    private String day=null;
    private String week=null;

    private String use = null;

    private Integer flag = 1;
    private Float account = null;

    private String remark = null;
    private String picturePath = null;

    public BillBean(){
    }

    public BillBean(boolean isIncome){
        if(isIncome){
            setFlag(0);
        }
    }

    public void setCalendar(Calendar calendar){
        String string = CommonUtils.getDateFormat().format(calendar.getTimeInMillis());
        setDate(string);
        string = CommonUtils.getTimeFormat().format(calendar.getTimeInMillis());
        setTime(string);

    }

    public boolean replace(BillBean a){
        if(a!=this){
            this.date = a.getDate();
            this.time = a.getTime();
            this.flag = a.flag;
            this.account = a.getAccount();
            this.year = a.getYear();
            this.month = a.getMonth();
            this.day = a.getDay();
            this.week = a.getWeek();
            this.use = a.getUse();
            this.remark = a.getRemark();
            this.picturePath = a.getPicturePath();
            return true;
        }
        return false;
    }


    public void setDateBaseId(Integer dateBaseId) {
        DateBaseId = dateBaseId;
    }

    public void setDate(String date) {
        this.date = date;
        String[] ss = date.split("-");
        year = ss[0];
        month = ss[1];
        day = ss[2];
        week = ss[3];

    }

    public void setTime(String time) {
        this.time = time;
    }


    public void setUse(@Nullable String use) {
        this.use = use;
    }


    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public void setAccount(Float account) {
        this.account = account;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getDateBaseId() {
        return DateBaseId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getWeek() {
        return week;
    }

    public String getUse() {
        return use;
    }


    public Integer getFlag() {
        return flag;
    }

    public Float getAccount() {
        return account;
    }

    public String getRemark() {
        return remark;
    }

    public String getPicturePath() {
        return picturePath;
    }
}
