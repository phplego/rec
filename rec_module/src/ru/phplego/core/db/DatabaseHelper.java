package ru.phplego.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.phplego.core.debug.Log;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 3:51
*/
public class DatabaseHelper extends SQLiteOpenHelper {
	private ActiveTable [] mTables;

	public DatabaseHelper(Context context, String name, ActiveTable [] tables, int version) {
		super(context, name, null, version);
		mTables = tables;
		Database.setDatabase(getWritableDatabase()); // Расшариваем базу для всех наших клссов Active...
	}

	public void onCreate(SQLiteDatabase db) {
		for(ActiveTable activeTable: mTables){
				for(String createSql: activeTable.getCreateQueries()){
						try{
								db.execSQL(createSql);
						}catch (Exception e){
								//Crash.send(e);
						}
						Log.d(createSql);
				}
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for(ActiveTable activeTable: mTables){
				for(String upradeSql: activeTable.getUpgradeQueries(oldVersion)){
						try{
								db.execSQL(upradeSql);
						}catch (Exception e){
								//Crash.send(e);
						}
						Log.d(upradeSql);
				}
		}
	}

}
