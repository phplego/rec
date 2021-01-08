package ru.phplego.secretary.activities.pages;

import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.EManager;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.view.ViewGroupFactoryTalks;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageCallsTalks extends PageAbstractListExpandableActiveQuery {
	GroupExAdapter mExAdapter;

	public PageCallsTalks(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();

		ActiveQuery<ActiveCall> activeQuery = new ActiveQuery<ActiveCall>();
		activeQuery.from(new ActiveCall());
		activeQuery.where("_id > 0");
		activeQuery.orderby("_id desc");
		activeQuery.limit(1000);

		mExAdapter = new GroupExAdapter(getContext(), activeQuery, new GroupExAdapter.Grouper() {
				@Override
				public String getGroupKey(ActiveCall activeCall) {
						return activeCall.getPhone();
				}
		}, new ViewGroupFactoryTalks());

		this.setAdapter(mExAdapter);

		// Слушатель, который инвалидирует данные в этом адаптере
		EManager.EventListener dataChangedListener = new EManager.EventListener() {
				@Override
				public void onEvent(EManager.Event e) {
						notifyDataSetChanged();
				}
		};

		// События, на которые мы обновляем содержимое
		App.getEManager().setEventListener(ActiveCall.InsertEvent.class, dataChangedListener, null);
		App.getEManager().setEventListener(ActiveCall.DeleteEvent.class, dataChangedListener, null);
	}

	public String getTitle() {
		return getContext().getString(R.string.dialogs).toUpperCase();
	}

	public void onPageSelected() {
		super.onPageSelected();
	}

}
