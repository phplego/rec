package ru.phplego.secretary.activities.pages;

import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.EManager;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.secretary.App;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.view.ViewGroupFactoryFavourites;
import java.util.Date;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageCallsFavourites extends PageAbstractListExpandableActiveQuery {
	public PageCallsFavourites(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();

		Date day_begin = new Date(System.currentTimeMillis());
		day_begin.setHours(0);
		day_begin.setMinutes(0);

		ActiveQuery<ActiveCall> activeQuery = new ActiveQuery();
		activeQuery.from(new ActiveCall());
		activeQuery.where("_id>0 and favourite > 0");
		activeQuery.orderby("created desc");
		activeQuery.limit(1000);

		GroupExAdapter mExAdapter = new GroupExAdapter(getContext(), activeQuery, null, new ViewGroupFactoryFavourites());
		this.setAdapter(mExAdapter);

		for(int i = 0; i < getListView().getExpandableListAdapter().getGroupCount(); i++) getListView().expandGroup(i);

		// Слушатель, который инвалидирует данные в этом адаптере
		EManager.EventListener dataChangedListener = new EManager.EventListener() {
				@Override
				public void onEvent(EManager.Event e) {
						notifyDataSetChanged();
				}
		};

		// События, на которые мы обновляем содержимое
		App.getEManager().setEventListener(ActiveCall.DeleteEvent.class, dataChangedListener, null);
		App.getEManager().setEventListener(ActiveCall.AddToFavouritesEvent.class, dataChangedListener, null);
		App.getEManager().setEventListener(ActiveCall.RemoveFromFavouritesEvent.class, dataChangedListener, null);
	}

	public String getTitle() {
		return getContext().getString(R.string.favourites).toUpperCase();
	}

	public void onPageSelected() {
		super.onPageSelected();
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
