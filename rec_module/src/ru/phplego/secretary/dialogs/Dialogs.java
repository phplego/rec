package ru.phplego.secretary.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveCall;
import java.util.Date;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 03.05.12
* Time: 22:38
* To change this template use File | Settings | File Templates.
*/

public class Dialogs {
	public static AlertDialog alert(Context context, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text)
						.setIcon(App.getIcon())
						.setTitle(R.string.app_name)
						.setCancelable(true)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
								}
						});
		return builder.create();
	}

	public static AlertDialog alert(Context context, int resource) {
		return alert(context, App.getContext().getString(resource));
	}

	public static AlertDialog confirm(Context context, int text_res, DialogInterface.OnClickListener onOkListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.app_name);
		builder.setIcon(App.getIcon());
		builder.setMessage(App.getStr(text_res));
		builder.setCancelable(true);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, onOkListener);
		return builder.create();
	}

	public static DialogDate toPlanDialog(Context context, final ActiveCall activeCall) {
		long old_alert_time = activeCall.getPlannedTime();
		if (old_alert_time == 0) old_alert_time = System.currentTimeMillis();
		final Date old_alert_date = new Date(old_alert_time);
		DialogDate dlg = new DialogDate(context,
						new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datePicker, int year, int month, int day) {
										Date date = new Date(year - 1900, month, day, old_alert_date.getHours(), old_alert_date.getMinutes());
										activeCall.setPlannedTime(date.getTime());
								}
						},
						old_alert_date.getYear() + 1900, old_alert_date.getMonth(), old_alert_date.getDate());
		dlg.setOnlyFuture(true);
		return dlg;
	}

}
