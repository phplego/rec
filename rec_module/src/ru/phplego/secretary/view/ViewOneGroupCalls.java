package ru.phplego.secretary.view;

import android.content.Context;
import android.graphics.Color;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 05.06.12
* Time: 17:30
*/

public class ViewOneGroupCalls extends ViewOneGroupByDate {
	public ViewOneGroupCalls(Context context, GroupExAdapter.Group group) {
		super(context, group);

		mTvTitle.setTextColor(Color.parseColor("#99BBFF"));  // Нежносереневый (самый первый)
	}

}
