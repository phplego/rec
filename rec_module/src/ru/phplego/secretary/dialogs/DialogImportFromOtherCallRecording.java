package ru.phplego.secretary.dialogs;

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import ru.phplego.core.db.Database;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityPager;
import ru.phplego.core.debug.Log;
import java.util.List;
import java.util.Vector;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 14.06.12
* Time: 21:33
*/

public class DialogImportFromOtherCallRecording {
	public static void start(final Context context) {
		final List<String> installedPackages = App.getOtherCallRecordingPackagesInstalled();

        Log.d("installedPackages: "+ installedPackages.size());

		if(installedPackages.size() == 0){
            App.toast("Other applications not found");
            return; // Если не установлено ни одного секретаря
        }


		// Если есть только один другой секретарь
		if(installedPackages.size() == 1){
				DialogImportFromOtherCallRecording.get(context, installedPackages.get(0)).show();
				return;
		}

		// Диалог выбора из какого секретаря импортировать
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.from_which_secretary);

		// Получаем заголовки других секретарей
		List<CharSequence> titles = new Vector<CharSequence>();
		for(String pkg: installedPackages){
				try{
						ApplicationInfo ai = context.getPackageManager().getApplicationInfo(pkg, 0);
						titles.add(context.getPackageManager().getApplicationLabel(ai));
				}catch (Exception e){
						titles.add(pkg);
				}
		}

		builder.setItems(titles.toArray(new String[]{}), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int index) {
                    DialogImportFromOtherCallRecording.get(context, installedPackages.get(index)).show();
				}
		});
		builder.create().show();
	}

	public static AlertDialog get(final Context context, final String package_name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(package_name.endsWith("pro"))
			builder.setMessage(R.string.really_want_import_from_secretary_pro);
		else
			builder.setMessage(R.string.really_want_import_from_secretary_free);

		builder.setIcon(App.getIcon());
		builder.setTitle(App.getStr(R.string.import_)+" "+App.getStr(R.string.import_from_secretary));
		builder.setCancelable(true);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
                        Log.d("Ok clicked");
						boolean ok = doImport(context, package_name);
						if(!ok) App.toast(R.string.database_import_err);
				}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	public static boolean doImport(final Context context, String package_name) {
        Log.d("doImport()... from pkg = "+package_name);

        if(!App.isPackageInstalled(package_name))return false;

		//tv.append("Binding..\n");
		ServiceConnection connection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        Log.d("onServiceConnected()...");
						//tv.append("Connected..\n");
						Parcel data = Parcel.obtain();
						Parcel reply = Parcel.obtain();
						try{
								//tv.append("Transact..\n");
								boolean res = iBinder.transact(AService.TRANSACT_CODE_EXPORT_TO_BYTE_ARRAY, data, reply, 0);
								//tv.append("Transact ends with "+res+"..\n");
								int db_size = reply.readInt();
								if(db_size != 0 && db_size != 123){ // 123 - это дикий багфикс, так как старая версия всегда возвращала 123 (тестовый код)
										byte [] dbArray = new byte[db_size];
										reply.readByteArray(dbArray);
										//tv.append("Readed bytes = "+dbArray.length+"\n");
										Database.importFromByteArray(dbArray);
										App.initDatabase();
										App.toast(R.string.database_import_ok);
										// Перезапускаем Пейджер
										Intent i = new Intent(context, ActivityPager.class);
										i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										context.startActivity(i);
								}
								else{
										App.toast(R.string.database_import_err);
								}
								//tv.append("Import complete!");
						}catch (RemoteException e){
                                Log.d("RemoteException! = "+e.getMessage());
								//tv.append("RemoteException! \n");
								Log.d(e);
						}
						context.unbindService(this);
				}

				@Override
				public void onServiceDisconnected(ComponentName componentName) {
                        Log.d("onServiceDisconnected()...");
				}
		};

        Intent intent = new Intent();
        // Для новой версии серетаря, достаточно установить компонент
        intent.setComponent(new ComponentName(package_name, AService.class.getName()));

        boolean bRes = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // Если импорт не удался
        if(!bRes){
            // Возможно мы имеем дело со старой версией секретаря, тогда нужно импортировать через склеенное имя
            String service_class = package_name+"."+AService.class.getSimpleName();
            intent = new Intent(service_class);
            bRes = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        Log.d("bindService() returned "+bRes);
		return bRes;
	}

}
