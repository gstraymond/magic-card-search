package fr.gstraymond.tools;

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

    public void e(String msg, Exception e) {
        android.util.Log.e(caller.getCanonicalName(), msg, e);
    }
}
