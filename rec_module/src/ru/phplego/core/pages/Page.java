package ru.phplego.core.pages;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ru.phplego.core.preferences.SmartPrefBoolean;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:27
* To change this template use File | Settings | File Templates.
*/

public abstract class Page implements View.OnCreateContextMenuListener {
	private ActivityPagerAbstract mActivityPager;

	private int mPosition;

	public boolean mIsCreated =false;

	public SmartPrefBoolean mIsEnabled;

	protected View mContentView;

	public Page(ActivityPagerAbstract activityPager) {
		mActivityPager = activityPager;
		mIsEnabled = new SmartPrefBoolean(this);
	}

	public ActivityPagerAbstract getContext() {
		return mActivityPager;
	}

	public boolean isEnabled() {
		return mIsEnabled.get(isEnabledByDefault());
	}

	public void setEnabled(boolean enabled) {
		mIsEnabled.put(enabled);
	}

	public boolean isEnabledByDefault() {
		return true;
	}

	protected void onCreate() {
		mIsCreated = true;
	}

	public abstract String getTitle();

	public void onResume() {
	}

	public void onPageSelected() {
	}

	public void onPageOut() {
	}

	/**
	* Номер страницы в ViewPager
	* @return
	*/
	public int getPosition() {
		return mPosition;
	}

	void setPosition(int position) {
		mPosition = position;
	}

	public boolean onSearch(String text) {
		return true;
	}

	public boolean onBackPressed() {
		return true;
	}

	/**
	* Если возвращает true - это значит пункты меню родителя не добавляются
	* @param menu
	* @return
	*/
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		return false;
	}

	public boolean isCurrent() {
		return this.equals(mActivityPager.getCurrentPage());
	}

	public boolean isCreated() {
		return mIsCreated;
	}

	public void setContentView(View view) {
		this.mContentView = view;
	}

	public View getContentView() {
		return mContentView;
	}

}
