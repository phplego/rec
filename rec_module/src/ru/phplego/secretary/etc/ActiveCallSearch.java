package ru.phplego.secretary.etc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import ru.phplego.core.db.ActiveQuery;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.pages.PageAbstractListExpandableActiveQuery;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.secretary.db.TableCalls;
import ru.phplego.core.debug.Log;
import java.util.Vector;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 01.05.12
* Time: 1:21
* To change this template use File | Settings | File Templates.
*/

public class ActiveCallSearch {
	public static void startSearch(final ActiveQuery activeQuery, final String text, final PageAbstractListExpandableActiveQuery page, final Runnable onComplete) {
		final ProgressDialog dialog = ProgressDialog.show(page.getContext(), "", App.getStr(R.string.loading), true);

		AsyncTask<String, String, Long> task = new AsyncTask<String, String, Long>(){

				@Override
				protected Long doInBackground(String... strings) {
						// Перед поиском обновим имена контактов
						ActiveQuery ac = new ActiveQuery();
						ac.from(new ActiveCall());
						ac.where(TableCalls.search + " IS NULL");
						Vector<ActiveCall> objects = ac.objects();
						Log.d("not contacts count=" + objects.size());
						for(ActiveCall one: objects){
								Log.d("cname="+one.get(TableCalls.search.toString()));
								String search = one.getContactDisplayName(App.getContext()).toLowerCase();
								search += " "+one.getNote().toLowerCase();
								one.set(TableCalls.search.toString(), search);
								one.update();
						}

						String textLower = text.toLowerCase();
						String whereStr = "note like ? or phone like ? or "+ TableCalls.search +" like ?";
						String [] whereArgs = new String[]{"%"+textLower+"%", "%"+textLower+"%", "%"+textLower+"%"};
						activeQuery.and(whereStr, whereArgs);
						return null;  //To change body of implemented methods use File | Settings | File Templates.
				}

				protected void onPostExecute(Long result) {
						onComplete.run();
						dialog.hide();
				}
		};


		task.execute("","","","");
	}

}
