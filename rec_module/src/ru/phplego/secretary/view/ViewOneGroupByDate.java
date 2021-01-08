package ru.phplego.secretary.view;

import android.content.Context;
import android.graphics.Color;
import ru.phplego.core.date.Date;
import ru.phplego.secretary.activities.pages.adapters.GroupExAdapter;
import ru.phplego.secretary.etc.Humanist;
import ru.phplego.rec_module.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 21:43
* To change this template use File | Settings | File Templates.
*/

public class ViewOneGroupByDate extends ViewOneGroup {

	public ViewOneGroupByDate(Context context, GroupExAdapter.Group group) {
		super(context, group);
        this.mGroup = group;
	}

    @Override
    public void fillViews(){
        super.fillViews();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        try{
            date = new Date(sdf.parse(this.mGroup.groupKey));
        }catch (ParseException e){

        }
        mTvTitle.setText(new SimpleDateFormat("d MMMMMMMMMMMMMMM").format(date));
        if(date.isToday())      mTvTitle.setText(R.string.today);
        if(date.isYesterday())  mTvTitle.setText(R.string.yesterday);
        if(date.isTomorrow())   mTvTitle.setText(R.string.tomorrow);

        mTvRightText.setText(Humanist.date("EEEEEEE", date));

        if(date.getDay() == 6 || date.getDay() == 0)
            mTvRightText.setTextColor(Color.parseColor("#FFBBBB"));
        else
            mTvRightText.setTextColor(Color.parseColor("#99BBFF"));

    }

}
