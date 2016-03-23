package com.magic.card.search.commons.log;

import com.crashlytics.android.Crashlytics;

public class Log {

    private Class caller;

    public Log(Object caller) {
        this.caller = caller.getClass();
    }

    public void d(String msg) {
        android.util.Log.d(caller.getCanonicalName(), msg);
    }

    public void i(String msg) {
        android.util.Log.i(caller.getCanonicalName(), msg);
    }

    public void w(String msg) {
        android.util.Log.w(caller.getCanonicalName(), msg);
    }

    public void e(String msg, Throwable t) {
        Crashlytics.logException(t);
        android.util.Log.e(caller.getCanonicalName(), msg, t);
    }

    public static void error(String msg, Throwable t, Class<?> callerClass) {
        Crashlytics.logException(t);
        android.util.Log.e(callerClass.getCanonicalName(), msg, t);
    }
}
