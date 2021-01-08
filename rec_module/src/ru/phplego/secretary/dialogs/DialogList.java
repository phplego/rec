package ru.phplego.secretary.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 17.08.12
* Time: 21:46
*/

public class DialogList {
	public static AlertDialog get(final Context context, CharSequence [] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Pick a color");
		builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
						//Toast.makeText(context, items[item], Toast.LENGTH_SHORT).show();
				}
		});
		return builder.create();
	}

}
