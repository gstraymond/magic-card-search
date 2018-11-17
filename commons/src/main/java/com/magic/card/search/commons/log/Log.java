package com.magic.card.search.commons.log;

public class Log {

    private Class caller;

    public Log(Object caller) {
        this.caller = caller.getClass();
    }

    public Log(Class<?> class_) {
        this.caller = class_;
    }

    public void d(String msg) {
        android.util.Log.d(caller.getCanonicalName(), msg);
    }

    public void d(String msg, Object... args) {
        d(String.format(msg, args));
    }

    public void i(String msg) {
        android.util.Log.i(caller.getCanonicalName(), msg);
    }

    public void i(String msg, Object... args) {
        i(String.format(msg, args));
    }

    public void w(String msg) {
        android.util.Log.w(caller.getCanonicalName(), msg);
    }

    public void w(String msg, Object... args) {
        w(String.format(msg, args));
    }

    public void e(String msg, Throwable t) {
        android.util.Log.e(caller.getCanonicalName(), msg, t);
    }

    public static void error(String msg, Throwable t, Class<?> callerClass) {
        android.util.Log.e(callerClass.getCanonicalName(), msg, t);
    }
}
