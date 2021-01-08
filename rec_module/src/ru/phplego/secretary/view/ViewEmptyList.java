package ru.phplego.secretary.view;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityHelp;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 01.05.12
* Time: 2:31
* To change this template use File | Settings | File Templates.
*/

public class ViewEmptyList extends RelativeLayout {
	public ViewEmptyList(Context context) {
		super(context);

		// Блок "Список пуст"
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		setLayoutParams(lp);
		setGravity(Gravity.CENTER);

		LayoutInflater li = LayoutInflater.from(getContext());
		View emptyView = li.inflate(R.layout.calls_empty_list, null);
		this.setVisibility(View.INVISIBLE);
		this.addView(emptyView, new LinearLayout.LayoutParams(App.dip(150), -2));

		// Кнопка "Позвонить" если список разговоров еще пуст
		Button btnShowContacts = (Button) emptyView.findViewById(R.id.show_contacts);
		btnShowContacts.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
						Intent intent = new Intent(Intent.ACTION_DEFAULT);
						intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
						getContext().startActivity(intent);

				}
		});


		// Кнопка "Помощь" если список разговоров еще пуст
		Button btnHelp = (Button) emptyView.findViewById(R.id.btn_help);
		btnHelp.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
						Intent intent = new Intent(getContext(), ActivityHelp.class);
						getContext().startActivity(intent);
				}
		});
	}

}
