package ru.phplego.secretary.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import ru.phplego.core.debug.Log;
import ru.phplego.rec_module.R;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class ActivityHelp extends Activity {
	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.help);
		LinearLayout layout = new LinearLayout(this);

		WebView webView = new WebView(this);
		layout.addView(webView);
		setContentView(layout);

		webView.getSettings().setJavaScriptEnabled(false);

        webView.loadData(getText(R.string.help_content).toString(), "text/html; charset=UTF-8", null);
	}

}
