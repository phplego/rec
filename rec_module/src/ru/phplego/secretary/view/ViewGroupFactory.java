package ru.phplego.secretary.view;

import android.content.Context;
import android.view.View;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 21:14
* To change this template use File | Settings | File Templates.
*/

public abstract class ViewGroupFactory {
	public abstract View createView(Context context, GroupExAdapter.Group group);

}
