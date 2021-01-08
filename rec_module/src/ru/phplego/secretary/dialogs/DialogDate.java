package ru.phplego.secretary.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.DatePicker;
import ru.phplego.core.date.Date;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import java.text.SimpleDateFormat;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 19:55
* To change this template use File | Settings | File Templates.
*/

public class DialogDate extends DatePickerDialog {
	static DialogDate instance;

	Date mDate;

	boolean mOnlyFuture =false;

	static final OnDateSetListener mOnDateSetListener =
	new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
			if(instance != null){
				Date curDate = new Date();
				curDate.setHours(0); curDate.setMinutes(0);
				if(instance.mOnlyFuture &&instance.mDate.getTime() < curDate.getTime()){
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							instance.show();
						}
					}, 10);
					return;
				}
				instance.mUserOnDateSetListener.onDateSet(datePicker, i, i1, i2);
			}
		}
	}
	;

	OnDateSetListener mUserOnDateSetListener;

	public DialogDate(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
		super(context, mOnDateSetListener , year, monthOfYear, dayOfMonth);
		instance = this;
		mUserOnDateSetListener = callBack;

		onDateChanged(null, year, monthOfYear, dayOfMonth);
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if(view != null) super.onDateChanged(view, year, month, day);
		mDate = new Date();
		mDate.setDate(day);
		mDate.setMonth(month);
		mDate.setYear(year - 1900);
		String title = new SimpleDateFormat("EEEEEEEEEEEEEE").format(mDate);
		if(mDate.isToday())      title = App.getStr(R.string.today);
		if(mDate.isYesterday())  title = App.getStr(R.string.yesterday);
		if(mDate.isTomorrow())   title = App.getStr(R.string.tomorrow);
		setTitle(App.getStr(R.string.plan_to)+"\n"+title.toUpperCase());
	}

	public void setOnlyFuture(boolean val) {
		mOnlyFuture = val;
	}

}
