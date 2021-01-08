package ru.phplego.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import ru.phplego.core.date.Date;
import ru.phplego.core.db.Database;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 18.07.12
* Time: 13:32
*/

public class Application extends android.app.Application {
	private static Context mContext;

	private static Application mInstance;

	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mContext = this.getApplicationContext();
	}

	public static Context getContext() {
		if(mContext == null){
			throw new Error("Trying to call 'App.getContext()' before 'mContext' initialized");
		}
		return mContext;
	}

	public static SQLiteDatabase getDb() {
		return Database.getDatabase();
	}

	public static String getVersion() {
		try{
				PackageManager manager = getContext().getPackageManager();
				PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
				return info.versionName;
		}catch (Exception e){ }
		return "";
	}

	/**
	* Возвращает число пикселов для заданного DIP
	* @param dip
	* @return
	*/
	public static int dip(float dip) {
		int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						dip, getContext().getResources().getDisplayMetrics());
		return value;
	}

	public static void toast(String text, Context ctxt, boolean long_time) {
		int length = long_time ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
		if(ctxt == null) ctxt = Application.getContext();
		Toast.makeText(ctxt, text, length).show();
	}

	public static void toast(int resource, boolean long_time) {
		toast(Application.getContext().getString(resource), null, long_time);
	}

	public static void toast(String text, boolean long_time) {
		toast(text, null, long_time);
	}

	public static void toast(String text) {
		toast(text, null, false);
	}

	public static void toast(int resource) {
		toast(resource, false);
	}

	public static boolean isPackageInstalled(String pkg) {
		PackageManager pm = getContext().getPackageManager();
		//get a list of installed apps.
		List<ApplicationInfo> packages = pm
						.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages) {
				if(pkg.equals(packageInfo.packageName)) return true;
		}
		return false;
	}

	public static void showSoftKeyboard() {
		InputMethodManager inputMethodManager=(InputMethodManager) Application.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

	public static void hideSoftKeyboard() {
		InputMethodManager inputMethodManager=(InputMethodManager) Application.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
	}

	public static Date getBuildDate() {
		try{
				ApplicationInfo ai = mInstance.getPackageManager().getApplicationInfo(mInstance.getPackageName(), 0);
				ZipFile zf = new ZipFile(ai.sourceDir);
				ZipEntry ze = zf.getEntry("classes.dex");
				long time = ze.getTime();
				return new Date(time);
				//return SimpleDateFormat.getInstance().format(new java.util.Date(time));

		}catch(Exception e){
				return new Date(0);
		}
	}

    public static Application getInstance(){
        return mInstance;
    }

}
