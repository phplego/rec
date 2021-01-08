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

public class SmartPrefInt extends SmartPref {
	public SmartPrefInt(Class clazz) {
		super(clazz);
	}

	public SmartPrefInt(Object agregator, boolean prepend_agregator_class_name) {
		super(agregator, prepend_agregator_class_name);
	}

	public SmartPrefInt(Object agregator) {
		super(agregator);
	}

	public SmartPrefInt(String full_name) {
		super(full_name);
	}

	public void put(int value) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		p.edit().putInt(getName(), value).commit();
	}

	public int get(int default_value) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		return p.getInt(getName(), default_value);
	}

	public int get() {
		return get(0);
	}

}
