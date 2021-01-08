package ru.phplego.secretary.activities.pages;

import android.widget.TextView;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageSearch extends Page {
	public PageSearch(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();
		setContentView( new TextView(getContext()) );
	}

	public String getTitle() {
		return getContext().getString(R.string.search).toUpperCase();
	}

	public void onPageSelected() {
		App.showSoftKeyboard();
	}

	public void onPageOut() {
		App.hideSoftKeyboard();
	}

}
