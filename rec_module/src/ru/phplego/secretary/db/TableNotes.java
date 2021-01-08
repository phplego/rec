package ru.phplego.secretary.db;

import ru.phplego.core.db.ActiveField;
import ru.phplego.core.db.ActiveTable;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 4:09
*/

public class TableNotes extends ActiveTable {
	private static int ord;

	public static ActiveField title =
	new ActiveField(++ord, "text",      null, true, 0)
	;

	public static ActiveField text =
	new ActiveField(++ord, "text",      null, true, 0)
	;

	public static ActiveField created =
	new ActiveField(++ord, "integer",   null, true, 0)
	;

	public static ActiveField modified =
	new ActiveField(++ord, "integer",   null, true, 0)
	;

	public static final String name ="notes";

	public TableNotes() {
		super(name, 11);
	}

}
