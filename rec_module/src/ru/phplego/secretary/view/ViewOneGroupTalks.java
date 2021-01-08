package ru.phplego.secretary.view;

import android.content.Context;
import ru.phplego.secretary.App;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.rec_module.R;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 21:27
* To change this template use File | Settings | File Templates.
*/

public class ViewOneGroupTalks extends ViewOneGroup {
    private Context mContext;
	public ViewOneGroupTalks(Context context, GroupExAdapter.Group group) {
		super(context, group);
        mContext = context;
        mGroup = group;

	}

    @Override
    public void fillViews(){
        super.fillViews();
        mTvTitle.setTextColor(App.getRes().getColor(R.color.my_tabs_bg));
        mTvTitle.setText(mGroup.get(0).getContactDisplayName(mContext));

    }

}
