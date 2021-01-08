package ru.phplego.secretary.db;

import ru.phplego.core.date.Date;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.db.ActiveRecord;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 15.01.12
* Time: 0:50
* To change this template use File | Settings | File Templates.
*/

public class ActiveNote extends ActiveRecord {
	public String getTableName() {
		return TableNotes.name;
	}

	public ActiveNote() {
		super();
	}

	public static ActiveNote getLast() {
		ActiveQuery<ActiveNote> ac = new ActiveQuery();
		ac.from(new ActiveNote());
		ac.orderby(TableNotes._id + " desc");
		ActiveNote last = ac.object();
		return last;
	}

	/*public ActiveNote(long id) {
		super(id);
	}*/
	public String getText() {
		return get(TableNotes.text);
	}

	public void setText(String text) {
		set(TableNotes.text, text);
	}

	public long getCreatedTime() {
		return getLong(TableNotes.created);
	}

	public Date getCreatedDate() {
		return new Date(getCreatedTime());
	}

	public long getModifiedTime() {
		return getLong(TableNotes.modified);
	}

	public Date getModifiedDate() {
		return new Date(getModifiedTime());
	}

}
