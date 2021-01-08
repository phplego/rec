package ru.phplego.core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 25.01.12
* Time: 3:45
* To change this template use File | Settings | File Templates.
*/

public class AndroidUtils {
	/*public static String getContactNameFromNumber(String number, Context context) {
		try{
				// define the columns I want the query to return
				String[] projection = new String[] {
								ContactsContract.PhoneLookup.DISPLAY_NAME,
								ContactsContract.PhoneLookup.NUMBER };

				// encode the phone number and build the filter URI
				//Uri contactUri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(number));
				Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

				// query time
				Cursor c = context.getContentResolver().query(contactUri, projection, null,
								null, null);

				// Если что-то не получилось, возвращаем номер
				if(c == null) return number;

				// if the query returns 1 or more results
				// return the first result
				if (c.moveToFirst()) {
						String name = c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        c.close();
						return name;
				}
                c.close();
		} catch (Exception e){}

		// return the original number if no match was found
		return number;
	}
	*/

    static public String getContactNameFromNumber(Context context, String number) {

        String name = number;

        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            } else {
            }
            cursor.close();
        }
        return name;
    }

	public static long getFreeSpaceOnSD() {
        return Environment.getExternalStorageDirectory().getUsableSpace();
        //StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		//return stat.getAvailableBlocks() * stat.getBlockSize();
	}

	public static String getFreeSpaceOnSD_str() {
		double space = (double)getFreeSpaceOnSD();

		String [] postf = new String[]{"B", "kB", "MB", "GB", "TB"};

		for(int i = 0; i < 5; i++){
			if(space < 1000){
				return String.format("%.2f %s", (float)space, postf[i]);
			}
			space = space / 1024;
		}

		return "-TOO BIG-";
	}

}
