package ru.phplego.secretary.view;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.phplego.core.pages.ContextMenuProvider;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.system.ActiveSystemCall;

public class ViewOneSystemCall extends LinearLayout implements ContextMenuProvider {
	private View mLayout;

	private ActiveSystemCall mActiveRecord;

	private Context mContext;

	private TextView tvInfo;

	private TextView tvPhone;

	private TextView tvNote;

	private ImageButton ibCall;

	private ImageView ivIncomingIndicator;

	private SeekBar mSeekBar;

	private static final int CONTEXT_DELETE =1;

	public ViewOneSystemCall(Context context, ActiveSystemCall item) {
		super(context);
		mContext = context;

		LinearLayout layout = (LinearLayout)App.inflate(R.layout.page_everything_system_call_item);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -2);
		layout.setLayoutParams(lp2);

		ibCall      = (ImageButton)layout.findViewById(R.id.call_button);
		tvPhone     = (TextView)layout.findViewById(R.id.number);

		ibCall.setFocusable(false);

		this.addView(layout);

		fillFromActiveRecord(item);
	}

	public void fillFromActiveRecord(ActiveSystemCall item) {
		mActiveRecord = item;

		tvPhone.setText(mActiveRecord.getCachedName(true));
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		//menu.add(CONTEXT_SEND, (int)info.id, NONE, R.string.send_record);
	}

	public boolean onContextItemSelected(MenuItem menuItem, View view) {
		switch(menuItem.getGroupId()){
				// Удаление записи
				case CONTEXT_DELETE:
						mActiveRecord.delete();
						break;
		}
		return true;
	}

}
