package ru.phplego.secretary.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.google.ads.AdView;
import ru.phplego.core.Utils;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.App;
import ru.phplego.secretary.Prefs;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveCall;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 12.01.12
* Time: 1:14
* To change this template use File | Settings | File Templates.
*/

public class ActivitySaveDialog extends Activity {
	private static final int DIALOG_YES_NO_MESSAGE =1;

	private enum Action {
		SAVE,
		SAVE_AND_ADD_NOTE,
		CANCEL
	}
	private Action action =Action.CANCEL;

	ActiveCall mActiveCall;

	long mRecordId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Чтобы не гас экран и не блокировалась клавиатура
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
						WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
						WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	public void onStart() {
		super.onResume();
		Intent intent = getIntent();

		Bundle extras = intent.getExtras();
		if(extras == null) {
				finish();
				return;
		}
		mRecordId = extras.getLong(AService.EXTRA_RECORD_ID);
		if(mRecordId == 0){
				finish();
				return;
		}

		mActiveCall = (ActiveCall) new ActiveCall().getInstance(mRecordId);

		// Если стоит настройка "Спрашивать по окончании звонка"
		if(Prefs.list_preference_recording.getInt(1) == 1){
				showDialog(DIALOG_YES_NO_MESSAGE);
				return;
		}
		// Если стоит настройка "Сохранять всегда"
		if(Prefs.list_preference_recording.getInt(1) == 2){
				ActivitySaveDialog.this.action = Action.SAVE;
				finish();
				return;
		}

		// Если стоит настройка "Не записывать"
		ActivitySaveDialog.this.action = Action.CANCEL;
		finish();
	}

	public void onStop() {
		super.onStop();
		switch (action){
				case SAVE:
						// Запись уже и так сохранена, ничего не делаем
						break;
				case SAVE_AND_ADD_NOTE:
						Intent intent = new Intent(ActivitySaveDialog.this, ActivityOneRecord.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra(AService.EXTRA_RECORD_ID, ActivitySaveDialog.this.mRecordId);
						startActivity(intent);
						break;
				case CANCEL:
						if(mActiveCall != null)
								mActiveCall.delete(); // Удаляем запись
						break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
				case DIALOG_YES_NO_MESSAGE:
						final String dialogTitle = getString(R.string.conversation_finished);
						final Dialog dialog = new Dialog(this);

						LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View view = inflater.inflate(R.layout.dialog_save_conversation, null, false);

                        if(App.isVersionFree()){
                            // Отображаем рекламный блок
                            AdView adView = (AdView)view.findViewById(R.id.adView);
                            adView.setVisibility(View.VISIBLE);
                        }

						dialog.setContentView(view);
						ImageView icon = (ImageView)dialog.findViewById(R.id.save_dialog_icon);
						icon.setImageResource(App.getIcon());
						dialog.setTitle(dialogTitle);

						Button btnSave = (Button) dialog.findViewById(R.id.save_dialog_ok_button);
						Button btnCancel = (Button) dialog.findViewById(R.id.save_dialog_cancel_button);
						Button btnSaveAndNote = (Button) dialog.findViewById(R.id.save_dialog_save_and_note_button);

						btnSave.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
										ActivitySaveDialog.this.action = Action.SAVE;
										ActivitySaveDialog.this.finish();
								}
						});
						btnSaveAndNote.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
										ActivitySaveDialog.this.action = Action.SAVE_AND_ADD_NOTE;
										ActivitySaveDialog.this.finish();
								}
						});
						btnCancel.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
										ActivitySaveDialog.this.action = Action.CANCEL;
										ActivitySaveDialog.this.finish();
								}
						});

						dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
										ActivitySaveDialog.this.action = Action.CANCEL;
										ActivitySaveDialog.this.finish();
								}
						});


						Thread thread = new Thread(new Runnable() {
								@Override
								public void run() {
										for(int i = 5; i >= 0; i--){
												final int index = i;
												Utils.sleep(1000);
												if(ActivitySaveDialog.this == null) return;
												ActivitySaveDialog.this.runOnUiThread(new Runnable() {
														@Override
														public void run() {
																Button cancelButton = (Button) dialog.findViewById(R.id.save_dialog_cancel_button);
																String buttonText = ActivitySaveDialog.this.getString(R.string.cancel);
																cancelButton.setText(buttonText+" ("+index+")");
														}
												});
												Utils.sleep(500);
										}

										ActivitySaveDialog.this.finish();
								}
						});
						thread.start();

						return dialog;
		}
		return null;
	}

}
