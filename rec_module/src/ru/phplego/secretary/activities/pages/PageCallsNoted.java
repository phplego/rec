package ru.phplego.secretary.activities.pages;

import android.view.View;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.EManager;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.view.ViewGroupFactoryNoted;
import ru.phplego.secretary.view.ViewOneRecord;

/**
* Created with IntelliJ IDEA.
* User: Oleg D.
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageCallsNoted extends PageAbstractListExpandableActiveQuery {
	View mView;

	public PageCallsNoted(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();

		ActiveQuery<ActiveCall> activeQuery = new ActiveQuery();
		activeQuery.from(new ActiveCall());
		activeQuery.and("note != ''");
		activeQuery.orderby("created desc");
		activeQuery.limit(1000);


		GroupExAdapter mExAdapter = new GroupExAdapter(getContext(), activeQuery, null, new ViewGroupFactoryNoted());
		this.setAdapter(mExAdapter);

		// Слушатель, который инвалидирует данные в этом адаптере
		EManager.EventListener dataChangedListener = new EManager.EventListener() {
				@Override
				public void onEvent(EManager.Event e) {
						notifyDataSetChanged();
				}
		};

		// Слушаем событие удалени/добавления заметки, и уведомляем адаптер
		App.getEManager().setEventListener(ActiveCall.InsertEvent.class, dataChangedListener, null);
		App.getEManager().setEventListener(ActiveCall.DeleteEvent.class, dataChangedListener, null);

		// Слушаем событие удалени/добавления заметки, и уведомляем адаптер
		App.getEManager().setEventListener(ActiveCall.DeleteEvent.class, dataChangedListener, null);
		App.getEManager().setEventListener(ViewOneRecord.OnNoteAddOrRemoveEvent.class, dataChangedListener, null);


		for(int i = 0; i < getListView().getExpandableListAdapter().getGroupCount(); i++) getListView().expandGroup(i);
	}

	public String getTitle() {
		return getContext().getString(R.string.noted_calls).toUpperCase();
	}

	public void onPageSelected() {
		super.onPageSelected();
	}

}
