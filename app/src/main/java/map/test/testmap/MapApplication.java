package map.test.testmap;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePalApplication;

public class MapApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        BuglyCrashHandler.getInstance().init();
        CrashReport.initCrashReport(getApplicationContext(), "171b14d1df", false);
    }

}
