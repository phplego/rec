package ru.phplego.core.pages;

import android.view.MenuItem;
import android.view.View;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 26.04.12
* Time: 20:54
* To change this template use File | Settings | File Templates.
*/

public interface ContextMenuProvider extends View.OnCreateContextMenuListener {
	public boolean onContextItemSelected(MenuItem menuItem, View view);

}
