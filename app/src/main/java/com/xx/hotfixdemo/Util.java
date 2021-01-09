package com.xx.hotfixdemo;

import android.util.Log;

public class Util {
    private static final String TAG = "Util";

    public void test() {
//        Log.d(TAG, "test: 啦啦啦啦拉拉阿拉啦");
        throw new IllegalArgumentException();
    }
}
