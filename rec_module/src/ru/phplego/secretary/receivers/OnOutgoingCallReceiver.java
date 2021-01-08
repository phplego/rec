package ru.phplego.secretary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.acra.ErrorReporter;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.Prefs;
import ru.phplego.secretary.debug.Crash;
import ru.phplego.secretary.media.CallRecorder;
import java.util.Date;

public class OnOutgoingCallReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		// Если это бесплатная версия и на телефоне имеется платная версия, то ничего не делаем
		//if(Application.IS_FREE_VERSION && Application.isPackageInstalled(Application.PACKAGE_PRO)) return;

		// Если программа выключена, ничего не делаем
		if(!Prefs.checkbox_preference_resident.get(true)) return;


		// Если запись разговоров выключена
		if(!Prefs.checkbox_preference_resident.get(true)) return;

		// Если по какой-то причине запись уже идет
		if(CallRecorder.isRecordingSt()) return;

		Bundle extras = intent.getExtras();
        String phoneNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER);
        Log.d("phoneNumber = " + phoneNumber);

        Intent myIntent = new Intent(context, AService.class);
        myIntent.setAction(AService.ACTION_START_RECORDING);
        myIntent.putExtra(AService.EXTRA_PHONE_NUMBER, phoneNumber);
        myIntent.putExtra(AService.EXTRA_IS_INCOMING, false);
		myIntent.setFlags(Intent.FLAG_FROM_BACKGROUND);

        Log.d("Starting service " + myIntent.getComponent().getClassName());

        context.startService(myIntent);

		ErrorReporter.getInstance().putCustomData("StartRecordTimeOutgoing", new Date().toLocaleString());
		Crash.update();
	}

}
