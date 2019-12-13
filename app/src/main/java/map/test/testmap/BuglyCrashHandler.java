package map.test.testmap;

import android.util.Log;

public class BuglyCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "BuglyCrashHandler";


    private volatile static BuglyCrashHandler instance;

    private BuglyCrashHandler() {

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "uncaughtException called!  exit!");

        //关闭所有Activity
        ActivityCollector.clearAllActivity();
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }

    public static BuglyCrashHandler getInstance() {

        if (instance == null) {
            synchronized (BuglyCrashHandler.class) {
                if (instance == null) {
                    instance = new BuglyCrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(){
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}