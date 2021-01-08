package ru.phplego.secretary.activities.pages;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.*;
import ru.phplego.core.date.Date;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.db.ActiveRecord;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.AdapterEverything;
import ru.phplego.secretary.db.system.ActiveSystemCall;
import ru.phplego.secretary.db.system.ActiveSystemSMS;
import ru.phplego.secretary.view.listeners.EndlessScrollListener;
import java.util.*;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageEverything extends PageAbstractList implements AdapterView.OnItemClickListener {
	private Vector <ActiveRecord> mItems;

	public PageEverything(ActivityPagerAbstract context) {
		super(context);
	}

	public Vector <ActiveRecord> getItems(long date_from, long date_to) {
		String[] projection = new String[] {
						CallLog.Calls._ID,
						CallLog.Calls.NUMBER,
						CallLog.Calls.DATE,
						CallLog.Calls.NEW,
						CallLog.Calls.DURATION,
						CallLog.Calls.TYPE,
						CallLog.Calls.CACHED_NAME,
		};

		Cursor cursor = this.getContext().managedQuery(
						CallLog.Calls.CONTENT_URI, projection,
						"date < "+date_from+" AND date > "+date_to, null,
						"date DESC"
		);

		ActiveQuery ac = new ActiveQuery();
		ac.from(new ActiveSystemCall());
		Vector<ActiveRecord> items = ac.objects(cursor);

		Uri mSmsinboxQueryUri = Uri.parse("content://sms");
		Cursor cursor1 = getContext().getContentResolver().query(
						mSmsinboxQueryUri,
						new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"},
						"date < "+date_from+" AND date > "+date_to, null,
						"date DESC"
		);


		ActiveQuery ac1 = new ActiveQuery();
		ac1.from(new ActiveSystemSMS());
		items.addAll(ac1.objects(cursor1));

		// Сортируем по дате
		Collections.sort(items, new Comparator<ActiveRecord>() {
				@Override
				public int compare(ActiveRecord activeRecord, ActiveRecord activeRecord1) {
						long date = activeRecord.getLong("date");
						long date1 = activeRecord1.getLong("date");
						if (date < date1) return 1;
						if (date > date1) return -1;
						return 0;
				}
		});

		for(ActiveRecord one: items){
				if(one.get("number").length() > 0) one.set("title", one.get("number"));
				if(one.get("body").length() > 0) one.set("title", one.get("body"));
				Date d = new Date(one.getLong("date"));
				String summary = d.format("%, HH:mm", "dd MMMMMMM")+"\n";
				summary += one.get(CallLog.Calls.TYPE)+"\n";
				summary += one.get(CallLog.Calls.CACHED_NAME)+"\n";
				one.set("human_date", summary);
		}
		return items;
	}

	private Vector <ActiveRecord> getNextPage(long date_from, int min_count) {
		long step = 1000 * 60 * 60 * 24 * 7;
		long date_to = date_from - step;
		Vector<ActiveRecord> items = new Vector<ActiveRecord>();
		int loops = 0;
		while (items.size() < min_count){
				items.addAll(getItems(date_from, date_to));
				date_to = date_to - step;
				date_from = date_from - step;
				loops ++;
				if(loops  >  50) break;
		}
		return items;
	}

	public void onCreate() {
		super.onCreate();
		getListView().setOnItemClickListener(this);

		mItems = getNextPage(System.currentTimeMillis(), 30);

		this.setAdapter(new AdapterEverything(getContext(), mItems));

		this.getListView().setOnScrollListener(new EndlessScrollListener(5) {
				@Override
				protected void onScrollToEnd(int page) {
						new AsyncTask(){
								@Override
								protected Object doInBackground(Object... objects) {
										Vector<ActiveRecord> newItems = getNextPage(mItems.lastElement().getLong("date"), 20);
										mItems.addAll(newItems);
										return null;
								}

								@Override
								protected void onPostExecute(Object o){
										((BaseAdapter)getListView().getAdapter()).notifyDataSetChanged();
								}
						}.execute();
				}
		});
	}

	public String getTitle() {
		return (App.getStr(R.string.everything)).toUpperCase();
	}

	public void onPageSelected() {
	}

	public void onPageOut() {
	}

	public void onItemClick(AdapterView <?> adapterView, View view, int position, long id) {
		// Простой короткий клик на элементе списка
		Object item = getAdapter().getItem(position);
		if(item instanceof ActiveSystemCall){
				// Звонок
				ActiveSystemCall one = (ActiveSystemCall)item;
				int contact_id = one.getContactId(getContext());
				if(contact_id > 0){
						Intent i = new Intent(Intent.ACTION_VIEW);
						Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, ""+contact_id);
						i.setData(uri);
						getContext().startActivity(i);
				}

		}
		else
		if(item instanceof ActiveSystemSMS){
				// Текстовое сообщение SMS
				ActiveSystemSMS one = (ActiveSystemSMS)item;
		}
	}

}
