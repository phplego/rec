package ru.phplego.core.preferences;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Vector;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 08.05.12
* Time: 19:37
* To change this template use File | Settings | File Templates.
*/

public class SmartPref {
	private Object mArgegator;

	private String mName;

	private boolean mPrependAgregatorClassName =true;

	/**
	*
	* @param agregator объект или класс, который содержит эту настройку
	*/
	public SmartPref(Object agregator) {
		this(agregator, true);
	}

	public SmartPref(Object agregator, boolean prepend_agregator_class_name) {
		mArgegator = agregator;
		mPrependAgregatorClassName = prepend_agregator_class_name;
	}

	/**
	* Если имя настройки задано явно и отличается от имени переменной класса
	* @param full_name
	*/
	public SmartPref(String full_name) {
		mName = full_name;
		mPrependAgregatorClassName = false;
	}

	public String getName() {
		if(mName != null){
				return mName;
		}
		Class clazz = null;
		if(mArgegator instanceof Class)
				clazz = (Class)mArgegator;
		else
				clazz = mArgegator.getClass();

		Field[] fields = getAllFields(clazz);
		//Log.d(clazz.getSimpleName()+" fields count: "+fields.length);
		for(Field f: fields){
				try{
						if(!f.getType().equals(SmartPref.class) && !f.getType().equals(SmartPrefBoolean.class) && !f.getType().equals(SmartPrefInt.class) && !f.getType().equals(SmartPrefString.class)){
								continue;
						}
						boolean ourType = f.get(mArgegator) instanceof SmartPref;
						if(!ourType) continue;
						if(f.get(mArgegator).equals(this)){
								if(mPrependAgregatorClassName)
										mName = clazz.getSimpleName()+"."+f.getName();
								else
										mName = f.getName();
								return mName;
						}
				}catch (IllegalAccessException e) {
						throw new Error("Field "+f.getDeclaringClass().getSimpleName()+"."+f.getName()+" must be public");
				}
		}
		return "";
	}

	private Field [] getAllFields(Class clazz) {
		Vector<Field> fields = new Vector<Field>();
		Collections.addAll(fields, clazz.getDeclaredFields());
		Class superClass = clazz;
		while((superClass = superClass.getSuperclass()) != null){
				//Log.d("+++++++++"+superClass.getSimpleName()+" fcnt: "+superClass.getDeclaredFields().length);
				Collections.addAll(fields, superClass.getDeclaredFields());
		}
		return fields.toArray(new Field[0]);
	}

}
