package ru.phplego.secretary.debug;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.acra.ErrorReporter;
import ru.phplego.secretary.App;
import java.util.Date;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 13.04.12
* Time: 16:35
* To change this template use File | Settings | File Templates.
*/

public class Crash {
	public static void update() {
		ErrorReporter.getInstance().putCustomData("CrashDataUpdated", new Date().toLocaleString());
	}

	public static void send(Exception e) {
		ErrorReporter.getInstance().handleSilentException(e);
	}

	public static void put(String key, String val) {
		ErrorReporter.getInstance().putCustomData(key, val);
	}

}
