package ru.phplego.secretary;

import android.content.Context;
import android.os.Environment;
import ru.phplego.core.preferences.SmartPrefBoolean;
import ru.phplego.core.preferences.SmartPrefInt;
import ru.phplego.core.preferences.SmartPrefString;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 20.04.12
* Time: 19:09
* To change this template use File | Settings | File Templates.
*/

public class Prefs {
	public static SmartPrefInt theme =
	new SmartPrefInt(Prefs.class, false)
	;

	public static SmartPrefBoolean checkbox_preference_resident =
	new SmartPrefBoolean(Prefs.class, false)
	;

	public static SmartPrefBoolean checkbox_preference_trayicon =
	new SmartPrefBoolean(Prefs.class, false)
	;

	public static SmartPrefBoolean checkbox_preference_corrupt_extension =
	new SmartPrefBoolean(Prefs.class, false)
	;

	public static SmartPrefBoolean developer_mode =
	new SmartPrefBoolean(Prefs.class, false)
	;

	public static SmartPrefBoolean is_first_launch =
	new SmartPrefBoolean(Prefs.class, false)
	;

	public static SmartPrefString app_icon =
	new SmartPrefString(Prefs.class, false)
	;

	public static SmartPrefString list_preference_recording =
	new SmartPrefString(Prefs.class, false)
	;

	public static SmartPrefString list_preference_recording_audio_source =
	new SmartPrefString(Prefs.class, false)
	;

	public static SmartPrefString list_preference_recording_format =
	new SmartPrefString(Prefs.class, false)
	;

	public static SmartPrefString list_preference_recording_bitrate =
	new SmartPrefString(Prefs.class, false)
	;

	public static SmartPrefString records_path =
	new SmartPrefString(Prefs.class, false)
	;

	public static SmartPrefString modules_order =
	new SmartPrefString(Prefs.class, false)
	;

	public static int getApplicationIcon(Context context) {
		String r = app_icon.get("");
		// r = "res/drawable/ic_launcher.png"
		r = r.replace(".png", "");
		r = r.replace("res/drawable/", "");
		int imageResource = App.getContext().getResources().getIdentifier(r, "drawable", App.getContext().getPackageName());
		if(imageResource != 0) return imageResource;
		return R.drawable.icon_margin;
	}

	public static String getRecordsPath() {
		String path = records_path.get();
		if(path.length() == 0){
				path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CallRecording";
				records_path.put(path);
		}

		// Если путь указан относительный, переделаем его в абсолютный
		if(!path.startsWith("/")){
				path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path;
				records_path.put(path);
		}
		return path;
	}

}
