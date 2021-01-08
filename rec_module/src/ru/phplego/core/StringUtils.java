package ru.phplego.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 24.01.12
* Time: 22:11
* To change this template use File | Settings | File Templates.
*/

public class StringUtils {
	public static String join(Collection <String> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator iter = s.iterator();
		while (iter.hasNext()) {
				buffer.append(iter.next());
				if (iter.hasNext()) {
						buffer.append(delimiter);
				}
		}
		return buffer.toString();
	}

	public static String join(String [] s, String delimiter) {
		if(s.length == 0) return "";
		if(s.length == 1) return s[0];
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < s.length; i++){
				buffer.append(s[i]);
				if (i < s.length - 1) {
						buffer.append(delimiter);
				}
		}
		return buffer.toString();
	}

	public static <T> T [] concat(T [] first, T [] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

}
