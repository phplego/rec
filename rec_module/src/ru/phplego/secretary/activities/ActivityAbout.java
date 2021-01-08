package ru.phplego.secretary.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;

public class ActivityAbout extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		//mNotifyMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

		TextView tv = (TextView)findViewById(R.id.version_text);
		tv.setTextSize(25);
		tv.setText("v"+App.getVersion());

		Button btnSendEmail = (Button)findViewById(R.id.sendEmailToUs);
		btnSendEmail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						Intent i = new Intent(Intent.ACTION_SENDTO);
						//i.setType("message/rfc822");
						i.setType("text/plain");
						//i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"box4dub@gmail.com"});
						String sbj = App.getStr(R.string.app_name)+" v"+App.getVersion();
						sbj += " " + Build.MANUFACTURER + " " + Build.DEVICE;
						sbj += ", Android " + Build.VERSION.RELEASE;
						i.putExtra(Intent.EXTRA_SUBJECT, sbj);
						i.putExtra(Intent.EXTRA_TEXT   , App.getStr(R.string.hello_oleg));
						i.setData(Uri.parse("mailto:phplego@gmail.com"));
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
								startActivity(Intent.createChooser(i, App.getStr(R.string.send_email_to_developer)));
						} catch (android.content.ActivityNotFoundException ex) {
								Toast.makeText(ActivityAbout.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
						}

				}
		});
	}

}
