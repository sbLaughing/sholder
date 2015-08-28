package laughing.sholder.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class AccountDBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "account.db";
	
	private static final int DB_VERSION = 1;
	private static String year;

	public AccountDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

		createYearTable(year, db);

		//resume=0 代表收入 =1代表支出
		db.execSQL("CREATE TABLE IF NOT EXISTS UseList"
				+ "(_id integer primary key autoincrement,"
				+ "use text not null,"
				+ "resume integer not null)");


		db.execSQL("CREATE TABLE IF NOT EXISTS ImageManager"
				+ "(_id integer primary key autoincrement,"
				+ "old_image_path text not null,"
				+ "new_image_path text not null," + "_date text not null)");

		db.execSQL(
				"INSERT INTO UseList(use, resume) values(?,?)",
				new String[] { "早午晚饭", "1" });
		db.execSQL(
				"INSERT INTO UseList(use, resume) values(?,?)",
				new String[] { "工资", "0" });
		
		db.execSQL("CREATE TABLE IF NOT EXISTS userconfig"
				+ "(email text null,"
				+ "password text null,"
				+ "database_add text"
				+ ")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
	}

	public static void createYearTable(String year, SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS Account"
				+ year
				+ "(_id integer primary key autoincrement,"
				+ "_date text not null,"
				+ " _time text not null,"
				+ " use text not null,"
				+ "account currency not null,"
				+ "flag integer not null,"
				+ "picture_path text, "
				+ "remark text)");
	}

}
