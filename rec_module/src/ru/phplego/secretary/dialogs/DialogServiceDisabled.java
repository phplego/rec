package ru.phplego.secretary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.App;
import ru.phplego.secretary.Prefs;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 27.04.12
* Time: 22:53
* To change this template use File | Settings | File Templates.
*/

public class DialogServiceDisabled {
	public static AlertDialog.Builder get(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.start_service_question)
						.setIcon(App.getIcon())
						.setTitle(R.string.app_name)
						.setCancelable(true)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
										Prefs.checkbox_preference_resident.put(true);
										activity.startService(new Intent(App.getContext(), AService.class));
								}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
								}
						});
		return builder;
	}

}
