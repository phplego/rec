package ru.phplego.core.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.phplego.core.Cachable;
import java.util.*;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 14.01.12
* Time: 18:36
* To change this template use File | Settings | File Templates.
*/

public class ActiveRecord implements Map <String, String>, Cachable {
	static final String PRIMARY_KEY_FIELD_NAME ="_id";

	private boolean mIsDeleted;

	private String table;

	private static Hashtable <String, ActiveRecord> _active_records_cache =new Hashtable();

	private LinkedHashMap <String, String> data =
	new LinkedHashMap<String, String>()
	;

	private LinkedHashMap <String, String> data_modified =
	new LinkedHashMap<String, String>()
	;

	private LinkedHashSet <OnChangeListener> onChangeListeners =
	new LinkedHashSet<OnChangeListener>()
	;

	// Реализация интерфейса Map
	public void clear() {
		data.clear();
	}

	public boolean containsKey(Object o) {
		return data.containsKey(o);
	}

	public boolean containsValue(Object o) {
		return data.containsValue(o);
	}

	public String get(Object o) {
		return data.get(o);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Set <String> keySet() {
		return data.keySet();
	}

	public String remove(Object o) {
		return data.remove(o);
	}

	public int size() {
		return data.size();
	}

	public Collection <String> values() {
		return data.values();
	}

	public Set <Entry <String, String>> entrySet() {
		return data.entrySet();
	}

	public void putAll(Map <? extends String, ? extends String> map) {
		data.putAll(map);
		data_modified.putAll(map);
	}

	public String put(String key, String value) {
		data_modified.put(key, value);
		return data.put(key, value);
	}

	public boolean isInvalid() {
		return mIsDeleted;
	}

	public interface OnChangeListener {
		public void onChange(ActiveRecord activeRecord, String field);

		public Context getContext();

	}
	// Конец реализации интерфейса Map
	public String getTableName() {
		return table;
	}

	protected void setTableName(String value) {
		table = value;
	}

	public ActiveRecord() {
	}

	/*public ActiveRecord(long id){
		this.load(id);
	}*/
	public long getId() {
		if(!data.containsKey(PRIMARY_KEY_FIELD_NAME)) return 0;
		return getLong(PRIMARY_KEY_FIELD_NAME);
	}

	public boolean load(long id) {
		if(id == 0) return false;
		ActiveQuery q = new ActiveQuery();
		q.from(this);
		q.where("_id="+id);
		LinkedHashMap row = q.row();
		if(row.size() == 0) return false; //Запись не найдена
		this.loadFromMap(row);
		return true;
	}

	public void loadFromMap(Map <String, String> row) {
		this.data.putAll(row);
		_active_records_cache.put(getCacheKey(), this);
	}

    public void loadFromCursor(Cursor cursor){
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        for(int i = 0; i < cursor.getColumnCount(); i++){
            String name = cursor.getColumnName(i);
            String value = cursor.getString(i);
            map.put(name, value);
        }

        loadFromMap(map);
    }

	private String getCacheKey() {
		return getCacheKey(this.getId());
	}

	private String getCacheKey(long id) {
		return getCacheKey(this.getClass(), id);
	}

	private static String getCacheKey(Class clazz, long id) {
		return clazz.getName()+":"+id;
	}

	public static int getCacheSize() {
		return _active_records_cache.size();
	}

	public static ActiveRecord getFromCache(Class <? extends ActiveRecord> clazz, long id) {
		String key = getCacheKey(clazz, id);
		if(_active_records_cache.containsKey(key))
				return _active_records_cache.get(key);
		return null;
	}

	public ActiveRecord getFromCache(long id) {
		return getFromCache(this.getClass(), id);
	}

	public void saveToCache() {
		if(this.getId() == 0) return; //throw new Error("id cannot be 0");
		_active_records_cache.put(getCacheKey(), this);
	}

	public void removeFromCache() {
		_active_records_cache.remove(getCacheKey());
	}

	public static ActiveRecord getInstance(Class <? extends ActiveRecord> clazz, long id) {
		ActiveRecord object = getFromCache(clazz, id);
		if(object != null) return object;

		ActiveRecord newObject = null;
		try{
				newObject = clazz.newInstance();
				newObject.load(id);
		}
		catch (Exception e){
				//empty
		}
		assert newObject != null;
		newObject.saveToCache();
		return newObject;
	}

	public ActiveRecord getInstance(long id) {
		return getInstance(this.getClass(), id);
	}

	public void set(String field_name, String value, boolean not_modify) {
		if(data.containsKey(field_name) && data.get(field_name).equals(value)) return; //поле не имзенилось
		data.put(field_name, value);
		if(!not_modify) data_modified.put(field_name, value);
		if(!not_modify) for(OnChangeListener one: onChangeListeners) one.onChange(this, field_name);
	}

	public void set(String field_name, String value) {
		set(field_name, value, false);
	}

	public void set(String field_name, Long value) {
		set(field_name, value.toString(), false);
	}

	public void set(String field_name, boolean value) {
		set(field_name, value ? "1" : "0", false);
	}

	public String get(String field_name) {
		String ret = data.get(field_name);
		if(ret == null) return "";
		return ret;
	}

	public String get(String field_name, String default_value) {
		String val = get(field_name);
		if(val.length() == 0) return default_value;
		return val;
	}

	public int getInt(String field_name) {
		String value = data.get(field_name);
		try{
				return Integer.parseInt(value);
		}catch (Exception e){}
		return 0;
	}

	public long getLong(String field_name) {
		String value = data.get(field_name);
		try{
				return Long.parseLong(value);
		}catch (Exception e){}
		return 0;
	}

	public boolean getBool(String field_name) {
		return getInt(field_name) > 0;
	}

	// Методы для того чтобы не приводить ActiveField к строке
	public String get(ActiveField activeField) {
		return get(activeField.getName());
	}

	public String get(ActiveField activeField, String def) {
		return get(activeField.getName(), def);
	}

	public boolean getBool(ActiveField activeField) {
		return getBool(activeField.getName());
	}

	public int getInt(ActiveField activeField) {
		return getInt(activeField.getName());
	}

	public long getLong(ActiveField activeField) {
		return getLong(activeField.getName());
	}

	public void set(ActiveField activeField, String val) {
		set(activeField.getName(), val);
	}

	public void set(ActiveField activeField, boolean val) {
		set(activeField.getName(), val);
	}

	public void set(ActiveField activeField, long val) {
		set(activeField.getName(), val);
	}

	public long insert() {
		ContentValues values = new ContentValues();
		Iterator<String> iter = data_modified.keySet().iterator();
		while (iter.hasNext()) {
				String key = iter.next();
				values.put(key, data_modified.get(key));
		}
		SQLiteDatabase db = Database.getDatabase();
		long id = db.insert(getTableName(), null, values);
		this.set(PRIMARY_KEY_FIELD_NAME, ""+id, true); // Обновляем айди у текущего объекта
		saveToCache(); // Сохраняем в кеш
		return id;
	}

	public boolean update() {
		if(data_modified.isEmpty()) return false;
		ContentValues values = new ContentValues();
		Iterator<String> iter = data_modified.keySet().iterator();
		while (iter.hasNext()) {
				String key = iter.next();
				values.put(key, data_modified.get(key));
		}
		SQLiteDatabase db = Database.getDatabase();
		db.update(getTableName(), values, PRIMARY_KEY_FIELD_NAME+"="+getId(), null);
		return true;
	}

	public void delete() {
		SQLiteDatabase db = Database.getDatabase();
		db.delete(getTableName(), PRIMARY_KEY_FIELD_NAME+"="+getId(), null);
		mIsDeleted = true;
		removeFromCache();
	}

	public void setOnChangeListener(OnChangeListener listener) {
		onChangeListeners.add(listener);
	}

	public void removeOnChangeListener(OnChangeListener listener) {
		onChangeListeners.remove(listener);
	}

	public void removeOnChangeListeners(Context context) {
		HashSet forRemove = new HashSet();
		for(OnChangeListener one: onChangeListeners)
				if(context.equals(one.getContext()))
						forRemove.add(one);
		for(Object one: forRemove)
				onChangeListeners.remove((OnChangeListener)one);
	}

	public static void removeAllOnChangeListeners(Context context) {
		for(ActiveRecord a: _active_records_cache.values()){
				a.removeOnChangeListeners(context);
		}
	}

}
