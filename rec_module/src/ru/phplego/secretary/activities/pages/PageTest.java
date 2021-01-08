package ru.phplego.secretary.activities.pages;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.activities.ActivityReorderPages;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.db.TableCalls;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageTest extends Page {
	public PageTest(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();
		ScrollView scrollView = new ScrollView(getContext());
		LinearLayout layout = new LinearLayout(getContext());
		scrollView.addView(layout);

		layout.setOrientation(LinearLayout.VERTICAL);

		{
				Button b    = new Button(getContext());
				final TextView tv = new TextView(getContext());
				layout.addView(b);
				layout.addView(tv);
				b.setText("Insert ActiveCall");
				b.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
								long start = System.currentTimeMillis();
								ActiveCall ac = new ActiveCall();
								ac.set(TableCalls.created.toString(), start);
								ac.setNote("Test");
								ac.insert();
								long end = System.currentTimeMillis();
								tv.append("\nTime:"+(end - start));
								tv.append("\nId:"+ac.getId());
						}
				});
		}

		{
				Button b    = new Button(getContext());
				final TextView tv = new TextView(getContext());
				layout.addView(b);
				layout.addView(tv);
				b.setText("Reorder Pages");
				b.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
								Intent i = new Intent(getContext(), ActivityReorderPages.class);
								getContext().startActivity(i);
						}
				});
		}

		setContentView(scrollView);
	}

	public String getTitle() {
		return "Developer".toUpperCase();
	}

	public void onPageSelected() {
	}

	public void onPageOut() {
	}

}
