package ru.phplego.core;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 18.07.12
* Time: 16:20
*/

public class Res {
	public static int getId(String packageName, String className, String name) {
		Class r = null;
		int id = 0;
		try {
				r = Class.forName(packageName + ".R");

				Class[] classes = r.getClasses();
				Class desireClass = null;

				for (int i = 0; i < classes.length; i++) {
						if(classes[i].getName().split("\\$")[1].equals(className)) {
								desireClass = classes[i];

								break;
						}
				}

				if(desireClass != null)
						id = desireClass.getField(name).getInt(desireClass);
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		} catch (IllegalArgumentException e) {
				e.printStackTrace();
		} catch (SecurityException e) {
				e.printStackTrace();
		} catch (IllegalAccessException e) {
				e.printStackTrace();
		} catch (NoSuchFieldException e) {
				e.printStackTrace();
		}

		return id;
	}

	public static int [] getIds(String packageName, String className, String name) {
		Class r = null;
		int[] ids = {};
		try {
				r = Class.forName(packageName + ".R");

				Class[] classes = r.getClasses();
				Class desireClass = null;

				for (int i = 0; i < classes.length; i++) {
						if(classes[i].getName().split("\\$")[1].equals(className)) {
								desireClass = classes[i];

								break;
						}
				}

				if(desireClass != null)
						ids = (int[])desireClass.getField(name).get(desireClass);
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		} catch (IllegalArgumentException e) {
				e.printStackTrace();
		} catch (SecurityException e) {
				e.printStackTrace();
		} catch (IllegalAccessException e) {
				e.printStackTrace();
		} catch (NoSuchFieldException e) {
				e.printStackTrace();
		}

		return ids;
	}

}
