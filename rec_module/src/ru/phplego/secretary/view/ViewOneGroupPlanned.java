package ru.phplego.secretary.view;

import android.content.Context;
import android.graphics.Color;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 21:27
* To change this template use File | Settings | File Templates.
*/

public class ViewOneGroupPlanned extends ViewOneGroupByDate {
	public ViewOneGroupPlanned(Context context, GroupExAdapter.Group group) {
		super(context, group);

		mTvTitle.setTextColor(Color.parseColor("#FF00FF"));
	}

}
