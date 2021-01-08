package ru.phplego.secretary.activities.pages;

import android.content.DialogInterface;
import android.view.*;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.EManager;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.DefaultOnCreateContextMenuListener;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.dialogs.Dialogs;
import ru.phplego.secretary.view.ViewGroupFactoryCalls;

/**
 * Created with IntelliJ IDEA.
 * User: Oleg
 * Date: 28.04.12
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */

public class PageCallsAll extends PageAbstractListExpandableActiveQuery {
    ActiveQuery <ActiveCall> mActiveQuery =new ActiveQuery();

    public PageCallsAll(ActivityPagerAbstract context) {
        super(context);
    }

    public void onCreate() {
        super.onCreate();

        mActiveQuery.from(new ActiveCall());
        //mActiveQuery.where("duration > 0");
        mActiveQuery.orderby("created desc");
        mActiveQuery.limit(1000);


        GroupExAdapter exAdapter = new GroupExAdapter(getContext(), mActiveQuery, null, new ViewGroupFactoryCalls());
        this.setAdapter(exAdapter);

        getListView().setOnCreateContextMenuListener(new DefaultOnCreateContextMenuListener());
        getListView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        getListView().expandGroup(0);

        EManager.EventListener dataChangedListener = new EManager.EventListener() {
            @Override
            public void onEvent(EManager.Event e) {
                Log.d("dataChangedListener");
                notifyDataSetChanged();
            }
        };

        App.getEManager().setEventListener(ActiveCall.InsertEvent.class, dataChangedListener, null);
        App.getEManager().setEventListener(ActiveCall.DeleteEvent.class, dataChangedListener, null);
    }

    public String getTitle() {
        return getContext().getString(R.string.calls).toUpperCase();
    }

    public void onPageSelected() {
        super.onPageSelected();
    }

    static final int OPT_MENU_CLEAR_ALL =0xFFCCFF;

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, OPT_MENU_CLEAR_ALL, 131, R.string.clear).setIcon(android.R.drawable.ic_menu_delete);
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case OPT_MENU_CLEAR_ALL:
                Dialogs.confirm(getContext(), R.string.delete_all_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TableClipboard.deleteAllRecords();
                        getAdapter().notifyDataSetChanged();
                    }
                }).show();
                return true;
        }
        return false;
    }

}
