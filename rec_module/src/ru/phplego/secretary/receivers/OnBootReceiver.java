package ru.phplego.secretary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.Prefs;

public class OnBootReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		// Если это бесплатная версия и на телефоне имеется платная версия, то ничего не делаем
		//if(Application.IS_FREE_VERSION && Application.isPackageInstalled(Application.PACKAGE_PRO)) return;

		// Если программа выключена, ничего не делаем
		if(!Prefs.checkbox_preference_resident.get(true)) return;

		// Запускаем сервис
		context.startService(new Intent(context, AService.class));
	}

}
