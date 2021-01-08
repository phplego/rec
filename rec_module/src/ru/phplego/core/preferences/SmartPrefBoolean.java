package ru.phplego.core.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.phplego.core.Application;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 08.05.12
* Time: 19:58
* To change this template use File | Settings | File Templates.
*/

public class SmartPrefBoolean extends SmartPref {
	public SmartPrefBoolean(Class clazz) {
		super(clazz);
	}

	public SmartPrefBoolean(Object agregator, boolean prepend_agregator_class_name) {
		super(agregator, prepend_agregator_class_name);
	}

	public SmartPrefBoolean(Object agregator) {
		super(agregator);
	}

	public SmartPrefBoolean(String full_name) {
		super(full_name);
	}

	public void put(Boolean value) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		p.edit().putBoolean(getName(), value).commit();
	}

	public boolean get(boolean default_value) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		return p.getBoolean(getName(), default_value);
	}

	public boolean get() {
		return get(false);
	}

}
