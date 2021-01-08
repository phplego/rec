package ru.phplego.secretary.db.system;

import ru.phplego.core.date.Date;
import ru.phplego.core.db.ActiveRecord;

public class ActiveSystemSMS extends ActiveRecord {
	public Date getDate() {
		return new Date(getLong("date"));
	}

	public String getText() {
		return get("body");
	}

}
