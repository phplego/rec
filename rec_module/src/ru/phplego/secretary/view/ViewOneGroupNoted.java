package ru.phplego.secretary.view;

import android.content.Context;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 21:27
* To change this template use File | Settings | File Templates.
*/

public class ViewOneGroupNoted extends ViewOneGroupByDate {
	public ViewOneGroupNoted(Context context, GroupExAdapter.Group group) {
		super(context, group);

		//mTvTitle.setTextColor(Color.parseColor("#FFFFFF"));
	}

}
