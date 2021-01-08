package ru.phplego.secretary.activities.pages;

import android.widget.LinearLayout;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageScroller extends Page {
	private String mTitle ="";

	public PageScroller(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();
		setContentView(new LinearLayout(getContext()));
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void onPageSelected() {
	}

	public void onPageOut() {
	}

}
