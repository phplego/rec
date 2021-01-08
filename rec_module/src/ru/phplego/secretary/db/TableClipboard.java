package ru.phplego.secretary.db;

import ru.phplego.core.db.ActiveField;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.db.ActiveRecord;
import ru.phplego.core.db.ActiveTable;
import java.util.Vector;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 4:09
*/

public class TableClipboard extends ActiveTable {
	private static int ord;

	public static ActiveField text =
	new ActiveField(++ord, "text",      null, true, 0)
	;

	public static ActiveField created =
	new ActiveField(++ord, "integer",   null, true, 0)
	;

	public static ActiveField modified =
	new ActiveField(++ord, "integer",   null, true, 0)
	;

	public static final String name ="clipboard";

	public TableClipboard() {
		super(name, 12);
	}

	public static void deleteSameRecords(String textForSearch) {
		ActiveQuery q = new ActiveQuery();
		q.from(new ActiveClipboardRecord());
		q.where(text + " = ?", new String[]{textForSearch});
		Vector<ActiveRecord> items = q.objects();
		for(ActiveRecord r: items){
				r.delete();
		}
	}

	public static void deleteAllRecords() {
		ActiveQuery q = new ActiveQuery();
		q.from(new ActiveClipboardRecord());
		Vector<ActiveRecord> items = q.objects();
		for(ActiveRecord r: items){
				r.delete();
		}
	}

}
