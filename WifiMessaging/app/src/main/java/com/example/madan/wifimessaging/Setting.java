package com.example.madan.wifimessaging;

/**
 * Created by madan on 1/5/17.
 */

public class Setting {
    public static final int SERVER_PORT = 45678;
    public static final String SHARED_PERF_NAME = "setting_pref";
    public static final String KEY_FOR_USER_NAME = "user_name";

    public static String USER_NAME;
    public static String GROUP_NAME;
    public static int USER_ID;

    public static RGB rgb = new RGB(-1, -1, -1);

    public static class RGB {
        public int r;
        public int g;
        public int b;

        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
