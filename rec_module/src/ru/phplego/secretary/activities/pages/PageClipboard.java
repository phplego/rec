package ru.phplego.secretary.activities.pages;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.phplego.core.EManager;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.secretary.App;
import ru.phplego.secretary.ClipboardMonitor;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.AdapterClipboard;
import ru.phplego.secretary.db.ActiveClipboardRecord;
import ru.phplego.secretary.db.TableClipboard;
import ru.phplego.secretary.dialogs.Dialogs;
import static android.view.Menu.NONE;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 19:08
*/

public class PageClipboard extends PageAbstractList implements AdapterView.OnItemClickListener {
	public PageClipboard(ActivityPagerAbstract activityPager) {
		super(activityPager);
	}

	public void setEnabled(boolean value) {
		super.setEnabled(value);

		// Если модуль включен
		if(value){
				// Поток, проверяющий Буфер обмена на предмет нового текста
				ClipboardMonitor.run_();
		}else{
				ClipboardMonitor.stop_();
		};
	}

	public String getTitle() {
		return (App.getStr(R.string.clipboard_history)).toUpperCase();
	}

	public AdapterClipboard getAdapter() {
		return (AdapterClipboard) super.getAdapter();
	}

	public void onCreate() {
		super.onCreate();
		getListView().setOnCreateContextMenuListener(this);
		getListView().setOnItemClickListener(this);
		getListView().setDivider(null);
		TextView tvFooter = new TextView(getContext());
		tvFooter.setFocusable(true);
		tvFooter.setPadding(20, 10, 20, 20);
		tvFooter.setText(R.string.clipboard_history_hint);
		getListView().addFooterView(tvFooter);
		setAdapter(new AdapterClipboard(getContext()));

		// Обработчик события "Буфер обмена изменен"
		App.getEManager().setEventListener(ClipboardMonitor.EventClipboardChanged.class, new EManager.EventListener<ClipboardMonitor.EventClipboardChanged>() {
				@Override
				public void onEvent(ClipboardMonitor.EventClipboardChanged e) {
						getContext().runOnUiThread(new Runnable() {
								@Override
								public void run() {
										getAdapter().notifyDataSetChanged();
								}
						});
				}
		}, null);
	}

	static final int CONTEXT_MENU_DELETE =1;

	static final int CONTEXT_MENU_COPY =3;

	static final int CONTEXT_MENU_SEND =4;

	static final int CONTEXT_MENU_PASTE =5;

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if(menuInfo == null) return;
		AdapterView.AdapterContextMenuInfo info1 = (AdapterView.AdapterContextMenuInfo) menuInfo;
		int item_id = (int)info1.id;

		ActiveClipboardRecord item = (ActiveClipboardRecord) new ActiveClipboardRecord().getInstance(item_id);
		menu.add(CONTEXT_MENU_DELETE, item_id, NONE, R.string.delete);

		if(item.getText().length() > 0){
				menu.add(CONTEXT_MENU_COPY, item_id, NONE, android.R.string.copy);
				menu.add(CONTEXT_MENU_SEND, item_id, NONE, R.string.send);
		}

		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		if(clipboard.hasText()){
				menu.add(CONTEXT_MENU_PASTE, item_id, NONE, android.R.string.paste);
		}
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		ActiveClipboardRecord item = (ActiveClipboardRecord) new ActiveClipboardRecord().getInstance(menuItem.getItemId());
		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		switch (menuItem.getGroupId()){
				case CONTEXT_MENU_DELETE:
						new ActiveClipboardRecord().getInstance(menuItem.getItemId()).delete();
						getAdapter().notifyDataSetChanged();
						break;
				case CONTEXT_MENU_COPY:
						clipboard.setText(item.getText());
						break;
				case CONTEXT_MENU_SEND:
						Intent i=new Intent(Intent.ACTION_SEND);
						i.setType("text/plain");
						i.putExtra(Intent.EXTRA_TEXT, item.getText());
						getContext().startActivity(i);
						break;
				// Вставить
				case CONTEXT_MENU_PASTE:
						if(clipboard.hasText()){
								item.setText(item.getText()+clipboard.getText());
								item.update();
								getAdapter().notifyDataSetChanged();
				   }
						break;
		}
		return true;
	}

	public void onItemClick(AdapterView <?> adapterView, View view, int i, long id) {
		ActiveClipboardRecord item = (ActiveClipboardRecord) new ActiveClipboardRecord().getInstance(id);
		ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(item.getText());
		//App.toast("Текст скопирован в буфер обмена");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0xFFCCFF, Menu.NONE, R.string.clear).setIcon(android.R.drawable.ic_menu_delete);
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
				case 0xFFCCFF:
						Dialogs.confirm(getContext(), R.string.delete_all_confirm,  new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
										TableClipboard.deleteAllRecords();
										getAdapter().notifyDataSetChanged();
								}
						}).show();
						return true;
		}
		return false;
	}

	public boolean isEnabledByDefault() {
		return false; // Невидим по умолчанию
	}

}
