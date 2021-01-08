package ru.phplego.secretary;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import ru.phplego.core.db.Database;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityAbout;
import ru.phplego.secretary.activities.ActivityOneRecord;
import ru.phplego.secretary.activities.ActivityPager;
import ru.phplego.secretary.activities.ActivitySaveDialog;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.db.TableCalls;
import ru.phplego.secretary.debug.Crash;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.media.CallRecorder;
import java.io.IOException;

public class AService extends Service {
	public static final String ACTION_START_RECORDING ="ACTION_START_RECORDING";

	public static final String ACTION_STOP_RECORDING ="ACTION_STOP_RECORDING";

	public static final String ACTION_UPDATE_TRAY_ICON ="ACTION_UPDATE_TRAY_ICON";

	public static final String ACTION_ON_TRAY_ICON_CLICK ="ACTION_ON_TRAY_ICON_CLICK";

	public static final String EXTRA_RECORD_ID ="EXTRA_RECORD_ID";

	public static final String EXTRA_PHONE_NUMBER ="EXTRA_PHONE_NUMBER";

	public static final String EXTRA_IS_INCOMING ="EXTRA_IS_INCOMING";

	public static final int TRANSACT_CODE_EXPORT_TO_BYTE_ARRAY =1;

	public static final int TRANSACT_CODE_DISABLE =2;

	public static final int TRANSACT_CODE_EXIT =3;

	public void onCreate() {
		super.onCreate();

		if(Prefs.checkbox_preference_resident.get(true)){
				App.turnOffOtherCallRecording();
		}

		if(Prefs.checkbox_preference_resident.get(true) && Prefs.checkbox_preference_trayicon.get(true)){
				createTrayIcon(App.getIcon());
		}


		// Инициализируем мониторинг буфера обмена
		ClipboardMonitor.onServiceCreate();
	}

	private void createTrayIcon(int icon_resource) {
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon_resource, getString(R.string.service_working), when);
		Context context = getApplicationContext();
		CharSequence contentTitle = this.getString(R.string.app_title);
		CharSequence contentText = getString(R.string.service_working);

		Intent notificationIntent = new Intent(this, AService.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.setAction(ACTION_ON_TRAY_ICON_CLICK);
		PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
		startForeground(10, notification);
	}

	private void destroyTrayIcon() {
		// Cancel the persistent notification.
		stopForeground(true);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		int return_value = START_NOT_STICKY;
		if(intent == null) return return_value;

        try{
            // Начать запись
            if(AService.ACTION_START_RECORDING.equals(intent.getAction())){
                String phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
                if(phoneNumber == null) return return_value  ;

                // Если это запрос USSD, ничего не записываем:
                if(phoneNumber.endsWith("#")) return return_value  ;

                // Начинаем запись
                Log.d("Action ACTION_START_RECORDING received. Calling startRecord()");

                startRecord(phoneNumber, intent.getBooleanExtra(EXTRA_IS_INCOMING, false));

                if(Prefs.checkbox_preference_trayicon.get(true)) // Ставим иконку записи
                    createTrayIcon(R.drawable.icon_recording);
            }
            // Завершить запись
            if(ACTION_STOP_RECORDING.equals(intent.getAction())){
                stopRecord();
                if(Prefs.checkbox_preference_trayicon.get(true)) // Возвращаем иконку на место
                    createTrayIcon(App.getIcon());
            }
            // Если во время разговора нажали на иконку в трее
            if(ACTION_ON_TRAY_ICON_CLICK.equals(intent.getAction())){
                if(CallRecorder.isRecordingSt()){
                    Intent i = new Intent(this, ActivityOneRecord.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(EXTRA_RECORD_ID, CallRecorder.getCurrentRecordIdSt());
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(this, ActivityPager.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }

            // Просьба обновить иконку в трее
            if(ACTION_UPDATE_TRAY_ICON.equals(intent.getAction())){
                if(Prefs.checkbox_preference_trayicon.get(true))
                        createTrayIcon(App.getIcon());
                else
                        destroyTrayIcon();
            }
		}
		catch (CallRecorder.CardNotMountedException e){
            // Ничего не делаем, карта не примонтирована (не отправляем отчет, потому что это относительно частая ситуация)
		}
        catch(IOException e){
            Crash.send(e);
            Log.d("111", "start|stop fails", e);
		}
		return return_value ;
	}

	public void onDestroy() {
		destroyTrayIcon();

		// Отключаем мониторинг буфера обмена
		ClipboardMonitor.onServiceDestroy();
	}

	public IBinder onBind(Intent intent) {
		class MyBinder extends Binder{
				public boolean onTransact(int code, Parcel data, Parcel reply, int flags){
						switch (code){
								case TRANSACT_CODE_EXPORT_TO_BYTE_ARRAY:
										Log.d("onTransact code = "+code);
										byte [] dbArray = Database.exportToByteArray();
										Log.d("dbArray.length = "+dbArray.length);
										reply.writeInt(dbArray.length); // Передаем размер базы данных
										reply.writeByteArray(dbArray);
										break;
								case TRANSACT_CODE_DISABLE:
										Prefs.checkbox_preference_resident.put(false);
										destroyTrayIcon();
										AService.this.stopSelf();
										break;
								case TRANSACT_CODE_EXIT:
										break;
						}
						return true;
				}
		}
		return new MyBinder();
	}

	public void startRecord(String phoneNumber, boolean incoming) throws IOException {
		if(CallRecorder.isRecordingSt()) return; // Не запусить новую запись, пока идет запись

		String filename = phoneNumber;
		if(incoming) filename += "_i";

		CallRecorder.getInstance().setFilename(filename);
		CallRecorder.getInstance().start();

		ActiveCall activeCall = new ActiveCall();

		String etc = "v" + App.getVersion() + "\n";
		etc += "ASource: " + Prefs.list_preference_recording_audio_source.getInt(0);

		activeCall.set(TableCalls.phone.toString(), phoneNumber);
		activeCall.set(TableCalls.created.toString(), System.currentTimeMillis());
		activeCall.set(TableCalls.modified.toString(), System.currentTimeMillis());
		activeCall.set(TableCalls.filename.toString(), CallRecorder.getInstance().getPath());
		activeCall.set(TableCalls.incoming.toString(), incoming);
		activeCall.set(TableCalls.etc.toString(), etc);

		long record_id = activeCall.insert();
		CallRecorder.getInstance().setCurrentRecordId(record_id);
	}

	public void stopRecord() throws IOException {
		CallRecorder.getInstance().stop();

		// Запишем продолжительность разговора
		long duration = CallRecorder.getInstance().getLastDurationMillis();
		long record_id = CallRecorder.getInstance().getCurrentRecordId();

		// На некоторых телефонах record_id = 0
		if(record_id == 0) return;

		ActiveCall activeCall = (ActiveCall) new ActiveCall().getInstance(record_id);
		activeCall.setDuration(duration);
		activeCall.update();

		// Если пометка не введена во время разговора, показываем диалог "Сохранить?"
        if(activeCall.getNote().length() == 0){
            Intent intent = new Intent(this, ActivitySaveDialog.class);
            intent.putExtra(EXTRA_RECORD_ID, record_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
		}
	}

}
