package ru.phplego.secretary.db;

import ru.phplego.core.date.Date;
import ru.phplego.core.db.ActiveRecord;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 15.01.12
* Time: 0:50
* To change this template use File | Settings | File Templates.
*/

public class ActiveClipboardRecord extends ActiveRecord {
	public String getTableName() {
		return TableClipboard.name;
	}

	public ActiveClipboardRecord() {
		super();
	}

	public String getText() {
		return get(TableClipboard.text);
	}

	public void setText(String text) {
		set(TableClipboard.text, text);
	}

	public long getCreatedTime() {
		return getLong(TableClipboard.created);
	}

	public Date getCreatedDate() {
		return new Date(getCreatedTime());
	}

	public long insert() {
		set(TableClipboard.created, System.currentTimeMillis());
		return super.insert();
	}

}
