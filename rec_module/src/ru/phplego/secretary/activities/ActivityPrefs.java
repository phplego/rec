package ru.phplego.secretary.activities;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import ru.phplego.secretary.AService;
import ru.phplego.secretary.App;
import ru.phplego.secretary.Prefs;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.debug.Crash;
import ru.phplego.secretary.dialogs.Dialogs;
import ru.phplego.secretary.media.CallRecorder;
import java.io.File;
import ru.phplego.core.AndroidUtils;

public class ActivityPrefs extends PreferenceActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// В зависимости от формата записи включаем или отключаем регулировку битрейта
		int recordingformat = Prefs.list_preference_recording_format.getInt(CallRecorder.RECORDING_FORMAT_MPEG4);
		switch (recordingformat){
				case CallRecorder.RECORDING_FORMAT_MPEG4:
						// Делаем доступным установку битрейта
						findPreference(Prefs.list_preference_recording_bitrate.getName()).setEnabled(true);
						break;
				case CallRecorder.RECORDING_FORMAT_3GP:
				case CallRecorder.RECORDING_FORMAT_WAV:
						// Делаем НЕдоступным установку битрейта
						findPreference(Prefs.list_preference_recording_bitrate.getName()).setEnabled(false);
						break;
		}

		// Включение / Выключение
		findPreference(Prefs.checkbox_preference_resident.getName()).setOnPreferenceChangeListener(
						new Preference.OnPreferenceChangeListener() {
								@Override
								public boolean onPreferenceChange(Preference preference, Object value) {
										Boolean enable = (Boolean) value;
										Prefs.checkbox_preference_resident.put(enable);
										if (enable)
												startService(new Intent(App.getContext(), AService.class));
										else
												stopService(new Intent(App.getContext(), AService.class));
										return true;
								}
						}
		);

		// На смену формата записи (mp4, 3gp, wav)
		findPreference(Prefs.list_preference_recording_format.getName()).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object value) {
						int format = Integer.parseInt(value.toString());
						switch (format){
								case CallRecorder.RECORDING_FORMAT_MPEG4:
										// Делаем доступным установку битрейта
										findPreference(Prefs.list_preference_recording_bitrate.getName()).setEnabled(true);
										break;
								case CallRecorder.RECORDING_FORMAT_3GP:
								case CallRecorder.RECORDING_FORMAT_WAV:
										// Делаем НЕдоступным установку битрейта
										findPreference(Prefs.list_preference_recording_bitrate.getName()).setEnabled(false);
										break;
						}
						return true;
				}
		});

		// На смену источника записи
		findPreference(Prefs.list_preference_recording_audio_source.getName()).setOnPreferenceChangeListener(
						new Preference.OnPreferenceChangeListener() {
								@Override
								public boolean onPreferenceChange(Preference preference, Object value) {
										int audioSource = Integer.parseInt(value.toString());
										MediaRecorder mr = new MediaRecorder();
										mr.reset();
										mr.setAudioSource(audioSource);
										mr.setOutputFile(App.getContext().getCacheDir() + "/test.3gp");
										mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
										mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

										// Проверяем, поддерживается ли источник записи
										try {
												mr.prepare();
												mr.start();
												Thread.sleep(100);
												mr.stop();
												mr.release();
												Toast.makeText(ActivityPrefs.this, "OK", Toast.LENGTH_SHORT).show();
										} catch (Exception e) {
												//Toast.makeText(ActivityPrefs.this, Application.getContext().getString(R.string.audio_source_not_supported), Toast.LENGTH_SHORT).show();
												Dialogs.alert(ActivityPrefs.this, R.string.audio_source_not_supported).show();
												return false;
										}
										return true;
								}
						}
		);


		// Выключение иконки в трее
		findPreference(Prefs.checkbox_preference_trayicon.getName()).setOnPreferenceChangeListener(
						new Preference.OnPreferenceChangeListener() {
								@Override
								public boolean onPreferenceChange(Preference preference, Object value) {
										Intent intent = new Intent(App.getContext(), AService.class);
										intent.setAction(AService.ACTION_UPDATE_TRAY_ICON);
										App.getContext().startService(intent);
										return true;
								}
						}
		);

		// Смена иконки программы
		findPreference(Prefs.app_icon.getName()).setOnPreferenceChangeListener(
						new Preference.OnPreferenceChangeListener() {
								@Override
								public boolean onPreferenceChange(Preference preference, Object value) {
										Intent intent = new Intent(App.getContext(), AService.class);
										intent.setAction(AService.ACTION_UPDATE_TRAY_ICON);
										App.getContext().startService(intent);
										return true;
								}
						}
		);

		// На изменение пути
		findPreference(Prefs.records_path.getName()).setOnPreferenceChangeListener(
						new Preference.OnPreferenceChangeListener() {
								@Override
								public boolean onPreferenceChange(Preference preference, Object value) {
										String path = (String)value;
										File f = new File(path);
										if( (!f.exists() && !f.mkdirs()) || !f.canWrite()){
												Dialogs.alert(ActivityPrefs.this, R.string.cannot_create_dir).show();
												return false;
										}
										return true;
								}
						}
		);

		// О программе
		String summary = getResources().getString(R.string.app_title) + " v"+App.getVersion() + "\n";
		summary += "Build time: " + App.getBuildDate().format("HH:mm d MMMMMMMMM yyyy") + "\n";
		summary += "Автор: Олег Дубров\n";
		summary += "SD FreeSpace: " + AndroidUtils.getFreeSpaceOnSD_str();
		findPreference("pref_about").setSummary(summary);
		findPreference("pref_about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(ActivityPrefs.this, ActivityAbout.class);
						startActivity(intent);
						return false;
				}
		});
	}

	public void onResume() {
		super.onResume();
		setTitle(R.string.options);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(Menu.NONE, 1, Menu.NONE, R.string.database_export);
		//menu.add(Menu.NONE, 2, Menu.NONE, R.string.database_import);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.send_report);
		menu.add(Menu.NONE, 4, Menu.NONE, R.string.technical_info);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
				case 3:
						Crash.update();
						Crash.send(new Exception("Из меню Завалить приложение"));
						App.toast(R.string.send_report_sended, true);
						break;
				case 4:
						Intent intent = new Intent(this, ActivityDeviceInfo.class);
						startActivity(intent);
						break;
		}

		return true;
	}

}
