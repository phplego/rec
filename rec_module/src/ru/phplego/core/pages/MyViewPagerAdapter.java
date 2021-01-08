package ru.phplego.core.pages;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.*;
import ru.phplego.core.etc.viewpagerindicator.TitleProvider;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 26.04.12
* Time: 19:12
* To change this template use File | Settings | File Templates.
*/

class MyViewPagerAdapter extends PagerAdapter implements TitleProvider {
	private Context mContext;

	private List <Page> mPages =new Vector<Page>();

	private int mLastSelectedPage;

	public MyViewPagerAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public void addPage(Page page) {
		page.setPosition(mPages.size()); //Чтобы сама страница знала на каком она месте
		mPages.add(page);
        notifyDataSetChanged();
	}

	public void removePageAt(int position) {
		mPages.remove(position);
		int i = 0;
		for(Page page: mPages){
				page.setPosition(i);
				i++;
		}
		_page_view_cache.clear();
		notifyDataSetChanged();
	}

	public String getTitle(int pageIndex) {
		return mPages.get(pageIndex).getTitle();
	}

	public int getCount() {
		return mPages.size();
	}

	private Hashtable <Integer, View> _page_view_cache =
	new Hashtable<Integer, View>()
	;

	public Object instantiateItem(ViewGroup collection, int pageIndex) {
		if(_page_view_cache.containsKey(pageIndex)){
				View view = _page_view_cache.get(pageIndex);
				collection.removeView(view);
				collection.addView(view);
				return view;
		}
		Page page = mPages.get(pageIndex);
		if(page.isCreated()) return page.getContentView();
		page.onCreate(); // Вот здесь то и создается страница
		collection.addView(page.getContentView());
		_page_view_cache.put(pageIndex, page.getContentView());
		return page.getContentView();
	}

	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	public Parcelable saveState() {
		return null;
	}

	public void onPageSelected(int pageIndex) {
		if(mLastSelectedPage != pageIndex && mLastSelectedPage < mPages.size())
				mPages.get(mLastSelectedPage).onPageOut();

		if(pageIndex < mPages.size())
				mPages.get(pageIndex).onPageSelected();
		mLastSelectedPage = pageIndex;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
	}

}
