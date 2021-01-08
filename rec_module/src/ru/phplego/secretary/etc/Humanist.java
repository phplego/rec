package ru.phplego.secretary.etc;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 06.01.12
* Time: 23:39
* To change this template use File | Settings | File Templates.
*/

public class Humanist {
	static Context context;

	public static void setContext(Context c) {
		context = c;
	}

	public static String date(String format, long timestamp) {
		return date(format, new Date(timestamp));
	}

	public static String date(String format, Date date) {
		long current = System.currentTimeMillis();
		if(date == null) date = new Date(current);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String raw = sdf.format(date);
		return raw;
	}

	public static String date(String format) {
		return date(format, null);
	}

	public static String getDuration(long miliseconds) {
		long seconds = miliseconds/1000;
		if(seconds < 60) return seconds+"s";
		long min = seconds / 60;
		long sec = seconds % 60;

		return min+"m "+sec+"s";
	}

}
