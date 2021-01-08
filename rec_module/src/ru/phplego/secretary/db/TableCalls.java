package ru.phplego.secretary.db;

import ru.phplego.core.db.ActiveField;
import ru.phplego.core.db.ActiveTable;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 4:09
*/

public class TableCalls extends ActiveTable {
	private static int ord;

	public static ActiveField title =
	new ActiveField(++ord, "text",      null, false, 0)
	;

	public static ActiveField phone =
	new ActiveField(++ord, "text",      null, true, 0)
	;

	public static ActiveField note =
	new ActiveField(++ord, "text",      null, true, 0)
	;

	public static ActiveField created =
	new ActiveField(++ord, "integer",   null, true, 0)
	;

	public static ActiveField modified =
	new ActiveField(++ord, "integer",   null, true, 0)
	;

	public static ActiveField filename =
	new ActiveField(++ord, "text",      null, false, 0)
	;

	public static ActiveField incoming =
	new ActiveField(++ord, "integer",   null, false, 0)
	;

	public static ActiveField duration =
	new ActiveField(++ord, "integer",   null, false, 0)
	;

	public static ActiveField etc =
	new ActiveField(++ord, "text",      null, false, 0)
	;

	public static ActiveField planned =
	new ActiveField(++ord, "integer",   null, true, 5)
	;

	public static ActiveField alerted =
	new ActiveField(++ord, "integer",   null, false, 6)
	;

	public static ActiveField search =
	new ActiveField(++ord, "text",      null, true, 9)
	;

	public static ActiveField favourite =
	new ActiveField(++ord, "integer",   null, false, 10)
	;

	public static final String name ="calls";

	public TableCalls() {
		super(name);
	}

}
