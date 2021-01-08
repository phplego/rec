package ru.phplego.secretary.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.*;
import ru.phplego.secretary.App;
import ru.phplego.secretary.db.ActiveNote;
import ru.phplego.secretary.db.TableNotes;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 27.04.12
* Time: 22:53
* To change this template use File | Settings | File Templates.
*/

public class DialogNotepadEditNote {
	public static AlertDialog.Builder get(final Context context, final ActiveNote activeNote, final Runnable onComplete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		final EditText etEditNote = new EditText(context);
		etEditNote.setGravity(Gravity.TOP);
		etEditNote.setTextSize(App.dip(20));
		etEditNote.setMinLines(4);

		etEditNote.setText(activeNote.getText());

		builder.setView(etEditNote);
		//builder.setTitle(R.string.pages);
		builder.setCancelable(true);

		// ОК
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
						activeNote.set(TableNotes.text, etEditNote.getText().toString().trim());
						activeNote.update();
						onComplete.run();
						dialog.cancel();
				}
		});
		// Отмена
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
						dialog.cancel();
				}
		});
		return builder;
	}

}
