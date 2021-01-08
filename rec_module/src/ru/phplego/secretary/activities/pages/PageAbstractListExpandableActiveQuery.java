package ru.phplego.secretary.activities.pages;

import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.etc.ActiveCallSearch;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import java.util.Vector;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 30.04.12
* Time: 19:13
* To change this template use File | Settings | File Templates.
*/

public abstract class PageAbstractListExpandableActiveQuery extends PageAbstractListExpandable {
	private boolean mDataChanged;

	Vector <String> mBaseWhere =new Vector<String>();

	public PageAbstractListExpandableActiveQuery(ActivityPagerAbstract activityPager) {
		super(activityPager);
	}

	public void onPageSelected() {
		reloadIfDataSetChanged();
	}

	public void notifyDataSetChanged() {
		mDataChanged = true;
		if(isCurrent()) reloadIfDataSetChanged();
	}

	protected void reloadIfDataSetChanged() {
		if(mDataChanged){
				Log.d("reloadIfDataSetChanged() cur="+this.getClass().getSimpleName());
				getAdapter().requery();
				getAdapter().notifyDataSetChanged();
		}
		mDataChanged = false;
	}

	public GroupExAdapter getAdapter() {
		return (GroupExAdapter)mExAdapter;
	}

	public void setAdapter(GroupExAdapter adapter) {
		super.setAdapter(adapter);
		mBaseWhere = getAdapter().getActiveQuery().where();
	}

	public boolean onSearch(String text) {
		if(getAdapter() == null) return true;

		this.getAdapter().getActiveQuery().where(mBaseWhere);
		if(text.length() == 0){
				notifyDataSetChanged();
				return true;
		}
		ActiveCallSearch.startSearch(this.getAdapter().getActiveQuery(), text, this, new Runnable() {
				@Override
				public void run() {
						notifyDataSetChanged();
						for (int i = 0; i < getAdapter().getGroupCount(); i++) getListView().expandGroup(i);
				}
		});

		return true;
	}

}
