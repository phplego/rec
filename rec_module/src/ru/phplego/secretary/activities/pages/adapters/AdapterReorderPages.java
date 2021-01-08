package ru.phplego.secretary.activities.pages.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import ru.phplego.core.debug.Log;
import ru.phplego.core.pages.Page;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.PageManager;
import ru.phplego.secretary.view.sortablelist.*;

public final class AdapterReorderPages extends BaseAdapter implements RemoveListener, DropListener {
	private int [] mIds;

	private int [] mLayouts;

	private LayoutInflater mInflater;

	PageManager mPageManager;

	public AdapterReorderPages(Context context, int [] itemLayouts, int [] itemIDs, PageManager pageManager) {
		mPageManager = pageManager;
		init(context,itemLayouts,itemIDs);
	}

	private void init(Context context, int [] layouts, int [] ids) {
		mInflater = LayoutInflater.from(context);
		mIds = ids;
		mLayouts = layouts;
	}

	public int getCount() {
		return mPageManager.getPagesAll().size();
	}

	public Page getItem(int position) {
		return mPageManager.getPagesAll().get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (/*true ||*/ convertView == null) {
				convertView = mInflater.inflate(mLayouts[0], null);

				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(mIds[0]);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkboxEnabled1);

				convertView.setTag(holder);
		} else {
				holder = (ViewHolder) convertView.getTag();
		}
		holder.checkBox.setText(" "+mPageManager.getPagesAll().get(position).getTitle());

		holder.checkBox.setOnCheckedChangeListener(null);
		holder.checkBox.setChecked(getItem(position).isEnabled());


		/*if(mPageManager.isStartPage(getItem(position))){
				holder.text.setText("start");
				holder.checkBox.setTextColor(Color.MAGENTA);
		}
		else {
				holder.checkBox.setTextColor(getItem(position).isEnabled() ? Color.WHITE : Color.DKGRAY);
				holder.text.setText(" ");
		}*/
		holder.checkBox.setTextColor(getItem(position).isEnabled() ? Color.WHITE : Color.DKGRAY);

		holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
						Log.d("onCheckedChanged "+b);
						getItem(position).setEnabled(b);
						holder.text.setEnabled(b);
						holder.checkBox.setTextColor(b ? Color.WHITE : Color.DKGRAY);

						// Уведомляем, что страницы измениилсь
						mPageManager.notifyChanged();
						notifyDataSetChanged();
				}
		});

		return convertView;
	}

	static class ViewHolder {
		TextView text;

		CheckBox checkBox;

	}
	public void onRemove(int which) {
	}

	public void onDrop(int from, int to) {
	}

	private int mFrom =-1;

	public void onDrag(int from, int to) {
		if(to < 0 || to >= getCount()) return;
		if(mFrom == -1) return;
		if(mFrom != to){
				mPageManager.exchange(mFrom,to);
				mPageManager.saveOrder();
				mFrom = to;
				notifyDataSetChanged();
		}
	}

	public void onStartDrag(int from) {
		mFrom = from;
	}

}
