package ru.phplego.secretary.activities.pages.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.core.Cachable;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.core.debug.Log;
import ru.phplego.secretary.view.ViewGroupFactory;
import ru.phplego.secretary.view.ViewOneGroup;
import ru.phplego.secretary.view.ViewOneRecord;
import ru.phplego.secretary.view.ViewOneRecord2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 06.01.12
 * Time: 9:15
 * To change this template use File | Settings | File Templates.
 */

public class GroupExAdapter extends BaseExpandableListAdapter {
    private Context mContext;

    private ActiveQuery <ActiveCall> mActiveQuery;

    private Grouper mGrouper;

    private LinkedHashMap <String, Group> mGroupedData;

    private ViewGroupFactory mViewGroupFactory;

    public class Group extends Vector <ActiveCall> {
        public String groupKey;

    }
    public interface Grouper {
        public String getGroupKey(ActiveCall activeCall);

    }
    public GroupExAdapter(Context context, ActiveQuery <ActiveCall> activeQuery1, Grouper grouper, ViewGroupFactory groupFactory) {
        mContext        = context;
        mActiveQuery    = activeQuery1;
        mGrouper        = grouper;
        mGroupedData    = new LinkedHashMap<String, Group>();
        mViewGroupFactory = groupFactory;

        if(mGrouper == null) mGrouper = new Grouper() {
            @Override
            public String getGroupKey(ActiveCall activeCall) {
                Date date = activeCall.getCreatedDate();
                return new SimpleDateFormat("dd.MM.yyyy").format(date);
            }
        };

        requery();
    }

    public ActiveQuery getActiveQuery() {
        return mActiveQuery;
    }

    public void requery() {
        Log.d("requery()");
        mGroupedData.clear();
        Vector<ActiveCall> calls = mActiveQuery.objects();
        for(ActiveCall activeCall: calls){
            Group group = new Group();
            group.groupKey = mGrouper.getGroupKey(activeCall);
            if(!mGroupedData.containsKey(group.groupKey))
                mGroupedData.put(group.groupKey, group);
            mGroupedData.get(group.groupKey).add(activeCall);
        }
    }

    public Group [] getGroupedData() {
        return mGroupedData.values().toArray(new Group[0]);
    }

    /**
     * @param groupPosition
     * @param childPosition
     * @return идентификатор записи разговора
     */
    public long getChildId(int groupPosition, int childPosition) {
        return getGroupedData()[groupPosition].get(childPosition).getId();
    }

    public long getGroupId(int groupPosition) {
        return -groupPosition;
    }

    public Object getGroup(int position) {
        return position;
    }

    public ActiveCall getChild(int groupPosition, int childPosition) {
        return getGroupedData()[groupPosition].get(childPosition);
    }

    public int getGroupCount() {
        return mGroupedData.size();
    }

    public int getChildrenCount(int groupPosition) {
        return getCallGroup(groupPosition).size();
    }

    public Group getCallGroup(int groupPosition) {
        if(groupPosition > getGroupedData().length - 1 || groupPosition < 0) return new Group();
        return getGroupedData()[groupPosition];
    }

    private Hashtable <String, View> _group_view_cache =new Hashtable<String, View>();

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Если вылезли за пределы массива
        if(mGroupedData.size() <= groupPosition) return new TextView(mContext);

        String groupKey = getGroupedData()[groupPosition].groupKey;

        if(_group_view_cache.containsKey(groupKey)){
            View view = _group_view_cache.get(groupKey);
            if(view instanceof Cachable && !((Cachable) view).isInvalid()){
                return view;
            }
        }

        /*
        // Пробовал повторно использовать convertedView, как рекоммендует Гугл - тормозит жутко
        if(convertView instanceof ViewOneGroup){
            ViewOneGroup ooo = (ViewOneGroup)convertView;
            ooo.fillViewsFromGroup(getGroupedData()[groupPosition]);
            return ooo;
        }
        */

        View view = mViewGroupFactory.createView(mContext, getGroupedData()[groupPosition]);
        ((ViewOneGroup)view).fillViews();

        _group_view_cache.put(groupKey, view);

        return view;
    }

    private Hashtable <Long, View> _child_view_cache = new Hashtable<Long, View>();

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        if(convertView instanceof ViewOneRecord){
            ViewOneRecord ooo = (ViewOneRecord)convertView;
            ooo.fillFromActiveCall(getChild(groupPosition,childPosition));
            return ooo;
        }

        /*
        if(convertView instanceof ViewOneRecord2){
            ViewOneRecord2 ooo = (ViewOneRecord2)convertView;
            ooo.fillFromActiveCall(getChild(groupPosition,childPosition));
            return ooo;
        }
        */

        /*
        long id = getChildId(groupPosition, childPosition);
        if(_child_view_cache.containsKey(id)){
            View view = _child_view_cache.get(id);
            if(view instanceof Cachable && !((Cachable) view).isInvalid()){
                return view;
            }
        }
        */


        ViewOneRecord viewRecord = new ViewOneRecord(mContext, getChild(groupPosition,childPosition));
        //ViewOneRecord2 viewRecord = new ViewOneRecord2(mContext, getChild(groupPosition,childPosition));

        //_child_view_cache.put(id, viewRecord);

        return viewRecord;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void clearViewCache() {
        _child_view_cache.clear();
    }

}
