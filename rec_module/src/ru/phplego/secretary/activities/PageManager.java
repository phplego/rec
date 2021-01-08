package ru.phplego.secretary.activities;

import android.database.DataSetObservable;
import ru.phplego.core.StringUtils;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.Prefs;
import ru.phplego.secretary.activities.pages.*;
import java.util.*;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 21.08.12
* Time: 20:04
*/

public class PageManager extends DataSetObservable {
	public class PagesCollection extends Vector <Page> {
	}
	private ActivityPagerAbstract mActivityPager;

	public PagesCollection mPagesAll =new PagesCollection();

	public PageManager(ActivityPagerAbstract activityPager) {
		mActivityPager = activityPager;

		mPagesAll.add(new PageNotepad(mActivityPager));
		mPagesAll.add(new PageCallsTalks(mActivityPager));
		mPagesAll.add(new PageCallsAll(mActivityPager));
		//mPagesAll.add(new PageEverything(mActivityPager));
		mPagesAll.add(new PageCallsNoted(mActivityPager));
		mPagesAll.add(new PageCallsFavourites(mActivityPager));
		mPagesAll.add(new PageCallsPlaned(mActivityPager));
		mPagesAll.add(new PageClipboard(mActivityPager));
		//mPagesAll.add(new PageOptions(mActivityPager));
		//mPagesAll.add(new PageHelp(mActivityPager));
		if(Prefs.developer_mode.get()) mPagesAll.add(new PageTest(mActivityPager));
		//mPagesAll.add(new PagePages(mActivityPager));

		restoreOrder();
	}

	public PagesCollection getPagesEnabled() {
		PagesCollection ret = new PagesCollection();
		for(Page page: mPagesAll){
				if(page.isEnabled()) ret.add(page);
		}
		return ret;
	}

	public PagesCollection getPagesAll() {
		return mPagesAll;
	}

	public void exchange(int position1, int position2) {
		Page tmp = mPagesAll.get(position1);
		mPagesAll.setElementAt(mPagesAll.get(position2), position1);
		mPagesAll.setElementAt(tmp, position2);
		notifyChanged();
	}

	public void saveOrder() {
		Vector<String> class_names = new Vector<String>();
		for(Page page: mPagesAll){
				class_names.add(page.getClass().getSimpleName());
		}
		String modules_list = StringUtils.join(class_names, ",");
		Prefs.modules_order.put(modules_list);
	}

	public void restoreOrder() {
		String modules_list = Prefs.modules_order.get();
		if(modules_list.length() == 0) return;

		final String[] class_names = modules_list.split(",");
		// Сортируем так же как в сохраненной строке
		Collections.sort(mPagesAll, new Comparator<Page>() {
				public int compare(Page p1, Page p2) {
						int savedPosition1 = Arrays.asList(class_names).indexOf(p1.getClass().getSimpleName());
						int savedPosition2 = Arrays.asList(class_names).indexOf(p2.getClass().getSimpleName());
						if(savedPosition1 > savedPosition2) return 1;
						if(savedPosition1 < savedPosition2) return -1;
						return 0;
				}
		});

		notifyChanged();
	}

	public List <String> getTitlesOfEnabledPages() {
		List<String> titles = new ArrayList<String>();

		for(final Page page: getPagesEnabled()) titles.add(page.getTitle());
		return titles;
	}

	public boolean isStartPage(Page p) {
		String current_start_page = ActivityPager.getInstance().mStartPageClassName.get(ActivityPager.DEFAULT_START_PAGE_CLASS_NAME);
		return current_start_page.equals(p.getClass().getSimpleName());
	}

	/**
	* Позиция, на которой находится стартовая страница среди Enabled страниц (используется для спиннеров)
	* @return
	*/
	public int getPositionOfStartPage() {
		int pos = 0; int i = 0;
		for(Page p: getPagesEnabled()){
				if(isStartPage(p))
						pos = i;
				i++;
		}
		return pos;
	}

	public void setStartPage(Page p) {
		if(!p.isEnabled()) throw new Error("Cannot set disabled page as start");
		ActivityPager.getInstance().mStartPageClassName.put(p.getClass().getSimpleName());
	}

}
