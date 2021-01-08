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

public class SmartPrefString extends SmartPref {
	public SmartPrefString(Class clazz) {
		super(clazz);
	}

	public SmartPrefString(Object agregator, boolean prepend_agregator_class_name) {
		super(agregator, prepend_agregator_class_name);
	}

	public SmartPrefString(Object agregator) {
		super(agregator);
	}

	public SmartPrefString(String full_name) {
		super(full_name);
	}

	public void put(String value) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		p.edit().putString(getName(), value).commit();
	}

	public String get(String default_value) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(Application.getContext());
		return p.getString(getName(), default_value);
	}

	public int getInt(int default_value) {
		try{
				String str_value = this.get(""+default_value);
				return Integer.parseInt(str_value);
		}catch (Exception e){

		}
		return default_value;
	}

	public String get() {
		return get("");
	}

}
