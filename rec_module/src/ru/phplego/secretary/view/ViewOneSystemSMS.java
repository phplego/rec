package ru.phplego.secretary.view;

import android.content.Context;
import android.view.*;
import android.widget.*;
import ru.phplego.core.Cachable;
import ru.phplego.core.pages.ContextMenuProvider;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.system.ActiveSystemSMS;

public class ViewOneSystemSMS extends LinearLayout implements ContextMenuProvider, Cachable {
	private ActiveSystemSMS mActiveSMS;

	private Context mContext;

	private TextView tvDate;

	private TextView tvText;

	private ImageButton ibPlay;

	private ImageButton ibPause;

	private ImageButton ibCall;

	private ImageView ivIncomingIndicator;

	private SeekBar mSeekBar;

	private static final int CONTEXT_DELETE =1;

	public ViewOneSystemSMS(Context context, ActiveSystemSMS item) {
		super(context);
		mContext = context;
		LinearLayout layout = (LinearLayout) App.inflate(R.layout.page_everything_sms_item);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -2);
		layout.setLayoutParams(lp2);

		tvDate = (TextView)layout.findViewById(R.id.date);
		tvText = (TextView)layout.findViewById(R.id.text);

		this.addView(layout);

		fillFromActiveRecord(item);
	}

	public void fillFromActiveRecord(ActiveSystemSMS item) {
		mActiveSMS = item;

		tvDate.setText(mActiveSMS.getDate().format("HH:mm, %:", "E, d MMMMMMM"));
		tvText.setText(mActiveSMS.getText());
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		//menu.add(CONTEXT_SEND, (int)info.id, NONE, R.string.send_record);
	}

	public boolean onContextItemSelected(MenuItem menuItem, View view) {
		switch(menuItem.getGroupId()){
				// Удаление записи
				case CONTEXT_DELETE:
						mActiveSMS.delete();
						break;
		}
		return true;
	}

	public boolean isInvalid() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

}
