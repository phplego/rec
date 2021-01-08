package ru.phplego.secretary.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.view.ViewOneRecord;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 27.04.12
* Time: 22:53
* To change this template use File | Settings | File Templates.
*/

public class DialogOneRecord {
	public static AlertDialog.Builder get(final Activity activity, final ActiveCall activeCall) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater factory = LayoutInflater.from(activity);
		final View viewDiaogContent = factory.inflate(R.layout.dialog_one_record, null);

		final EditText etNote = (EditText)viewDiaogContent.findViewById(R.id.one_record_note);

		etNote.setText(activeCall.getNote());

		// Сохранение при вводе в текстовом поле
		TextWatcher textWatcher = new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
				@Override
				public void afterTextChanged(Editable editable) {
						String old_note = activeCall.getNote();
						String new_note = etNote.getText().toString();
						if(old_note.equals(new_note)) return;
						activeCall.setNote(new_note);
						activeCall.update();

						if(old_note.length() == 0 || new_note.length() == 0)
								App.getEManager().riseEvent(new ViewOneRecord.OnNoteAddOrRemoveEvent(), null); //Событие появления заметки
				}
		};
		etNote.addTextChangedListener(textWatcher);


		final Button ibToPlan = (Button)viewDiaogContent.findViewById(R.id.btnToPlan);
		// Обработчик кнопки "Запланировать"
		ibToPlan.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View view) {
						Dialogs.toPlanDialog(activity, activeCall).show();
				}
		});

		// Чекбокс ИЗБРАННАЯ
		CheckBox chkFavourite = (CheckBox)viewDiaogContent.findViewById(R.id.chkFavourite);
		chkFavourite.setChecked(activeCall.isFavourite());
		chkFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
						if(b)
								activeCall.addToFavourites();
						else
								activeCall.removeFromFavourites();
				}
		});

		if(activeCall.isIncoming())
				builder.setIcon(R.drawable.sym_call_incoming);
		else
				builder.setIcon(R.drawable.sym_call_outgoing);

		builder.setView(viewDiaogContent);
		builder.setTitle(activeCall.getContactDisplayName(activity));
		builder.setCancelable(true);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
								}
						});
		return builder;
	}

}
