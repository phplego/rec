package ru.phplego.secretary.activities.pages.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveClipboardRecord;
import ru.phplego.secretary.db.TableClipboard;
import java.util.Vector;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 19:17
*/

public class AdapterClipboard implements ListAdapter {
	private Context mContext;

	private ActiveQuery mActiveQuery =new ActiveQuery();

	Vector <ActiveClipboardRecord> mNotes =
	new Vector<ActiveClipboardRecord>()
	;

	private DataSetObserver mDataSetObserver;

	public AdapterClipboard(Context context) {
		mContext = context;

		mActiveQuery.from(new ActiveClipboardRecord());
		mActiveQuery.orderby(TableClipboard.created + " desc");
		mActiveQuery.limit(100);
		mNotes = mActiveQuery.objects();
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEnabled(int i) {
		return true;
	}

	public void registerDataSetObserver(DataSetObserver dataSetObserver) {
		mDataSetObserver = dataSetObserver;
	}

	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
	}

	public void notifyDataSetChanged() {
		if(mDataSetObserver == null) return;
		mDataSetObserver.onChanged();
		this.onDataSetChanged();
	}

	private void onDataSetChanged() {
		// Здесь делается повторный запрос к базе
		mNotes = mActiveQuery.objects();
	}

	public int getCount() {
		return mNotes.size();
	}

	public ActiveClipboardRecord getItem(int i) {
		if(i >= mNotes.size() || i < 0) return new ActiveClipboardRecord();
		return mNotes.get(i);
	}

	public long getItemId(int i) {
		return getItem(i).getId();
	}

	public boolean hasStableIds() {
		return true;
	}

	private class ViewHolder {
		TextView tvNote;

		TextView tvDate;

		LinearLayout layoutBorder;

	}
	public View getView(int position, View view, ViewGroup viewGroup) {
		LinearLayout layout;
		ViewHolder holder;
		if(view == null){
				layout = (LinearLayout)App.inflate(R.layout.page_notepad_note_list_item);
				holder = new ViewHolder();
				holder.tvNote = (TextView)layout.findViewById(R.id.notepadTvNote);
				holder.tvDate = (TextView)layout.findViewById(R.id.notepadTvDate);
				holder.layoutBorder = (LinearLayout)layout.findViewById(R.id.noteBorder);
				layout.setTag(holder);
		}
		else{
				layout = (LinearLayout) view;
				holder = (ViewHolder) layout.getTag();
		}

		if(position == 0){
				holder.tvDate.setText(R.string.current_value_clipboard);
				holder.tvNote.setTextColor(Color.WHITE);
				holder.tvDate.setTextColor(0x99FFFFFF);
				holder.layoutBorder.getBackground().setColorFilter(0x997B9726, PorterDuff.Mode.DARKEN);
		}
		else{
				holder.tvDate.setText(getItem(position).getCreatedDate().format("HH:mm, %:", "E, d MMMMMMM"));
				holder.tvNote.setTextColor(Color.LTGRAY);
				holder.tvDate.setTextColor(0x88AAAAAA);
				holder.layoutBorder.getBackground().clearColorFilter();
		}
		holder.tvNote.setText(getItem(position).getText());

		return layout;
	}

	public int getItemViewType(int i) {
		return 0;
	}

	public int getViewTypeCount() {
		return 1;
	}

	public boolean isEmpty() {
		return false;
	}

}
