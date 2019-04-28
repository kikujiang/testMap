package map.test.testmap.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.util.Log;
import android.view.View;

import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import map.test.testmap.Constants;
import map.test.testmap.NaviMain;
import map.test.testmap.model.Notice;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import q.rorbin.badgeview.QBadgeView;

public class MessageUpdateWorker extends Worker {

    private static final String TAG = "worker123";

    public MessageUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "====================doWork: working=========================");
        getNotice();
        return Result.success();
    }

    private void getNotice(){
        HttpUtils.getInstance().getNotice(new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {
                if(responseMapBean == null){
                    Log.d(TAG, "服务器返回对象为空");
                    return;
                }

                ResponseBean<Notice> current = (ResponseBean<Notice>)responseMapBean.body();

                if(current.getResult() == Constants.RESULT_OK){

                    final List<Notice> noticeList = current.getList();
                    Log.d(TAG, "run: ");
                    if(noticeList.size() > 0){
                        int initial = Constants.messageId;
                        int count = 0;

                        for (Notice item:noticeList) {
                            int id = item.getId();

                            if(initial < id && initial != 0){
                                count++;
                            }

                            if(Constants.messageId < id){
                                Constants.messageId = id;
                            }
                        }

                        if(count > 0){
                            showBadgeView(2,count);
                        }

                    }

                    Log.d(TAG, "success: message max id is:" + Constants.messageId);
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail: "+e.getMessage());
            }
        });
    }

    /**
     * BottomNavigationView显示角标
     *
     * @param viewIndex tab索引
     * @param showNumber 显示的数字，小于等于0是将不显示
     */
    private void showBadgeView(int viewIndex, int showNumber) {
        // 具体child的查找和view的嵌套结构请在源码中查看
        // 从bottomNavigationView中获得BottomNavigationMenuView
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) NaviMain.navigation.getChildAt(0);
        // 从BottomNavigationMenuView中获得childview, BottomNavigationItemView
        // 获得viewIndex对应子tab
        View view = menuView.getChildAt(2);
        // 从子tab中获得其中显示图片的ImageView
        View icon = view.findViewById(android.support.design.R.id.icon);
        // 获得图标的宽度
        int iconWidth = icon.getWidth();
        // 获得tab的宽度/2
        int tabWidth = view.getWidth() / 2;
        // 计算badge要距离右边的距离
        int spaceWidth = tabWidth - iconWidth;

        // 显示badegeview
        if (NaviMain.badgeView == null){
            NaviMain.badgeView = new QBadgeView(getApplicationContext());
            NaviMain.badgeView.bindTarget(view);
        }
        NaviMain.badgeView.setBadgeNumber(showNumber);
        BadgeUtil.setBadgeCount(getApplicationContext(),showNumber);
    }
}
