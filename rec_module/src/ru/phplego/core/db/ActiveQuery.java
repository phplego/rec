package ru.phplego.core.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import ru.phplego.core.StringUtils;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 14.01.12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */

public class ActiveQuery<ACTIVE_OBJECT_CLASS extends ActiveRecord> {
    private ActiveRecord from =null;

    private String [] select =null;

    private Vector <String> where =new Vector<String>();

    private Vector <String> whereArgs =new Vector<String>();

    private String groupby =null;

    private String orderby =null;

    private int limit =99999;

    private int offset =0;

    public ActiveQuery() {
    }

    public String [] select() {
        return select;
    }

    public ActiveRecord from() {
        return from;
    }

    public Vector <String> where() {
        return new Vector<String>(where);
    }

    public String groupby() {
        return groupby;
    }

    public String orderby() {
        return orderby;
    }

    public int limit() {
        return limit;
    }

    public void select(String [] value) {
        select = value;
    }

    public void select(String value) {
        select = new String[] {value};
    }

    public void from(ActiveRecord value) {
        from = value;
    }

    public void where(Vector <String> value) {
        where.clear();
        whereArgs.clear();
        where.addAll(value);
    }

    public void where(String value, String [] whereArguments) {
        where.clear();
        where.add(value);
        whereArgs.clear();
        if(whereArguments != null){
            for(String one: whereArguments)
                whereArgs.add(one);
        }
    }

    public void where(String value) {
        where(value, null);
    }

    public Vector <String> whereArgs() {
        return whereArgs;
    }

    public void groupby(String value) {
        groupby = value;
    }

    public void orderby(String value) {
        orderby = value;
    }

    public void limit(int value) {
        limit = value;
    }

    public void offset(int value) {
        offset = value;
    }

    public void and(String value) {
        where.add(value);
    }

    public void and(String value, String arg) {
        where.add(value);
        whereArgs.add(arg);
    }

    public void and(String value, String [] whereArguments) {
        where.add(value);
        if(whereArguments != null){
            for(String one: whereArguments)
                whereArgs.add(one);
        }
    }

    public Vector <ACTIVE_OBJECT_CLASS> objects() {
        //query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        String limit_str = ""+offset+","+limit;
        String where_str = "";
        if(where.size() > 0)  where_str = "("+ StringUtils.join(where, ") AND (")+")";

        Cursor cursor = Database.getDatabase().query(from().getTableName(), select, where_str, whereArgs.toArray(new String[0]), groupby, null, orderby, limit_str);
        Vector<ACTIVE_OBJECT_CLASS> objects = this.objects(cursor);
        cursor.close();
        return  objects;
    }

    public Vector <ACTIVE_OBJECT_CLASS> objects(Cursor cursor) {
        Vector<ActiveRecord> objects = new Vector<ActiveRecord>();
        cursor.moveToFirst();
        do{
            if(cursor.getCount() <= 0) break;

            Hashtable<String,String> row = new Hashtable<String, String>();
            for(int i = 0; i < cursor.getColumnCount(); i++){
                String field_name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                if(value == null) value = "";
                row.put(field_name, value);
            }
            long record_id = 0;
            try{
                record_id = Long.parseLong(row.get("_id"));
            }catch (Exception e){
                break;
            }

            ActiveRecord record = null;
            if(from != null)
                record = from.getFromCache(record_id);
            if(record == null)
                try{record = from().getClass().newInstance();}catch (Exception e){}
            if(record == null)
                record = new ActiveRecord();

            record.loadFromMap(row);
            record.saveToCache();
            objects.add(record);
        }while(cursor.moveToNext());

        return (Vector<ACTIVE_OBJECT_CLASS>) objects;
    }

    public LinkedHashMap <String, String> row() {
        LinkedHashMap<String, String> row = new LinkedHashMap<String, String> ();
        String where_str = StringUtils.join(where, " AND ");
        SQLiteDatabase db = Database.getDatabase();
        if(db == null) return row;
        if(from == null) return row;
        Cursor cursor = db.query(from.getTableName(), select, where_str, null, groupby, null, orderby, "1");
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getColumnCount(); i++){
                String field_name = cursor.getColumnName(i);
                String value = cursor.getString(i);
                if(value == null) value = "";
                row.put(field_name, value);
            }
        }
        cursor.close();
        return row;
    }

    public ACTIVE_OBJECT_CLASS object() {
        LinkedHashMap<String, String> row = row();
        if(row.size() == 0) return null;
        long record_id = 0;
        try{record_id = Long.parseLong(row.get(BaseColumns._ID));}catch (Exception e){}
        if(record_id == 0) return null;
        ActiveRecord record = null;
        if(from != null)
            record = from.getFromCache(record_id);
        if(record == null)
            try{record = from().getClass().newInstance();}catch (Exception e){}
        if(record == null)
            record = new ActiveRecord();

        record.loadFromMap(row);
        return (ACTIVE_OBJECT_CLASS)record;
    }

    public int count() {
        String[] oldSelect = select();
        select("COUNT(*)");
        LinkedHashMap row = row();
        select(oldSelect);
        if(row.size() == 0) return 0;
        try{
            return Integer.parseInt((String)row.values().toArray()[0]);
        }
        catch (Exception e){}
        return 0;
    }

}
