package laughing.sholder.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CommonUtils {

    private CommonUtils() {
    }

    private static class CommonFactory {
        private static CommonUtils common = new CommonUtils();
    }

    public static CommonUtils getInstance() {
        return CommonFactory.common;
    }

    public static void closeSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String FormatDate(Calendar c) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd-EEEE HH:mm");
        String string = formatter.format(c.getTimeInMillis());
        return string;
    }

    public static SimpleDateFormat getDateFormat() {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equals("zh")) {
            return new SimpleDateFormat("yyyy-MM-dd-EEEE", locale);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd-EEEEEEEEEE", locale);
        }
    }

    public static SimpleDateFormat getTimeFormat(){
            return new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public int dp2px(Context context, int dp) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, dm));

    }

    public String getAppDataPath(Context context) {
        return "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"
                + context.getPackageName();
    }

    public int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object object = null;
        Field field = null;
        int x = 0;
        int statusHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            object = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.setStackTrace(null);
        }

        return statusHeight;

    }

    public String getPathFromUri(Context context, Uri uri) {
        String result = null;
        String[] project = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, project, null,
                null, null);
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        }
        cursor.close();
        return result;
    }

    public Bitmap getScaledPicture(String path, int width, int height) {
        if (path == null)
            ;
        else {
            File mFile = new File(path);
            Bitmap bm = null;
            if (mFile.exists()) {
                Options opts = new Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, opts);
                opts.inSampleSize = ScaleMath(opts, width, height);
                opts.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(path, opts);
                return bm;
            }
        }
        return null;
    }

    private int ScaleMath(Options opts, int width, int height) {
        int scale = 1;
        int scaleX;
        int scaleY;

        scaleX = opts.outWidth / width;
        scaleY = opts.outHeight / height;
        if (scaleX > scaleY && scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleY > scaleX && scaleX >= 1) {
            scale = scaleY;
        }
        return scale;
    }

    public int getDayCountFromMonth(String year, String month) {
        return this.getDayCountFromMonth(Integer.valueOf(year), Integer.valueOf(month));
    }

    public int getDayCountFromMonth(int year, int month) {
        int count = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                count = 31;
                break;

            case 4:
            case 6:
            case 9:
            case 11:
                count = 30;
                break;

            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    count = 29;
                } else {
                    count = 28;
                }
                break;


        }
        return count;
    }

    public static String getChineseMonthName(int month){
        if(month>=13 || month <=0){
            return null;
        }
        String string = null;
        switch (month){
            case 1:
                string = "一";
                break;
            case 2:
                string = "二";
                break;
            case 3:
                string = "三";
                break;
            case 4:
                string = "四";
                break;
            case 5:
                string = "五";
                break;
            case 6:
                string = "六";
                break;
            case 7:
                string = "七";
                break;
            case 8:
                string = "八";
                break;
            case 9:
                string = "九";
                break;
            case 10:
                string = "十";
                break;
            case 11:
                string = "十一";
                break;
            case 12:
                string = "十二";
                break;
        }

        return string;



    }

}
