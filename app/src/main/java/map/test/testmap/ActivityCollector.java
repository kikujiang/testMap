package map.test.testmap;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    static List<Activity> activities;

    public static void clearAllActivity() {
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

    public static void remove(Activity activity){
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
    }

    public static void addActivity(Activity activity){

        if (activities == null){
            activities = new ArrayList<>();
        }

        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }
}
