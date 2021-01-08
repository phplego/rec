package ru.phplego.secretary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.annotation.ReportsCrashes;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.db.ActiveTable;
import ru.phplego.core.db.Database;
import ru.phplego.core.EManager;
import ru.phplego.core.db.DatabaseHelper;
import ru.phplego.secretary.db.*;
import ru.phplego.secretary.debug.Crash;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.media.CallRecorder;
import ru.phplego.secretary.media.MyPlayer;
import ru.phplego.secretary.etc.Humanist;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import ru.phplego.secretary.media.MyPlayer.OnProgressListenter;
import ru.phplego.core.AndroidUtils;

/**
* Created by IntelliJ IDEA.
* User: Oleg
* Date: 12.04.12
* Time: 20:59
* To change this template use File | Settings | File Templates.
* Ключ отправки отчетов (новый) добавлен 20 апреля 2012 в версии 6.9
*/

@ReportsCrashes(formKey = "", // will not be used
		formUri = "http://virtualsecretaryandroid.appspot.com/virtualsecretaryandroid"
		//formUriBasicAuthLogin = "yourlogin", // optional
		//formUriBasicAuthPassword = "y0uRpa$$w0rd", // optional
		//mode = ReportingInteractionMode.TOAST,
		//resToastText = R.string.help
)
public class App extends ru.phplego.core.Application {
	private static SQLiteDatabase database;

	private static MyPlayer mMyPlayer;

	private static EManager mEManager =new EManager();

	public void onCreate() {
		// Привет
		ACRA.init(this);
		super.onCreate();
		Humanist.setContext(getContext());

		initDatabase(); // Инициализируем базу данных

		mMyPlayer = new MyPlayer();

		ErrorReporter.getInstance().putCustomData("StartTime", new Date().toLocaleString());
		ErrorReporter.getInstance().putCustomData("AAC_SUPPORTED", CallRecorder.AAC_SUPPORTED ? "1" : "0");
		ErrorReporter.getInstance().putCustomData("SDCARD_FREE_SPACE", AndroidUtils.getFreeSpaceOnSD_str());
		Crash.update();

		// Если Samsung Galaxy S2 с андроидом ниже 4
		if(Device.isSamsungGalaxyS2() && !Device.isAndroid4OrHigher()){
				// Устанавливаем источник записи "Исходящий поток"
				String val = Prefs.list_preference_recording_audio_source.get(""+MediaRecorder.AudioSource.VOICE_UPLINK);
				Prefs.list_preference_recording_audio_source.put(val);
		}

		// Хак, который справляет старый путь к папке на новый
		Prefs.getRecordsPath();

        // Запускаем сервис
        if(Prefs.checkbox_preference_resident.get(true)) startService(new Intent(this, AService.class));

    }

	public void onTerminate() {
		Log.d("OnTerminate");
		Database.getDatabase().close();
		ClipboardMonitor.stop_();
		super.onTerminate();
	}

	public static void initDatabase() {
		ActiveTable[] tables = {new TableCalls(), new TableNotes(), new TableClipboard()};
		new DatabaseHelper(getContext(), "callrecording.db", tables, 12);
	}

	public static int getIcon() {
		return Prefs.getApplicationIcon(App.getContext());
	}

	public static Resources getRes() {
		return getContext().getResources();
	}

	public static String getStr(int resId) {
		return getRes().getString(resId);
	}

	public static MyPlayer getPlayer() {
		return mMyPlayer;
	}

	public static EManager getEManager() {
		return mEManager;
	}

	public static void restart() {
		Intent i = getContext().getPackageManager()
						.getLaunchIntentForPackage( getContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getContext().startActivity(i);
	}

	public static View inflate(int res) {
		LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(res, null, false);
		return view;
	}

	public static void turnOffOtherCallRecording() {
		final List<String> installedPackages = App.getOtherCallRecordingPackagesInstalled();
		// Если не установлен другой секретарь - выходим
		if(installedPackages.size() == 0) return;

		// Для каждого установленного "другого" секретаря
		for(String package_name: installedPackages){
				Intent intent = new Intent(package_name+"."+AService.class.getSimpleName());
				ServiceConnection connection = new ServiceConnection() {
						@Override
						public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
								Parcel data = Parcel.obtain();
								Parcel reply = Parcel.obtain();
								try{
										boolean res = iBinder.transact(AService.TRANSACT_CODE_DISABLE, data, reply, 0);
								}catch (RemoteException e){
										Crash.send(e);
										Log.d(e);
								}
								App.getContext().unbindService(this);
						}
						@Override
						public void onServiceDisconnected(ComponentName componentName) {
								App.toast("onServiceDisconnected");
						}
				};
				boolean bRes = App.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
		}
	}

	public static boolean isFirstLaunch() {
		ActiveQuery ac = new ActiveQuery();
		ac.from(new ActiveCall());
		// Если база пуста _И_ не установлена настройка "Первый запуск = false"
		return ac.count() == 0 && Prefs.is_first_launch.get(true);
	}

	public static List <String> getOtherCallRecordingPackages() {
		Vector<String> ret = new Vector<String>();

		if(getInstance().getApplicationContext().getPackageName().equals("ru.readyscript.secretarypro")){
			ret.add("ru.readyscript.secretary");
			return ret;
		}

		if(getInstance().getApplicationContext().getPackageName().equals("ru.readyscript.secretary")){
			ret.add("ru.readyscript.secretarypro");
			return ret;
		}

		if(getInstance().getApplicationContext().getPackageName().equals("ru.phplego.recpro")){
			ret.add("ru.phplego.recfree");
			return ret;
		}

		if(getInstance().getApplicationContext().getPackageName().equals("ru.phplego.recfree")){
			ret.add("ru.phplego.recpro");
			return ret;
		}

		return ret;
	}

	public static List <String> getOtherCallRecordingPackagesInstalled() {
		List<String> all = App.getOtherCallRecordingPackages();
		Vector<String> ret = new Vector<String>();
		for(String one: all){
				if(isPackageInstalled(one)) ret.add(one);
		}
		return ret;
	}

	public static boolean isVersionPro() {
		return !App.isVersionFree();
	}

	public static boolean isVersionFree() {
        Log.d("pkg = "+getInstance().getApplicationContext().getPackageName());

		return !getInstance().getApplicationContext().getPackageName().endsWith("pro");
	}

	public void getPackagePro() {
	}

}
