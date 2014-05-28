package com.dhara.googlecalendartrial.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Utilities {
	public static String getMessage(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    e.printStackTrace(ps);
	    ps.close();
	    return baos.toString();
	}
}
