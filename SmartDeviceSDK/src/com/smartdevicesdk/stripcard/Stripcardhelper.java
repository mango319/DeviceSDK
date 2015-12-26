package com.smartdevicesdk.stripcard;

public class Stripcardhelper {
	
	public static native byte[] ReadCard();
	
	public static native String ReadCardStr();
	
	static {
        System.loadLibrary("strip-card");
    }
}
