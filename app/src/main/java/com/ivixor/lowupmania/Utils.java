package com.ivixor.lowupmania;


public class Utils {
    public static void pause(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
