package ru.phplego.secretary.activities.pages.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveNote;
import ru.phplego.secretary.db.TableNotes;
import java.util.Vector;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 19:17
*/

public class AdapterNotepad implements ListAdapter {
	private Context mContext;

	private ActiveQuery mActiveQuery =new ActiveQuery();

	Vector <ActiveNote> mNotes =new Vector<ActiveNote>();

	private DataSetObserver mDataSetObserver;

	public AdapterNotepad(Context context) {
		mContext = context;

		mActiveQuery.from(new ActiveNote());
		mActiveQuery.orderby(TableNotes.created + " desc");
		mActiveQuery.offset(1); // Все записи, кроме последней
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

	public ActiveNote getItem(int i) {
		if(i >= mNotes.size() || i < 0) return new ActiveNote();
		return mNotes.get(i);
	}

	public long getItemId(int i) {
		return getItem(i).getId();
	}

	public boolean hasStableIds() {
		return true;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		LinearLayout layout;
		if(view == null)
				layout = (LinearLayout)App.inflate(R.layout.page_notepad_note_list_item);
		else
				layout = (LinearLayout) view;
		TextView tvNote = (TextView)layout.findViewById(R.id.notepadTvNote);
		TextView tvDate = (TextView)layout.findViewById(R.id.notepadTvDate);
		tvNote.setText(getItem(position).getText());
		tvDate.setText(getItem(position).getCreatedDate().format("HH:mm, %:", "E, d MMMMMMM"));
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
