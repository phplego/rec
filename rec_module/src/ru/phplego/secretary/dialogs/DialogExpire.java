package ru.phplego.secretary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 27.04.12
* Time: 22:53
* To change this template use File | Settings | File Templates.
*/

public class DialogExpire {
	public static AlertDialog.Builder get(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.trial_expired)
						.setIcon(R.drawable.icon_margin)
						.setTitle(R.string.app_name)
						.setCancelable(true)
						.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
										Intent intent = new Intent(Intent.ACTION_VIEW);
										intent.setData(Uri.parse("market://details?id=ru.readyscript.secretary" + "pro"));
										activity.startActivity(intent);
										activity.finish();
								}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
								}
						})
						.setOnCancelListener(new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialogInterface) {
										activity.finish();
								}
						});
		return builder;
	}

}
