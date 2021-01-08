package ru.phplego.secretary.activities.pages;

import android.widget.*;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityPager;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PagePages extends Page {
	GroupExAdapter mExAdapter;

	public PagePages(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		ScrollView scroll = new ScrollView(getContext());
		scroll.addView(layout);

		for(final Page page: ((ActivityPager)getContext()).getPageManager().getPagesAll()){
				if(page.equals(this)) continue;
				CheckBox ch = new CheckBox(getContext());
				ch.setText(" "+page.getTitle());
				ch.setTextSize(26);
				ch.setChecked(page.isEnabled());
				ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
								page.setEnabled(b);
								getContext().restart(PagePages.class);
						}
				});

				layout.addView(ch);
		}

		setContentView(scroll);
	}

	public String getTitle() {
		return App.getStr(R.string.pages).toUpperCase();
	}

	public void onPageSelected() {
		super.onPageSelected();
	}

	public boolean isEnabled() {
		return true;
	}

}
