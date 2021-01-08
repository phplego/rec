package ru.phplego.core.debug;

import android.text.TextUtils;

/**
* Extended logger.
*
* @author A.Tsvetkov 2010 http://tsvetkov.at.ua mailto:al@ukr.net
*/

public final class Log {
	private static final String POSFIX_STRING =")";

	private static final String PREFIX_STRING ="> (";

	private static final String PREFIX_MAIN_STRING =" ~~~ ";

	/**
	* Send a VERBOSE log message.
	*
	* @param msg The message you would like logged.
	*/
	public static void v(String msg) {
		android.util.Log.v(getLocation(), msg);
	}

	/**
	* Send a DEBUG log message.
	*
	* @param msg The message you would like logged.
	*/
	public static void d(String msg) {
		android.util.Log.d(getLocation(), msg);
	}

	/**
	* Send a INFO log message.
	*
	* @param msg The message you would like logged.
	*/
	public static void i(String msg) {
		android.util.Log.i(getLocation(), msg);
	}

	/**
	* Send a WARN log message.
	*
	* @param msg The message you would like logged.
	*/
	public static void w(String msg) {
		android.util.Log.w(getLocation(), msg);
	}

	/**
	* Send a ERROR log message.
	*
	* @param msg The message you would like logged.
	*/
	public static void e(String msg) {
		android.util.Log.e(getLocation(), msg);
	}

	// ==========================================================

	/**
	* Send a VERBOSE log message and log the exception.
	*
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void v(String msg, Exception e) {
		android.util.Log.v(getLocation(), msg, e);
	}

	/**
	* Send a DEBUG log message and log the exception.
	*
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void d(String msg, Exception e) {
		android.util.Log.d(getLocation(), msg, e);
	}

	/**
	* Send a INFO log message and log the exception.
	*
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void i(String msg, Exception e) {
		android.util.Log.i(getLocation(), msg, e);
	}

	/**
	* Send a WARN log message and log the exception.
	*
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void w(String msg, Exception e) {
		android.util.Log.w(getLocation(), msg, e);
	}

	/**
	* Send a ERROR log message and log the exception.
	*
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void e(String msg, Exception e) {
		android.util.Log.e(getLocation(), msg, e);
	}

	// ==========================================================

	/**
	* Send a VERBOSE log the exception.
	*
	* @param e An exception to log
	*/
	public static void v(Exception e) {
		android.util.Log.v(getLocation(), "", e);
	}

	/**
	* Send a DEBUG log the exception.
	*
	* @param e An exception to log
	*/
	public static void d(Exception e) {
		android.util.Log.d(getLocation(), "", e);
	}

	/**
	* Send a INFO log the exception.
	*
	* @param e An exception to log
	*/
	public static void i(Exception e) {
		android.util.Log.i(getLocation(), "", e);
	}

	/**
	* Send a WARN log the exception.
	*
	* @param e An exception to log
	*/
	public static void w(Exception e) {
		android.util.Log.w(getLocation(), "", e);
	}

	/**
	* Send a ERROR log the exception.
	*
	* @param e An exception to log
	*/
	public static void e(Exception e) {
		android.util.Log.e(getLocation(), "", e);
	}

	// ==========================================================

	/**
	* Send a <b>VERBOSE</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag.
	* Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumberClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	*/
	public static void v(Object obj, String msg) {
		android.util.Log.v(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}

	/**
	* Send a <b>DEBUG</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag.
	* Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	*/
	public static void d(Object obj, String msg) {
		android.util.Log.d(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}

	/**
	* Send a <b>INFO</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag.
	* Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	*/
	public static void i(Object obj, String msg) {
		android.util.Log.i(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}

	/**
	* Send a <b>WARN</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag.
	* Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	*/
	public static void w(Object obj, String msg) {
		android.util.Log.w(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}

	/**
	* Send a <b>ERROR</b> log message. Using when you extend any Class and wont to receive full info in LogCat tag.
	* Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	*/
	public static void e(Object obj, String msg) {
		android.util.Log.e(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg);
	}

	// ==========================================================

	/**
	* Send a <b>VERBOSE</b> log message and log the exception. Using when you extend any Class and wont to receive full
	* info in LogCat tag. Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void v(Object obj, String msg, Exception e) {
		android.util.Log.v(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}

	/**
	* Send a <b>DEBUG</b> log message and log the exception. Using when you extend any Class and wont to receive full
	* info in LogCat tag. Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void d(Object obj, String msg, Exception e) {
		android.util.Log.d(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}

	/**
	* Send a <b>INFO</b> log message and log the exception. Using when you extend any Class and wont to receive full
	* info in LogCat tag. Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void i(Object obj, String msg, Exception e) {
		android.util.Log.i(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}

	/**
	* Send a <b>WARN</b> log message and log the exception. Using when you extend any Class and wont to receive full
	* info in LogCat tag. Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param msg The message you would like logged.
	* @param e An exception to log
	*/
	public static void w(Object obj, String msg, Exception e) {
		android.util.Log.w(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}

	/**
	* Send a <b>ERROR</b> log message and log the exception. Using when you extend any Class and wont to receive full
	* info in LogCat tag. Usually you can use "this" in "obj" parameter. As result you receive tag string
	* "<b>(Called Main Class) LogginClass:MethodInLogginClass:lineNumber</b>"
	*
	* @param obj main class
	* @param e An exception to log
	* @param msg The message you would like logged.
	*/
	public static void e(Object obj, String msg, Exception e) {
		android.util.Log.e(PREFIX_STRING + obj.getClass().getSimpleName() + POSFIX_STRING + getLocation(), msg, e);
	}

	private static String getLocation() {
		final String className = Log.class.getName();
		final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		boolean found = false;
		for (int i = 0; i < traces.length; i++) {
				StackTraceElement trace = traces[i];

				try {
						if (found) {
								if (!trace.getClassName().startsWith(className)) {
										Class<?> clazz = Class.forName(trace.getClassName());
										return PREFIX_MAIN_STRING + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber();
								}
						} else if (trace.getClassName().startsWith(className)) {
								found = true;
								continue;
						}
				} catch (ClassNotFoundException e) {
				}
		}
		return "[]: ";
	}

	private static String getClassName(Class <?> clazz) {
		if (clazz != null) {
				if (!TextUtils.isEmpty(clazz.getSimpleName())) {
						return clazz.getSimpleName();
				}

				return getClassName(clazz.getEnclosingClass());
		}

		return "";
	}

	private static long mTimePoint =0;

	public static void timePoint(String name) {
		if(mTimePoint == 0){
				mTimePoint = System.currentTimeMillis();
				return;
		}

		d("Time point ["+name+"] time: "+(System.currentTimeMillis() - mTimePoint) + " ms");
		mTimePoint = System.currentTimeMillis();
	}

	public static void timePoint() {
		timePoint("");
	}

}
