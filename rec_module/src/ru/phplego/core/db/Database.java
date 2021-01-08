package ru.phplego.core.db;

import android.database.sqlite.SQLiteDatabase;
import ru.phplego.core.Utils;
import ru.phplego.core.debug.Log;
import java.io.*;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 14.01.12
* Time: 18:37
* To change this template use File | Settings | File Templates.
*/
public class Database {
	protected static SQLiteDatabase database =null;

	public static SQLiteDatabase getDatabase() {
		return database;
	}

	public static void setDatabase(SQLiteDatabase db) {
		database = db;
	}

	public static boolean exportToFile(String dest_file_name) {
		if(dest_file_name == null) return false;
		if(dest_file_name.length() == 0) return false;
		File fSource = new File(Database.getDatabase().getPath());
		Utils.copyfile(getDatabase().getPath(), dest_file_name);
		File fDest = new File(dest_file_name);
		return fDest.length() == fSource.length(); // Если размеры совпадают
	}

	public static boolean importFromFile(String db_file_name) {
		if(db_file_name == null) return false;
		if(db_file_name.length() == 0) return false;
		if(!isSQLiteDatabaseFile(db_file_name)) return false;
		File fSource = new File(db_file_name);
		Utils.copyfile(db_file_name, getDatabase().getPath());
		File fDest = new File(getDatabase().getPath());
		boolean ok = fDest.length() == fSource.length(); // Если размеры совпадают
		return ok;
	}

	public static boolean isSQLiteDatabaseFile(String file_name) {
		if(file_name == null) return false;
		if(file_name.length() == 0) return false;
		File fSource = new File(file_name);
		try{
				InputStream fosSource = new FileInputStream(fSource);
				byte [] signature = new byte[6];
				fosSource.read(signature);
				fosSource.close();
				String sSignature = new String(signature);
				Log.d(sSignature);
				if("SQLite".equals(sSignature)) return true;
		}catch (IOException e){
				return false;
		}
		return false;
	}

	public static byte [] exportToByteArray() {
		File fSource = new File(getDatabase().getPath());
		if(!fSource.canRead()) return null;
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.toString().toCharArray();

		try{
				InputStream fosSource = new FileInputStream(fSource);
				byte[] result = new byte[0];
				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = fosSource.read(buf)) > 0){
						int start_position = result.length;
						byte [] resultBigger = new byte[result.length + len]; // Увеличиваем массив
						System.arraycopy(result, 0, resultBigger, 0, result.length); // Копируем прежнее содержимое
						result = resultBigger; // теперь result стал больше на len байт
						System.arraycopy(buf, 0, result, start_position, len); // дополняем прочитанными байтами
				}
				if(result.length != fSource.length()) return null;
				return result;

		}catch (IOException e){
				//Crash.send(e);
				return null;
		}
	}

	public static boolean importFromByteArray(byte [] byteArray) {
		File fDest = new File(getDatabase().getPath());
		if(!fDest.canWrite()) return false;
		try{
				OutputStream fosDest = new FileOutputStream(fDest);
				fosDest.write(byteArray);
				fosDest.close();
		}catch (IOException e){
				//Crash.send(e);
				return false;
		}
		return fDest.length() == byteArray.length; // Если размеры совпадают
	}

}
