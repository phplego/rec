package ru.phplego.secretary.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.*;
import ru.phplego.core.pages.Page;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityPager;
import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 27.04.12
* Time: 22:53
* To change this template use File | Settings | File Templates.
*/

public class DialogPages {
	static boolean mIsModified =false;

	public static AlertDialog.Builder get(final ActivityPager activityPager) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activityPager);
		LinearLayout layout = new LinearLayout(activityPager);
		layout.setOrientation(LinearLayout.VERTICAL);
		ScrollView scroll = new ScrollView(activityPager);
		scroll.addView(layout);

		mIsModified = false;

		final List<String> titles = new ArrayList<String>();
		for(final Page page: activityPager.getPageManager().getPagesEnabled()) titles.add(page.getTitle());

		final Spinner spinner = new Spinner(activityPager);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activityPager,
						android.R.layout.simple_spinner_item, titles);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		for(final Page page: activityPager.getPageManager().getPagesAll()){
				CheckBox ch = new CheckBox(activityPager);
				ch.setText(" "+page.getTitle());
				ch.setTextSize(26);
				ch.setChecked(page.isEnabled());
				ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
								page.setEnabled(b);
								mIsModified = true;
								titles.clear();
								for(final Page page: activityPager.getPageManager().getPagesEnabled()) titles.add(page.getTitle());
								adapter.notifyDataSetChanged();

								// Устанавливаем текущую страницу из настроек
								int pos = 0; int i = 0;
								for(Page p: activityPager.getPageManager().getPagesEnabled()){
										String current_start_page = activityPager.mStartPageClassName.get(ActivityPager.DEFAULT_START_PAGE_CLASS_NAME);
										if(p.getClass().getSimpleName().equals(current_start_page))
												pos = i;
										i++;
								}
								spinner.setSelection(pos);

						}
				});
				layout.addView(ch);
		}



		// Устанавливаем текущую страницу из настроек
		int pos = 0; int i = 0;
		for(Page p: activityPager.getPageManager().getPagesEnabled()){
				String current_start_page = activityPager.mStartPageClassName.get(ActivityPager.DEFAULT_START_PAGE_CLASS_NAME);
				if(p.getClass().getSimpleName().equals(current_start_page))
						pos = i;
				i++;
		}
		spinner.setSelection(pos);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
						Page p = activityPager.getPageManager().getPagesEnabled().get(index);
						activityPager.mStartPageClassName.put(p.getClass().getSimpleName());
				}
				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {}
		});

		TextView labelForSpinner = new TextView(activityPager);
		labelForSpinner.setText(R.string.start_page);
		layout.addView(labelForSpinner);
		layout.addView(spinner);

		builder.setView(scroll);
		builder.setTitle(R.string.pages);
		builder.setCancelable(true);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
										if(mIsModified){ // Только если пользователь что-то изменил
												activityPager.restart();
										}
										dialog.cancel();
								}
						});
		return builder;
	}

}
