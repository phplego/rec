package ru.phplego.secretary.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.widget.TextView;
import ru.phplego.core.db.ActiveRecord;
import ru.phplego.secretary.App;
import ru.phplego.secretary.debug.Crash;
import ru.phplego.rec_module.R;
import ru.phplego.core.AndroidUtils;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 20.04.12
* Time: 13:15
* To change this template use File | Settings | File Templates.
*/

public class ActivityDeviceInfo extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView tv = (TextView)findViewById(R.id.version_text);

		PackageManager manager = this.getPackageManager();
		try{
				PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
				tv.setTextSize(25);
				tv.setText("v"+info.versionName);
		}catch (Exception e){
				Crash.send(e);
		}

		String tvText = tv.getText().toString()+"\n";

		DisplayMetrics dMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

		switch(dMetrics.densityDpi){

				case DisplayMetrics.DENSITY_XHIGH:
						tvText += "DENSITY_XHIGH";
						break;
				case DisplayMetrics.DENSITY_HIGH:
						tvText += "DENSITY_HIGH";
						break;
				case DisplayMetrics.DENSITY_MEDIUM:
						tvText += "DENSITY_MEDIUM";
						break;
				case DisplayMetrics.DENSITY_LOW:
						tvText += "DENSITY_LOW";
						break;
		}

		tvText += "\n100 dip = " + App.dip(100) + " pixels";
		tvText += "\nActRecord.getCacheSize(): " + ActiveRecord.getCacheSize();

		tvText += "\nAvlb on SD: " + AndroidUtils.getFreeSpaceOnSD_str();
		tvText += "\nadmob_publisher_id: " + getString(R.string.admob_publisher_id);

		tv.setText(tvText);
	}

}
