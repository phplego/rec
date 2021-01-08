package ru.phplego.secretary.activities.pages;

import android.content.Context;
import android.view.*;
import android.widget.*;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 09.05.12
* Time: 23:07
*/

public abstract class PageAbstractList extends Page {
	protected ListView mListView;

	protected ListAdapter mListAdapter;

	protected View mEmptyView;

	public PageAbstractList(ActivityPagerAbstract activityPager) {
		super(activityPager);
	}

	public void onCreate() {
		LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.std_list_view, null, false);
		this.setListView((ListView) view.findViewById(R.id.list_view));
		setContentView(view);
	}

	public void onPageSelected() {
		super.onPageSelected();
	}

	public void onResume() {
		super.onResume();
	}

	public ListView getListView() {
		return mListView;
	}

	public ListAdapter getAdapter() {
		return mListAdapter;
	}

	public void setListView(ListView listView) {
		mListView = listView;
		if(mListAdapter != null) mListView.setAdapter(mListAdapter);
	}

	public void setAdapter(ListAdapter adapter) {
		mListAdapter = adapter;
		if(mListView != null) mListView.setAdapter(adapter);
	}

	public void setEmptyView(View emptyView) {
		mEmptyView = emptyView;
		//((RelativeLayout)view).addView(mEmptyView);

		mEmptyView.setVisibility(View.VISIBLE);
		// Показывание / скрытие блока для пустого списка
		final Runnable checkEmptiness = new Runnable() {
				@Override
				public void run() {
						if(getAdapter() != null && getAdapter().getCount() == 0)
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

	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(Menu.NONE, 10, Menu.NONE, R.string.expand_all).setIcon(android.R.drawable.ic_menu_add);
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

}
