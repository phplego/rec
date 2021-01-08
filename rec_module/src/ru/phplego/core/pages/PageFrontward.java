package ru.phplego.core.pages;

import android.view.View;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 19.08.12
* Time: 18:47
*/

public class PageFrontward extends Page {
	private Page mOriginal;

	public PageFrontward(ActivityPagerAbstract activityPager) {
		super(activityPager);
	}

	public void setOriginal(Page original) {
		mOriginal = original;
	}

	protected void onCreate() {
		mOriginal.onCreate();
	}

	public String getTitle() {
		return mOriginal.getTitle();
	}

	public boolean isEnabled() {
		return mOriginal.isEnabled();
	}

	public View getContentView() {
		return mOriginal.getContentView();
	}

	public boolean isCurrent() {
		return mOriginal.isCurrent();
	}

	public void onPageSelected() {
		mOriginal.onPageSelected();
	}

}
