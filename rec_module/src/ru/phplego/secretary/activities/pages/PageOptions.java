package ru.phplego.secretary.activities.pages;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import ru.phplego.core.pages.ActivityPagerAbstract;
import ru.phplego.core.pages.Page;
import ru.phplego.secretary.Prefs;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.activities.ActivityPrefs;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 28.04.12
* Time: 20:43
* To change this template use File | Settings | File Templates.
*/

public class PageOptions extends Page {
	public PageOptions(ActivityPagerAbstract context) {
		super(context);
	}

	public void onCreate() {
		super.onCreate();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		LinearLayout viewGroup = (LinearLayout)inflater.inflate(R.layout.page_options, null, false);
		setContentView(viewGroup);

		final int dark_theme = android.R.style.Theme_Black_NoTitleBar;
		final int light_theme = android.R.style.Theme_Light_NoTitleBar;
		final int current_theme = Prefs.theme.get();

		ImageButton btnLights = (ImageButton) viewGroup.findViewById(R.id.btn_lights);
		if(Prefs.theme.get() == light_theme)
				btnLights.setColorFilter(Color.parseColor("#FFFF00"));
		btnLights.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						Prefs.theme.put(current_theme == light_theme ? dark_theme : light_theme);
						//Prefs.set(Prefs.THEME, current_theme == light_theme ? dark_theme : light_theme);
						getContext().restart(PageOptions.class);
				}
		});

		ImageButton btnOptions = (ImageButton) viewGroup.findViewById(R.id.btn_options);
		btnOptions.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						Intent intent = new Intent(getContext(), ActivityPrefs.class);
						getContext().startActivity(intent);
				}
		});
	}

	public String getTitle() {
		return getContext().getString(R.string.options).toUpperCase();
	}

	public boolean isEnabledByDefault() {
		return false; // Невидим по умолчанию
	}

}
