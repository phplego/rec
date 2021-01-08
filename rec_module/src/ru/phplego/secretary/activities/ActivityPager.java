package ru.phplego.secretary.activities;

import android.content.*;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import ru.phplego.core.db.Database;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.core.preferences.SmartPrefString;
import ru.phplego.secretary.App;
import ru.phplego.secretary.Prefs;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.*;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.dialogs.DialogImportFromOtherCallRecording;
import ru.phplego.secretary.dialogs.Dialogs;

public class ActivityPager extends ActivityPagerAbstract implements SearchView.OnQueryTextListener {
    public static final String DEFAULT_START_PAGE_CLASS_NAME = PageCallsAll.class.getSimpleName();

    private static ActivityPager myInstance;

    public SmartPrefString mStartPageClassName;


    private PageManager myPageManager;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        //if(Prefs.theme.get() != 0) setTheme(Prefs.theme.get()); // Установка светлой темы
        super.onCreate(savedInstanceState);
        myInstance = this;

        // При первом запуске предлагаем импортировать базу из другого секретаря
        if(App.isFirstLaunch()){
            Prefs.is_first_launch.put(false);
            if(App.getOtherCallRecordingPackagesInstalled().size() > 0){
                Dialogs.confirm(this, R.string.do_you_want_to_import_from_other, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogImportFromOtherCallRecording.start(ActivityPager.this);
                    }
                }).show();
            }
        }

        mStartPageClassName = new SmartPrefString(this);


        myPageManager = new PageManager(this);



        //PageScroller pageScrollerFirst = new PageScroller(this);
        //addPage(pageScrollerFirst);
        for(Page page: myPageManager.getPagesEnabled()){
            if(page.isEnabled()) this.addPage(page);
        }
        //PageScroller pageScrollerLast = new PageScroller(this);
        //addPage(pageScrollerLast);

        //pageScrollerFirst.setTitle(getPages().get(getPages().size()-2).getTitle());
        //pageScrollerLast.setTitle(getPages().get(1).getTitle());


        // Устанавливаем текущую страницу, если она передана в Intent
        scrollToPageFromIntent();


        // Для бесплатной версии
        if(App.isVersionFree()){
            // Добавляем кнопку "Обновиться до ПРО версии"
            RelativeLayout layout = new RelativeLayout(this);
            Button upgradeButton = new Button(this);
            upgradeButton.setText(R.string.upgrade_to_pro_version);
            upgradeButton.getBackground().setColorFilter(getResources().getColor(R.color.my_tabs_bg), PorterDuff.Mode.MULTIPLY);
            upgradeButton.setTextColor(Color.parseColor("#FFFFFF"));
            upgradeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + getString(R.string.package_pro)));
                    startActivity(intent);
                }
            });

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            upgradeButton.setLayoutParams(lp);
            layout.addView(upgradeButton);
            addContentView(layout, lp);

            // Отображаем рекламный блок
            findViewById(R.id.adBlock).setVisibility(View.VISIBLE);
        }

    }


    public int getStartPage() {
        String startPageCls = mStartPageClassName.get(DEFAULT_START_PAGE_CLASS_NAME);
        int startPage = getPagePositionByClassName(startPageCls);
        if(startPage == -1) return 1;
        return startPage;
    }

    public PageManager getPageManager() {
        return myPageManager;
    }

    public void onStart() {
        super.onStart();

    }

    public void onResume() {
        super.onResume();

    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        if(menuItem.getGroupId() == 0xff){ // Меню переключения страниц
            setCurrentPage(menuItem.getItemId(), true);
            return false;
        }
        return super.onContextItemSelected(menuItem);
    }

    private static final int MENU_HELP =1001;

    private static final int MENU_PAGES =1002;

    private static final int MENU_PREFS =1003;

    private static final int MENU_EXIT =1004;

    private static final int MENU_IMPORT_DB_FROM_OTHER_SECRETARY =1005;

    private static final int MENU_IMPORT_DB_FROM_FILE =1006;

    private static final int MENU_EXPORT_DB_TO_FILE =1007;

    public boolean onCreateOptionsMenu(Menu menu) {
        if(super.onCreateOptionsMenu(menu)) return true;

        // Place an action bar item for searching.

        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(this);
        sv.setOnQueryTextListener(this);
        item.setActionView(sv);

        int ord = 0;
        // Помощь
        item = menu.add(Menu.NONE, MENU_HELP, 100 + ord++*10, R.string.help).setIcon(android.R.drawable.ic_menu_help);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        // Модули
        menu.add(Menu.NONE, MENU_PAGES,100 + ord++*10, R.string.pages).setIcon(R.drawable.icon_menu_pages);
        // Настройки
        menu.add(Menu.NONE, MENU_PREFS,100 + ord++*10, R.string.options).setIcon(android.R.drawable.ic_menu_preferences);
        // Инструменты
        SubMenu menuTools = menu.addSubMenu(Menu.NONE, Menu.NONE, 100 + ord++*10, R.string.tools).setIcon(android.R.drawable.ic_menu_directions);
        // Импортировать из другого секретаря
        menuTools.add(Menu.NONE, MENU_IMPORT_DB_FROM_OTHER_SECRETARY, Menu.NONE, R.string.import_from_secretary);
        // Импортировать из файла
        menuTools.add(Menu.NONE, MENU_IMPORT_DB_FROM_FILE, Menu.NONE, R.string.import_from_file);
        // Экспорт
        menuTools.add(Menu.NONE, MENU_EXPORT_DB_TO_FILE, Menu.NONE, R.string.export);
        // Выход
        menu.add(Menu.NONE, MENU_EXIT, 100 + ord++*10, R.string.exit).setIcon(R.drawable.exit);



        return true;
    }

    static final int REQUEST_IMPORT =1;

    static final int REQUEST_EXPORT =2;

    public boolean onOptionsItemSelected(MenuItem item) {
        if(getCurrentPage() != null){
            // Вызываем обработчик меню текущей страницы
            if(getCurrentPage().onOptionsItemSelected(item)) return true;
        }
        String file_name = "CallRecordingBeta.db";
        if(App.isVersionFree()) file_name = "CallRecording.db";
        String full_file_name = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+file_name;

        switch(item.getItemId()){
            case MENU_EXIT:
                finish();
                break;
            case MENU_HELP:
                Intent intent1 = new Intent(this, ActivityHelp.class);
                startActivity(intent1);
                break;
            case MENU_PAGES:
                //DialogPages.get(this).show();
                Intent i = new Intent(this, ActivityReorderPages.class);
                startActivity(i);
                break;
            case MENU_PREFS:
                Intent intent2 = new Intent(this, ActivityPrefs.class);
                startActivity(intent2);
                break;
            // Импортировать из другого секретаря
            case MENU_IMPORT_DB_FROM_OTHER_SECRETARY:
                int cnt = App.getOtherCallRecordingPackagesInstalled().size();
                Log.d("MENU_IMPORT_DB_FROM_OTHER_SECRETARY, cnt = " + cnt);
                if(cnt == 0) break;
                DialogImportFromOtherCallRecording.start(this);
                break;
            case MENU_EXPORT_DB_TO_FILE:
                ActivityFileDialog.run(this, REQUEST_EXPORT, full_file_name, new String[]{"db"});
                break;
            case MENU_IMPORT_DB_FROM_FILE:
                ActivityFileDialog.run(this, REQUEST_IMPORT, null, new String[]{"db"});
                break;
        }

        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        String full_file_name = data.getStringExtra(ActivityFileDialog.RESULT_PATH);
        if(full_file_name == null) return;
        switch (requestCode){
            case REQUEST_IMPORT:
                boolean imported = Database.importFromFile(full_file_name);
                if(imported){
                    App.toast(getString(R.string.database_import_ok) + " " + full_file_name, true);
                }
                else
                    App.toast(getString(R.string.database_import_err)+" "+full_file_name, true);
                App.initDatabase(); // Реинициализируем базу данных
                Intent i = new Intent(this, ActivityPager.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case REQUEST_EXPORT:
                boolean ok = Database.exportToFile(full_file_name);
                if(ok)
                    App.toast(getString(R.string.database_export_ok)+" "+full_file_name, true);
                else
                    App.toast(getString(R.string.database_export_err) + " " + full_file_name, true);
                break;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void onCenterItemClick(int position) {
        super.onCenterItemClick(position);
    }

    public void onPageScrollStateChanged(int state) {
		/*if (state == ViewPager.SCROLL_STATE_IDLE){
				if(getCurrentPagePosition() == 0)
						setCurrentPage(getPages().size() - 2, false);
				if(getCurrentPagePosition() == getPages().size() - 1)
						setCurrentPage(1, false);
		}*/
    }

    public static ActivityPager getInstance() {
        return myInstance;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.d("onQueryTextChange: " + s);
        Page currentPage = getCurrentPage();
        if(currentPage != null)
            getCurrentPage().onSearch(s);
        return false;
    }
}
