package map.test.testmap;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.internal.BottomNavigationMenuView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import map.test.testmap.model.Notice;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.BadgeUtil;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.PreferencesUtils;
import q.rorbin.badgeview.QBadgeView;

public class CustomReceiver extends XGPushBaseReceiver {
    public static final String LogTag = "CustomReceiver";

//    public static int MsgCount = 0;

    private Context mContext;

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        this.mContext = context;
//        MsgCount++;
//        BadgeUtil.setBadgeCount(context,MsgCount);
        getNotice();
        Log.d(LogTag, "+++++++++++++++++++++++++++++展示通知的回调");
    }

    private void getNotice(){
        long time = PreferencesUtils.getLong(mContext,String.valueOf(Constants.userId)+"123456",0L);
        HttpUtils.getInstance().getNotice(time,new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {
                if(responseMapBean == null){
                    Log.d(LogTag, "服务器返回对象为空");
                    return;
                }

                ResponseBean<Notice> current = (ResponseBean<Notice>)responseMapBean.body();

                if(current.getResult() == Constants.RESULT_OK){
                    long currentTime = current.getTime();
                    PreferencesUtils.putLong(mContext,String.valueOf(Constants.userId)+"123456",currentTime);
                    final List<Notice> noticeList = current.getList();
                    Log.d(LogTag, "run: ");
                    if(noticeList.size() > 0){
                        int initial = Constants.messageId;
                        int count = 0;

                        for (Notice item:noticeList) {
                            int id = item.getId();

                            if(initial < id && initial != 0){
                                count++;
                            }
                        }

                        if(count > 0){
                            Constants.noticeCount = count;
                            showBadgeView(2,count);
                        }

                    }

                    Log.d(LogTag, "success: message max id is:" + Constants.messageId);
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(LogTag, "fail: "+e.getMessage());
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
            NaviMain.badgeView = new QBadgeView(this.mContext);
            NaviMain.badgeView.bindTarget(view);
        }
        NaviMain.badgeView.setBadgeNumber(showNumber);
        BadgeUtil.setBadgeCount(this.mContext,showNumber);
    }

    //反注册的回调
    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        Log.d(LogTag, text);

    }

    //设置tag的回调
    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);

    }

    //删除tag的回调
    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(LogTag, text);

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击。此处不能做点击消息跳转，详细方法请参照官网的Android常见问题文档
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        Log.e(LogTag, "+++++++++++++++ 通知被点击 跳转到指定页面。");
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;

        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }
                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        Log.d(LogTag, text);
    }

    //注册的回调
    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
            // 在这里拿token
            String token = message.getToken();
        } else {
            text = message + "注册失败错误码：" + errorCode;
        }
        Log.d(LogTag, text);
//        show(context, text);
    }

    // 消息透传的回调
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        // TODO Auto-generated method stub
        String text = "收到消息:" + message.toString();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        String title = message.getTitle();
        Log.d(LogTag, "onTextMessage: text:"+text+",and title is:" + title);
        if(title != null && title.length() != 0 && title.equals("强制退出")){
            Log.d(LogTag, "onTextMessage: 1111");

            PreferencesUtils.putString(context,"account",null);
            PreferencesUtils.putString(context,"password_selector",null);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);

//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//            builder.setTitle("请注意");
//            builder.setMessage("您的账号在其他地方登录，请重新登录！");
//            builder.setCancelable(false);
//            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//
//            AlertDialog dialog = builder.create();
//
////        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);     //无效 崩溃
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
//            dialog.show();
            Log.d(LogTag, "onTextMessage: 2222");
        }

        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    Log.d(LogTag, "get custom value:" + value);
                }

                // ...
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("LC", "++++++++++++++++透传消息");
        // APP自主处理消息的过程...
        Log.d(LogTag, text);
//        show(context, text);
    }
}