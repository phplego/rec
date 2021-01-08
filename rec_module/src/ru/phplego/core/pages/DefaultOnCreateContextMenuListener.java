package ru.phplego.core.pages;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ExpandableListView;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 26.04.12
* Time: 20:30
* To change this template use File | Settings | File Templates.
*/

public class DefaultOnCreateContextMenuListener implements View.OnCreateContextMenuListener {
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
		// Если это контекстное меню раскрывающегося списка
		if(contextMenuInfo instanceof ExpandableListView.ExpandableListContextMenuInfo){
				ExpandableListView.ExpandableListContextMenuInfo info
								= (ExpandableListView.ExpandableListContextMenuInfo) contextMenuInfo;
				if(info.targetView instanceof View.OnCreateContextMenuListener){
						((View.OnCreateContextMenuListener) info.targetView).onCreateContextMenu(contextMenu, view, info);
				}
		}
	}

}
