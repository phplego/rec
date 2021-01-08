package ru.phplego.secretary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import org.acra.ErrorReporter;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.Prefs;
import ru.phplego.secretary.debug.Crash;
import ru.phplego.secretary.media.CallRecorder;
import java.util.Date;

public class OnPhoneStateChangeReceiver extends BroadcastReceiver {
	static String stPhoneNumber =null;

	public void onReceive(Context context, Intent intent) {
		// Если это бесплатная версия и на телефоне имеется платная версия, то ничего не делаем
		//if(Application.IS_FREE_VERSION && Application.isPackageInstalled(Application.PACKAGE_PRO)) return;

		// Если программа выключена, ничего не делаем
		if(!Prefs.checkbox_preference_resident.get(true)) return;

		// Если запись разговоров выключена
		if(!Prefs.checkbox_preference_resident.get(true)) return;

		Bundle extras = intent.getExtras();
        String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        Log.d("phoneNumber = " + phoneNumber);

        if(phoneNumber != null){
			stPhoneNumber = phoneNumber;
        }
        else{
            // Если это скрытый номер
            //if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)
            stPhoneNumber = "00000000";
        }
        String state = extras.getString(TelephonyManager.EXTRA_STATE);

		Log.d("state: "+state+" phone: "+phoneNumber + (phoneNumber == null ?" real NULL" : "string"));

        if(stPhoneNumber != null && !CallRecorder.isRecordingSt()){
            Intent myIntent = new Intent(context, AService.class);
            myIntent.setAction(AService.ACTION_START_RECORDING);
            myIntent.putExtra(AService.EXTRA_PHONE_NUMBER, stPhoneNumber);
            myIntent.putExtra(AService.EXTRA_IS_INCOMING, true);
            context.startService(myIntent);

            ErrorReporter.getInstance().putCustomData("StartRecordTimeIncoming", new Date().toLocaleString());
            Crash.update();
		}

        if(state.equals(TelephonyManager.EXTRA_STATE_IDLE) && CallRecorder.isRecordingSt()){
            Intent myIntent = new Intent(context, AService.class);
            myIntent.setAction(AService.ACTION_STOP_RECORDING);
            context.startService(myIntent);
            ErrorReporter.getInstance().putCustomData("EndRecordTime", new Date().toLocaleString());
            Crash.update();
		}
	}
}
