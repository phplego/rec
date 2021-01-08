package ru.phplego.secretary.activities;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.db.TableCalls;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.dialogs.DialogDate;
import ru.phplego.secretary.view.ViewGroupFactoryCalls;
import ru.phplego.secretary.view.ViewOneRecord;
import java.io.File;
import java.util.Date;
import static android.view.Menu.NONE;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 14.01.12
* Time: 17:24
* To change this template use File | Settings | File Templates.
*/

public class ActivityOneRecord extends ExpandableListActivity {
	private static final int DATE_DIALOG_ID =1;

	private static final int TIME_DIALOG_ID =2;

	private static final int MENU_SET_DATE =1;

	private static final int MENU_SET_TIME =2;

	private ActiveCall mActiveCall;

	private void save() {
		EditText note_editor = (EditText) findViewById(R.id.one_record_note);
		String old_note = mActiveCall.getNote();
		String new_note = note_editor.getText().toString();

		if(old_note.equals(new_note)) return;
		mActiveCall.setNote(new_note);
		mActiveCall.update();

		if(old_note.length() == 0 || new_note.length() == 0)
				App.getEManager().riseEvent(new ViewOneRecord.OnNoteAddOrRemoveEvent(), null); //Событие появления заметки

		mActiveCall.setNote(note_editor.getText().toString());
		boolean modified = mActiveCall.update();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		long id = intent.getExtras().getLong(AService.EXTRA_RECORD_ID);
		if(id == 0){
				finish();
				return;
		}

		LayoutInflater inflater = LayoutInflater.from(this);
		getExpandableListView().addHeaderView(inflater.inflate(R.layout.activity_one_record, null, true));

		getExpandableListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
						ViewOneRecord viewOneRecord = (ViewOneRecord) view;
						Intent intent = new Intent(expandableListView.getContext(), ActivityOneRecord.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra(AService.EXTRA_RECORD_ID, viewOneRecord.getActiveCall().getId());
						viewOneRecord.getContext().startActivity(intent);
						return true;
				}
		});

		mActiveCall = (ActiveCall) new ActiveCall().getInstance(id);

		// Заголовок активити
		setTitle(mActiveCall.getPhone()+" - "+getString(mActiveCall.isIncoming() ? R.string.incoming : R.string.outgoing));

		// Крупный текст с именем абонента вверху экрана
		TextView one_record_title = (TextView) findViewById(R.id.one_record_title);
		one_record_title.setText(mActiveCall.getContactDisplayName(this));


		// Индикатор входящего/исходящего вызова
		ImageView incoming_indicator = (ImageView) findViewById(R.id.incoming_indicator_one_record);
		if(mActiveCall.isIncoming())
				incoming_indicator.setImageDrawable(getResources().getDrawable(R.drawable.sym_call_incoming));


		// Редактор заметки
		EditText note_editor = (EditText) findViewById(R.id.one_record_note);
		note_editor.setText(mActiveCall.getNote());
		note_editor.requestFocus();

		note_editor.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View view, int i, KeyEvent keyEvent) {
						switch (keyEvent.getAction()) {
								case KeyEvent.ACTION_UP:
										ActivityOneRecord.this.save();
										break;
						}
						return false;
				}
		});

		// Кнопка ОК
		Button bOk = (Button) findViewById(R.id.ok);
		bOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						finish();
				}
		});


		// Текст прочей информации о звонке
		TextView etc_info = (TextView) findViewById(R.id.etc_info);
		String etc_text = "\n";
		File call_file = new File(mActiveCall.getFilename());
		if(call_file.exists()){
				etc_text += "File size: " + call_file.length()/1024 + " kB\n";
				//etc_text += "File name: " + call_file.getName() + "\n";
		}
		etc_info.setText(etc_text);

		ActiveQuery activeQuery = new ActiveQuery();
		activeQuery.from(new ActiveCall());
		activeQuery.where(TableCalls.phone + "='" + mActiveCall.getPhone()+"'");
		activeQuery.orderby("created desc");
		activeQuery.limit(1000);
		GroupExAdapter adapter = new GroupExAdapter(this, activeQuery, null, new ViewGroupFactoryCalls());
		setListAdapter(adapter);
		for(int i = 0; i < adapter.getGroupCount(); i++)
				getExpandableListView().expandGroup(i);
	}

	public void onResume() {
		super.onResume();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_SET_DATE, NONE, NONE, R.string.set_alert_date_button_text);
		menu.add(MENU_SET_TIME, NONE, NONE, R.string.set_alert_time_button_text);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getGroupId()){
				case MENU_SET_DATE:
						showDialog(DATE_DIALOG_ID);
						return true;
				case MENU_SET_TIME:
						showDialog(TIME_DIALOG_ID);
						return true;
		}
		return false;
	}

	public void onPause() {
		super.onPause();
		this.save();
	}

	protected Dialog onCreateDialog(int id) {
		long old_alert_time = mActiveCall.getPlannedTime();
		if(old_alert_time == 0) old_alert_time = System.currentTimeMillis();
		final Date old_alert_date = new Date(old_alert_time);

		switch (id) {
				case DATE_DIALOG_ID:
						DatePickerDialog dlg = new DialogDate(this,
										new DatePickerDialog.OnDateSetListener() {
												@Override
												public void onDateSet(DatePicker datePicker, int year, int month, int day) {
														Date date = new Date(year-1900, month, day, old_alert_date.getHours(), old_alert_date.getMinutes());
														mActiveCall.setPlannedTime(date.getTime());
														mActiveCall.update();
														// Событие "Данны о запланнированных звонках изменены"
														//Application.getEManager().riseEvent(new ActiveCall.PlannedEvent(), null);
												}
										},
										old_alert_date.getYear()+1900, old_alert_date.getMonth(), old_alert_date.getDate());
						return dlg;
				case TIME_DIALOG_ID:
						return new TimePickerDialog(this,
										new TimePickerDialog.OnTimeSetListener() {
												@Override
												public void onTimeSet(TimePicker datePicker, int hour, int min) {
														Date date = new Date(old_alert_date.getYear(), old_alert_date.getMonth(), old_alert_date.getDate(), hour, min);
														mActiveCall.setPlannedTime(date.getTime());
														mActiveCall.update();
														// Событие "Данны о запланнированных звонках изменены"
														//Application.getEManager().riseEvent(new ActiveCall.PlannedEvent(), null);
												}
										},
										old_alert_date.getHours(), old_alert_date.getMinutes(), true);
		}
		return null;
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d("onDestroy");
		mActiveCall.removeOnChangeListeners(this);
		App.getPlayer().removeOnProgressListeners(this);
	}

}
