package ru.phplego.secretary.activities.pages.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ru.phplego.core.db.ActiveRecord;
import ru.phplego.secretary.db.system.ActiveSystemCall;
import ru.phplego.secretary.db.system.ActiveSystemSMS;
import ru.phplego.secretary.view.ViewOneSystemCall;
import ru.phplego.secretary.view.ViewOneSystemSMS;
import java.util.Vector;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 21.08.12
* Time: 3:24
*/

public class AdapterEverything extends BaseAdapter {
	static final int VIEW_TYPE_SYSTEM_CALL =0;

	static final int VIEW_TYPE_SYSTEM_SMS =1;

	private Context mContext;

	private Vector <ActiveRecord> mItems;

	public AdapterEverything(Context context, Vector <ActiveRecord> items) {
		mContext    = context;
		mItems      = items;
	}

	public int getCount() {
		return mItems.size();
	}

	public ActiveRecord getItem(int i) {
		return mItems.get(i);
	}

	public long getItemId(int i) {
		return i;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		if(convertView == null){
				if(getItem(position) instanceof ActiveSystemSMS)
						convertView = new ViewOneSystemSMS(mContext, (ActiveSystemSMS)getItem(position));
				if(getItem(position) instanceof ActiveSystemCall)
						convertView = new ViewOneSystemCall(mContext, (ActiveSystemCall)getItem(position));
		}
		else{
				if(convertView instanceof ViewOneSystemSMS)
						((ViewOneSystemSMS)convertView).fillFromActiveRecord((ActiveSystemSMS) getItem(position));
				if(convertView instanceof ViewOneSystemCall)
						((ViewOneSystemCall)convertView).fillFromActiveRecord((ActiveSystemCall) getItem(position));
		}

		return convertView;
	}

	public int getViewTypeCount() {
		// Два типа - смска и звонок
		return 2;
	}

	public int getItemViewType(int position) {
		if(getItem(position) instanceof ActiveSystemCall)
				return VIEW_TYPE_SYSTEM_CALL;
		if(getItem(position) instanceof ActiveSystemSMS)
				return VIEW_TYPE_SYSTEM_SMS;
		throw new Error("Not compartible item type "+getItem(position).getClass().getSimpleName());
	}

}
