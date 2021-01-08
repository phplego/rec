package ru.phplego.secretary.activities.pages;

import android.content.Context;
import android.view.*;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.DefaultOnCreateContextMenuListener;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.view.ViewEmptyList;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 09.05.12
* Time: 23:07
*/

public abstract class PageAbstractListExpandable extends Page {
	protected ExpandableListView mExList;

	protected ExpandableListAdapter mExAdapter;

	protected View mEmptyView;

	public PageAbstractListExpandable(ActivityPagerAbstract activityPager) {
		super(activityPager);
	}

	public void onCreate() {
		super.onCreate();

		LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.std_expandable_list_view, null, false);

		this.setContentView(view);
		this.setListView((ExpandableListView) view.findViewById(R.id.elv1));

		mEmptyView = new ViewEmptyList(getContext());
		((RelativeLayout)view).addView(mEmptyView);

		mEmptyView.setVisibility(View.VISIBLE);
		// Показывание / скрытие блока для пустого списка
		final Runnable checkEmptiness = new Runnable() {
				@Override
				public void run() {
						if(getAdapter() != null && getAdapter().getGroupCount() == 0)
								mEmptyView.setVisibility(View.VISIBLE);
						else
								mEmptyView.setVisibility(View.INVISIBLE);

				}
		};

		getListView().setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
				@Override
				public void onChildViewAdded(View view, View view1) {
						checkEmptiness.run();
				}

				@Override
				public void onChildViewRemoved(View view, View view1) {
						checkEmptiness.run();
				}
		});
	}

	public void onPageSelected() {
		super.onPageSelected();
	}

	public void onResume() {
		super.onResume();
	}

	public ExpandableListView getListView() {
		return mExList;
	}

	public ExpandableListAdapter getAdapter() {
		return mExAdapter;
	}

	public void setListView(ExpandableListView listView) {
		mExList = listView;
		mExList.setOnCreateContextMenuListener(new DefaultOnCreateContextMenuListener());
		if(mExAdapter != null) mExList.setAdapter(mExAdapter);
	}

	public void setAdapter(ExpandableListAdapter adapter) {
		mExAdapter = adapter;
		if(mExList != null) mExList.setAdapter(adapter);
	}

	static final int OPT_MENU_EXPAND_ALL =0xffff0000;

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, OPT_MENU_EXPAND_ALL, 90, R.string.expand_all).setIcon(android.R.drawable.ic_menu_add);
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
				case OPT_MENU_EXPAND_ALL:
						for(int i = 0; i < getAdapter().getGroupCount(); i++) getListView().expandGroup(i);
						return true;
		}
		return false;
	}

}
