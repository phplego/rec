package ru.phplego.secretary;

import android.os.Build;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 11.06.12
* Time: 18:59
*/

public class Device {
	public static boolean isAndroid4OrHigher() {
		//return false;
		return Build.VERSION.SDK_INT >= 14;
	}

	public static boolean isSamsungGalaxyS2() {
		return Build.DEVICE.equals("GT-I9100");
	}

}
