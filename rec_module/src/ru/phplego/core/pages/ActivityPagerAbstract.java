package ru.phplego.core.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ExpandableListView;
import ru.phplego.core.Application;
import ru.phplego.core.Res;
import ru.phplego.core.etc.viewpagerindicator.TitlePageIndicator;

import java.lang.reflect.Field;
import java.util.Vector;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 08.05.12
* Time: 20:30
* To change this template use File | Settings | File Templates.
*/

public class ActivityPagerAbstract extends Activity implements ViewPager.OnPageChangeListener, TitlePageIndicator.OnCenterItemClickListener {
	public static final String EXTRA_CURRENT_PAGE ="EXTRA_CURRENT_PAGE";

	public static final String EXTRA_CURRENT_PAGE_CLASS ="EXTRA_CURRENT_PAGE_CLASS";

	protected Vector <Page> myPages =new Vector<Page>();

	private MyViewPagerAdapter myAdapter;

	private ViewPager myPager;

	private TitlePageIndicator myIndicator;

	private int myCurrentPage;

	private Menu myOptionsMenu;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_pager);
		this.myPager     = (ViewPager)          findViewById(R.id.viewpager);
		this.myIndicator = (TitlePageIndicator) findViewById(R.id.indicator);

		this.myAdapter   = new MyViewPagerAdapter(this);

		this.myPager.setAdapter(this.myAdapter);
		//this.myPager.setOffscreenPageLimit(200);

		this.myIndicator.setViewPager(myPager);
		this.myIndicator.setOnPageChangeListener(this);
		this.myIndicator.setOnCenterItemClickListener(this);

        //Show "Thee dots" menu even if device have hardware "Menu button"
        this.getOverflowMenu();
	}

    /*
    This method shows "Thee dots" menu even if device have hardware "Menu button"
    */
    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void onStart() {
		super.onStart();
	}

	public void onResume() {
		super.onResume();
		for(Page page: myPages) page.onResume();
	}

	public void onBackPressed() {
		boolean default_behaviour = getCurrentPage().onBackPressed();
		if(default_behaviour) super.onBackPressed();
	}

	public boolean onContextItemSelected(MenuItem menuItem) {
		// Если у кликнутого элемента есть интерфейс ContextMenuProvider
		if(menuItem.getMenuInfo() instanceof ExpandableListView.ExpandableListContextMenuInfo){
				ExpandableListView.ExpandableListContextMenuInfo info
								= (ExpandableListView.ExpandableListContextMenuInfo) menuItem.getMenuInfo();
				if(info.targetView instanceof ContextMenuProvider){
						((ContextMenuProvider) info.targetView).onContextItemSelected(menuItem, info.targetView);
				}
		}
		// По умолчанию передаем нажатие текущему модулю (Page)
		getCurrentPage().onContextItemSelected(menuItem);
		return false;
	}

	public void onPageScrolled(int i, float v, int i1) {
	}

	public void onPageSelected(int i) {
		myCurrentPage = i;
		myAdapter.onPageSelected(i);
		invalidateOptionsMenu();
        getOverflowMenu();
	}

	public void onPageScrollStateChanged(int state) {
	}

	public void onCenterItemClick(int position) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		myOptionsMenu = menu;
		if(getCurrentPage() == null) return false;
		if(getCurrentPage().onCreateOptionsMenu(menu)) return true; // Меню текущей страницы
		return false;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if(myOptionsMenu.size() == 0) onCreateOptionsMenu(menu);
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if(getCurrentPage() == null) return;
		getCurrentPage().onCreateContextMenu(menu, v, menuInfo);
	}

	protected void scrollToPageFromIntent() {
		// Если передано имя класса страницы, это имеет больший приоритет
		String currentPageClass = getIntent().getStringExtra(EXTRA_CURRENT_PAGE_CLASS);
		if(currentPageClass != null){
				int pagePosition = getPagePositionByClassName(currentPageClass);
				if(pagePosition != -1)
						this.setCurrentPage(pagePosition, false);
				return;
		}
		// Устанавливаем текущую страницу либо по индексу из Intent либо по возврату метода getStartPage()
		int pagePosition = getIntent().getIntExtra(EXTRA_CURRENT_PAGE, getStartPage());
		if(pagePosition < 0 || pagePosition >= this.getPages().size())
				pagePosition = 0;
		this.setCurrentPage(pagePosition, false);
	}

	public void addPage(Page page) {
		myPages.add(page);
		myAdapter.addPage(page);
	}

	public int getPagePositionByClassName(String className) {
		int i = 0;
		for(Page page: myPages){
				if(page.getClass().getSimpleName().equals(className)) return i;
				i++;
		}
		return -1;
	}

	public int getStartPage() {
		return 0;
	}

	public Vector <Page> getPages() {
		return myPages;
	}

	public ViewPager getViewPager() {
		return myPager;
	}

	public Page getCurrentPage() {
		if(myPages.size() <= myCurrentPage) return null;
		if(myCurrentPage < 0) return null;
		return myPages.get(myCurrentPage);
	}

	public int getCurrentPagePosition() {
		return myCurrentPage;
	}

	public void setCurrentPage(int pageIndex, boolean smoothScroll) {
		myIndicator.setCurrentItem(pageIndex, smoothScroll);
		//myPager.setCurrentItem(pageIndex, smoothScroll);
	}

	public void restart() {
		Intent intent = new Intent(this, this.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public void restart(int pagePosition) {
		Intent intent = new Intent(this, this.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(EXTRA_CURRENT_PAGE, pagePosition);
		startActivity(intent);
	}

	public void restart(Class <? extends Page> clazz) {
		this.restart(clazz.getSimpleName());
	}

	public void restart(String simple_class_name) {
		Intent intent = new Intent(this, this.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(EXTRA_CURRENT_PAGE_CLASS, simple_class_name);
		startActivity(intent);
	}

}
