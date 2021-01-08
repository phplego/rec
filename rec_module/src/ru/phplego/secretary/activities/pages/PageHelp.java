package ru.phplego.secretary.activities.pages;

import android.webkit.WebView;
import android.widget.LinearLayout;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageHelp extends Page {
	public PageHelp(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();
		LinearLayout layout = new LinearLayout(getContext());

		WebView webView = new WebView(getContext());
		layout.addView(webView);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/help.htm");
		setContentView(layout);
	}

	public String getTitle() {
		return getContext().getString(R.string.help).toUpperCase();
	}

	public void onPageSelected() {
	}

	public void onPageOut() {
	}

	public boolean isEnabledByDefault() {
		return false;
	}

}
