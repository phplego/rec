package ru.phplego.core.date;

import ru.phplego.core.Application;
import ru.phplego.core.Res;
import java.text.SimpleDateFormat;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 08.06.12
* Time: 20:10
*/

public class Date extends java.util.Date {
	public Date() {
		super();
	}

	public Date(long createdTime) {
		super(createdTime);
	}

	public Date(java.util.Date createdTime) {
		super(createdTime.getTime());
	}

	/*
	"yyyy"                      - 2012
	"d MMMMMMMMMMMMMMM"         - 10 апреля
	"E"                         - Пн
	"EEEEEEE"                   - Понедельник
	"HH:mm"                     - 23:59
	D 	day in year 	        (Number) 	189
	E 	day of week 	        (Text) 	    Tuesday
	F 	day of week in month 	(Number) 	2 (2nd Wed in July)
	G 	era designator 	        (Text) 	    AD
	H 	hour in day             (0-23) 	(Number)
	K 	hour in am/pm           (0-11) 	(Number)
	L 	stand-alone month 	    (Text/Number) 	July / 07
	M 	month in year 	        (Text/Number) 	July / 07
	S 	fractional seconds 	    (Number) 	978
	W 	week in month 	        (Number) 	2
	Z 	time zone               (RFC 822) 	    (Timezone) 	-0800
	a 	am/pm marker 	        (Text) 	        PM
	c 	stand-alone day of week (Text/Number) 	Tuesday / 2
	d 	day in month 	        (Number) 	    10
	h 	hour in am/pm (1-12) 	(Number) 	    12
	k 	hour in day (1-24) 	    (Number)    	24
	m 	minute in hour 	        (Number) 	    30
	s 	second in minute 	    (Number) 	    55
	w 	week in year 	        (Number) 	    27
	y 	year 	                (Number) 	    2010
	z 	time zone 	            (Timezone) 	    Pacific Standard Time
	' 	escape for text 	    (Delimiter) 	'Date='
	'' 	single quote 	        (Literal) 	    'o''clock'
	 */
	public String format(String fmt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(this);
	}

	/**
	*
	* @param fmt Общая форма, например "%, HH:mm" - Сегодня, 23:59. Долежн содержать символ %
	* @param date_fmt То что будет подставлено вместо символа % если это не сегодня, не вчера и не завтра.
	*                 обычно "dd.MM.yyyy" (25.12.1984) или "dd MMMMMMM" (10 апреля)
	* @return
	*/
	public String format(String fmt, String date_fmt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		SimpleDateFormat sdf_date = new SimpleDateFormat(date_fmt);
		String date_string = sdf_date.format(this);
		String total_string = sdf.format(this);

		if(isToday())      date_string = Application.getContext().getString(R.string.today);
		if(isYesterday())  date_string = Application.getContext().getString(R.string.yesterday);
		if(isTomorrow())   date_string = Application.getContext().getString(R.string.tomorrow);

		total_string = total_string.replace("%", date_string);

		return total_string;
	}

	public boolean isToday() {
		return Date.isToday(this);
	}

	public boolean isYesterday() {
		return Date.isYesterday(this);
	}

	public boolean isTomorrow() {
		return Date.isTomorrow(this);
	}

	public static boolean isToday(java.util.Date date) {
		java.util.Date test = new java.util.Date();
		return  date.getDate () == test.getDate () &&
						date.getMonth() == test.getMonth() &&
						date.getYear() == test.getYear();
	}

	public static boolean isYesterday(java.util.Date date) {
		java.util.Date test = new java.util.Date(System.currentTimeMillis() - (1000*60*60*24));
		return  date.getDate () == test.getDate () &&
						date.getMonth() == test.getMonth() &&
						date.getYear() == test.getYear();
	}

	public static boolean isTomorrow(java.util.Date date) {
		java.util.Date test = new java.util.Date(System.currentTimeMillis() + (1000*60*60*24));
		return  date.getDate () == test.getDate () &&
						date.getMonth() == test.getMonth() &&
						date.getYear() == test.getYear();
	}

}
