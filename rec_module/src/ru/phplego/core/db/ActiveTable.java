package ru.phplego.core.db;

import android.provider.BaseColumns;
import ru.phplego.core.StringUtils;
import ru.phplego.core.debug.Log;
import java.lang.reflect.Field;
import java.util.*;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 2:46
*/

public class ActiveTable {
	public static ActiveField _id =
	new ActiveField(0, "integer", "primary key", false, 0)
	;

	private String mName;

	private int mVersion =0;

	private LinkedHashMap <String, ActiveField> mFields =
	new LinkedHashMap<String, ActiveField>()
	;

	public ActiveTable(String name, int version) {
		mName = name;
		mVersion = version;

		Vector<ActiveField> unsortedFields = new Vector<ActiveField>();

		// Переносим переменные класса в коллекцию полей mFields
		for(Field field: this.getClass().getFields()){
				try{
						Object fieldValue = field.get(this);
						if(fieldValue instanceof ActiveField){
								ActiveField activeField = (ActiveField)fieldValue;
								// Имя переменной класса становится именем поля таблицы:
								activeField.setName(field.getName());
								unsortedFields.add(activeField);
						}
				}catch (Exception e){
						Log.d(e);
				}
		}
		Collections.sort(unsortedFields);
		for(ActiveField af: unsortedFields){
				addField(af);
		}
	}

	public ActiveTable(String name) {
		this(name, 0);
	}

	public String getName() {
		return mName;
	}

	protected void addField(ActiveField filed) {
		mFields.put(filed.getName(), filed);
	}

	public Vector <String> getCreateQueries() {
		Vector<String> queries = new Vector<String>();

		// Запрос создания таблицы
		queries.add(getCreateTableQuery());

		// Запросы создания индексов
		queries.addAll(getCreateIndexesQueries());

		return queries;
	}

	public String getCreateTableQuery() {
		String sql = "CREATE TABLE IF NOT EXISTS " + mName + " (\n";
		Vector<String> lines = new Vector<String>();
		for(ActiveField field: mFields.values()){
				String line = field.getName() + " " + field.getType();
				if(field.getExtra() != null) line += " " + field.getExtra();
				lines.add(line);
		}
		sql += StringUtils.join(lines, ",\n");
		sql += "\n)";
		return sql;
	}

	public Vector <String> getCreateIndexesQueries() {
		Vector<String> queries = new Vector<String>();
		for(ActiveField field: mFields.values()){
				if(field.isIndex() && !field.getName().equals(BaseColumns._ID)){
						queries.add("CREATE INDEX IF NOT EXISTS " + field.getName() + " ON " + mName + " (" + field.getName() + ")");
				}
		}
		return queries;
	}

	public Vector <String> getUpgradeQueries(int old_version) {
		Vector<String> queries = new Vector<String>();
		for(ActiveField field: mFields.values()){
				// Если версия в которой добавлено поле больше версии текущей структуры базы
				if(field.getVersion() > old_version){
						String alterQuery = "ALTER TABLE " + mName + " ADD COLUMN " + field.getName() + " " + field.getType();
						if(field.getExtra() != null) alterQuery += " " + field.getExtra();
						queries.add(alterQuery);

						if(field.isIndex()){
								queries.add("CREATE INDEX IF NOT EXISTS " + field.getName() + " ON " + mName + " (" + field.getName() + ")");
						}
				}
		}

		if(mVersion > old_version)
				queries.addAll(getCreateQueries());
		return queries;
	}

}
