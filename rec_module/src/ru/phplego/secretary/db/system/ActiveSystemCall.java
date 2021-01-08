package ru.phplego.secretary.db.system;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import ru.phplego.core.AndroidUtils;
import ru.phplego.core.date.Date;
import ru.phplego.core.db.ActiveRecord;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 24.08.12
* Time: 23:23
*/

public class ActiveSystemCall extends ActiveRecord {
	public Date getDate() {
		return new Date(getLong("date"));
	}

	public String getPhone() {
		return get(CallLog.Calls.NUMBER);
	}

	public String getContactDisplayName(Context context) {
		return AndroidUtils.getContactNameFromNumber(context, getPhone());
	}

	public String getCachedName() {
		return getCachedName(false);
	}

	public String getCachedName(boolean or_number) {
		if(!or_number) return get(CallLog.Calls.CACHED_NAME);
		return get(CallLog.Calls.CACHED_NAME).length() > 0 ? get(CallLog.Calls.CACHED_NAME) : getPhone();
	}

	public int getContactId(Context context) {
		int phoneContactID = 0;
		Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
						Uri.encode(getPhone())),
						new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
						null, null, null
		);
		while(contactLookupCursor.moveToNext()){
				phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
		}
		contactLookupCursor.close();

		return phoneContactID;
	}

}
