package ru.phplego.core;

import java.io.*;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 12.01.12
* Time: 0:04
* To change this template use File | Settings | File Templates.
*/

public class Utils {
	public static void copyfile(String srFile, String dtFile) {
		try{
				File f1 = new File(srFile);
				File f2 = new File(dtFile);
				InputStream in = new FileInputStream(f1);

				//For Overwrite the file.
				OutputStream out = new FileOutputStream(f2);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0){
						out.write(buf, 0, len);
				}
				in.close();
				out.close();
		}
		catch(FileNotFoundException ex){
		}
		catch(IOException e){
		}
	}

	public static void sleep(long millis) {
		try{Thread.sleep(millis);}catch (Exception e){}
	}

}
