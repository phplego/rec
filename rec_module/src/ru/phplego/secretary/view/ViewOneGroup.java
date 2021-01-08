package ru.phplego.secretary.view;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import ru.phplego.core.Cachable;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.App;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.rec_module.R;
import ru.phplego.core.pages.ContextMenuProvider;
import static android.view.Menu.NONE;

/**
 * Created with IntelliJ IDEA.
 * User: Oleg
 * Date: 21.04.12
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */

public class ViewOneGroup extends LinearLayout implements ContextMenuProvider, Cachable {
    protected TextView mTvTitle;

    protected TextView mTvRightText;

    protected TextView mTvCount;

    private Context mContext;

    protected GroupExAdapter.Group mGroup;

    protected boolean mIsInvalid;

    private static final int CONTEXT_GROUP_EXPAND_ALL =1;

    private static final int CONTEXT_GROUP_COLLAPSE_ALL =2;

    private static final int CONTEXT_GROUP_DELETE =3;

    public ViewOneGroup(Context context, GroupExAdapter.Group group) {
        super(context);
        mContext = context;
        mGroup = group;

        setLayoutParams(new ListView.LayoutParams(-1, App.dip(45)));
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(0, App.dip(5), 0, App.dip(5));


        mTvTitle = new TextView(mContext);
        mTvTitle.setLayoutParams(new LayoutParams(-2, -2));
        mTvTitle.setPadding(App.dip(40), 0, 0, 0);
        mTvTitle.setTextSize(22);

        //mTvTitle.setTextColor(Color.parseColor("#0099DB")); Голубой
        addView(mTvTitle);

        mTvRightText = new TextView(mContext);
        mTvRightText.setLayoutParams(new LayoutParams(-2, -2, 2));
        mTvRightText.setGravity(Gravity.RIGHT);
        mTvRightText.setPadding(0, 0, App.dip(5), 0);
        mTvRightText.setTextColor(Color.parseColor("#99BBFF"));
        addView(mTvRightText);

        mTvCount = new TextView(mContext);
        mTvCount.setLayoutParams(new LayoutParams(App.dip(30), -2));
        mTvCount.setGravity(Gravity.CENTER);
        mTvCount.setTextColor(Color.parseColor("#555555"));
        addView(mTvCount);


    }

    public void fillViews(){

        mTvCount.setText("" + mGroup.size());
    }

    public void fillViewsFromGroup(GroupExAdapter.Group group){
        mGroup = group;
        fillViews();
    }


    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        menu.setHeaderTitle(R.string.group_operations);
        menu.add(CONTEXT_GROUP_EXPAND_ALL,   NONE, NONE, R.string.expand_all);
        menu.add(CONTEXT_GROUP_COLLAPSE_ALL, NONE, NONE, R.string.collapse_all);
        menu.add(CONTEXT_GROUP_DELETE,       NONE, NONE, R.string.delete);
    }

    public boolean onContextItemSelected(MenuItem menuItem, View view) {
        switch(menuItem.getGroupId()){
            case CONTEXT_GROUP_EXPAND_ALL:
                ExpandableListView elw = (ExpandableListView) getParent();
                for(int i = 0; i < elw.getExpandableListAdapter().getGroupCount(); i++)
                    elw.expandGroup(i);
                break;
            case CONTEXT_GROUP_COLLAPSE_ALL:
                ExpandableListView elw2 = (ExpandableListView) getParent();
                for(int i = 0; i < elw2.getExpandableListAdapter().getGroupCount(); i++)
                    elw2.collapseGroup(i);
                break;
            case CONTEXT_GROUP_DELETE:
                removeAllViews();
                for(ActiveCall one: mGroup) one.delete(false); //Удаляем без поднятия события
                App.getEManager().riseEvent(new ActiveCall.DeleteEvent(), null); // Одно событие для всех
                //setNeedReload();
                break;
        }
        return true;
    }

    public boolean isInvalid() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
