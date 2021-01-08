package ru.phplego.secretary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import ru.phplego.core.Application;
import ru.phplego.core.EManager;
import ru.phplego.secretary.db.ActiveClipboardRecord;
import ru.phplego.secretary.db.TableClipboard;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 10.09.12
* Time: 8:53
*/

public class ClipboardMonitor {
	private static final int PERIOD =1000;

	private static String mClipboardText;

	private static boolean mIsRunning =false;

	private static Handler mHandler =new Handler();

	public static class EventClipboardChanged extends EManager.Event {
	}
	private static void doWork() {
		ClipboardManager mClipboard = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		if (mClipboard.hasText()) {
				String current = mClipboard.getText().toString();
				//Log.d("tick");

				// Если Текст изменился и не пустой
				if(!current.equals(mClipboardText) && current.length() > 0){

						mClipboardText = mClipboard.getText().toString();

						// Удаляем записи с таким же текстом
						TableClipboard.deleteSameRecords(mClipboardText);

						// Вставляем новую запись
						ActiveClipboardRecord r = new ActiveClipboardRecord();
						r.setText(mClipboardText);
						r.insert();

						// Бросаем событие "Буфер обмена изменен"
						App.getEManager().riseEvent(new EventClipboardChanged(), null);
				}

		}
	}

	private static Runnable mRunnable =
	new Runnable() {
		@Override
		public void run() {
			doWork();
			postDelayed();
		}
	}
	;

	private static void postDelayed() {
		mHandler.postDelayed(mRunnable, PERIOD);
	}

	public static void run_() {
		if(mIsRunning) return;
		mIsRunning = true;
		postDelayed();
	}

	public static void stop_() {
		if(!mIsRunning) return;
		mHandler.removeCallbacks(mRunnable);
		mIsRunning = false;
	}

	public static void onServiceCreate() {
		// Если выключен весь секретарь
		if(!Prefs.checkbox_preference_resident.get(true)) return;

		// Если модуль истории буфера обмена включен
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		boolean enabled  = p.getBoolean("PageClipboard.mIsEnabled", false);
		if(enabled){
				// Поток, проверяющий Буфер обмена на предмет нового текста
				ClipboardMonitor.run_();
		};
	}

	public static void onServiceDestroy() {
		stop_();
	}

}
