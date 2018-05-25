package com.abysmal.slae;

/** Class for getting the current version of SLAE */
public class Version {

	public static final int MAJOR = 0, MINOR = 0, SUPER_MINOR = 3, BUILD = 0;
	public static final boolean SNAPSHOT = false;

	public static String getVersion() {
		return MAJOR + "." + MINOR + "." + SUPER_MINOR + (SNAPSHOT ? " SNAPSHOT" : "");
	}

	public static int getOpenGLMajor() {
		return 4;
	}

	public static int getOpenGLMinor() {
		return 5;
	}

}
