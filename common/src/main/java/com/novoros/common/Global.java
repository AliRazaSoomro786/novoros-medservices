package com.novoros.common;

public class Global {
    public static Schedule schedule = new Schedule();

    public static String split(int position, String string) {
        return string.split("-")[position];
    }
}
