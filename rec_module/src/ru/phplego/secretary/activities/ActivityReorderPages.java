package ru.phplego.secretary.activities;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.phplego.core.debug.Log;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.activities.pages.adapters.AdapterReorderPages;
import ru.phplego.secretary.view.sortablelist.*;
import ru.phplego.rec_module.R;

public class ActivityReorderPages extends ListActivity {
	PageManager mPageManager;

	private boolean mIsModified =false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_reorder_pages);

		Button buttonOk = (Button)findViewById(R.id.ok);
		buttonOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						if(mIsModified){ // Только если пользователь что-то изменил
								ActivityPager.getInstance().restart();
						}
						ActivityReorderPages.this.finish();
				}
		});

		mPageManager = ActivityPager.getInstance().getPageManager();

		// Спиннер выбора стартовой страницы
		final Spinner spinner = (Spinner)findViewById(R.id.startPageSpinner);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, mPageManager.getTitlesOfEnabledPages());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		// Устанавливаем текущую страницу из настроек
		spinner.setSelection(mPageManager.getPositionOfStartPage());

		mPageManager.registerObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
						Log.d("DataSetObserver.onChanged ");
						mIsModified = true;

						final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityReorderPages.this,
										android.R.layout.simple_spinner_item, mPageManager.getTitlesOfEnabledPages());
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner.setAdapter(adapter);

						// Устанавливаем текущую страницу из настроек
						int posOfStartPage = mPageManager.getPositionOfStartPage();
						spinner.setSelection(posOfStartPage);
						getListView().invalidateViews();
				}
		});

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
						Log.d("onItemSelected");

						Page p = mPageManager.getPagesEnabled().get(index);
						if(mPageManager.isStartPage(p)) return;
						ActivityPager.getInstance().mStartPageClassName.put(p.getClass().getSimpleName());
						mPageManager.notifyChanged();

						getListView().invalidateViews();
				}
				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {}
		});


		ListView listView = getListView();
		listView.setDivider(null);

		setListAdapter(new AdapterReorderPages(this, new int[]{R.layout.reorder_pages_dragitem}, new int[]{R.id.TextView01}, mPageManager));


		if (listView instanceof ListViewSortable) {
						((ListViewSortable) listView).setDropListener(mDropListener);
						((ListViewSortable) listView).setDragListener(mDragListener);
		}
	}

	private DropListener mDropListener =
	new DropListener() {
		public void onDrop(int from, int to) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof AdapterReorderPages) {
				//((AdapterReorderPages)adapter).onDrop(from, to);
				//getListView().invalidateViews();
			}
		}
	}
	;

	private DragListener mDragListener =
	new DragListener() {
			public void onDrag(int x, int y, ListView listView, int from, int to) {
				ListAdapter adapter = getListAdapter();
				((AdapterReorderPages)adapter).onDrag(from, to);
			}

			public void onStartDrag(View itemView, int from) {
				ListAdapter adapter = getListAdapter();
				((AdapterReorderPages)adapter).onStartDrag(from);
			}

			public void onStopDrag(View itemView) {
			}

	}
	;

}
