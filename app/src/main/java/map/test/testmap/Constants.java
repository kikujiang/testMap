package map.test.testmap;

import androidx.work.impl.model.WorkTypeConverters;

import java.util.List;

import map.test.testmap.model.DataType;

public class Constants {

    public static int messageId = 0;
    public static int noticeCount = 0;
    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 2;
    public static final int REQUEST_CODE = 1000;
    public static final int REQUEST_LINE_CODE = 1001;
    public static final int TYPE_HISTORY_POINT = 10010;
    public static final int TYPE_HISTORY_LINE = 10011;

    public static final String POINT_FLAG = "MainFragmentPoint";
    public static final String LINE_FLAG = "MainFragmentLine";

    public static List<DataType> pointTypeList = null;
    public static List<DataType> pointTerminalTypeList = null;
    public static List<DataType> pointLineTypeList = null;
    public static List<DataType> pointDeviceTypeList = null;

    public static final int TYPE_TASK_PUBLISH = 100001;
    public static final int TYPE_TASK_TO_BE_FINISH = 100002;
    public static final int TYPE_TASK_TO_BE_VERIFY = 100003;
    public static final int TYPE_TASK_COMPLETED = 100004;

    public static String channelId = "";
    public static boolean isLogin = false;
    public static int userId = 0;
    public static String userName = "";
    public static String loginTag = "";
//    public static final String WEB_URL = "http://172.19.26.52:8080/em";
//    public static final String WEB_URL = "http://172.18.110.57:8080";
//    public static final String WEB_URL = "http://172.18.110.57:8080";
//    public static final String WEB_URL1 = "http://172.18.110.57:8080/em";
//    public static final String TEST_SPLITTER = "/em";
//    public static final String TEST_SPLITTER = "";
//    public static final String WEB_URL = "http://172.17.4.5:8080";
//    public static final String WEB_URL = "http://172.18.110.65:8080";
//    public static final String WEB_URL = "http://10.206.2.164:8081";
    //正式环境
    public static final String WEB_URL = "http://119.3.71.206";
//    private static final String SPLITTER = "/app/";
    private static final String SPLITTER= "/app/";
    private static final String SPLITTER1= "";

    public static boolean isExistAlready = false;

    public static int currentIndex = 0;

    /**
     * 登陆
     */
    public static final String TAG_UPDATE = SPLITTER1+SPLITTER + "version";
    /**
     * 登陆
     */
    public static final String TAG_LOGIN = SPLITTER1+SPLITTER + "loginClient";
    /**
     * 登陆
     */
    public static final String TAG_LOGIN_QRCODE = SPLITTER1+SPLITTER + "login";
    /**
     * 登陆
     */
    public static final String TAG_LOGIN_QRCODE_CONFIRM = SPLITTER1+SPLITTER + "loginOK";
    /**
     * 登出
     */
    public static final String TAG_LOGOUT = SPLITTER1+SPLITTER + "logoutClient";
    /**
     * 获取用户信息
     */
    public static final String TAG_USER_INFO = SPLITTER1+"/userAction"+SPLITTER + "getUserInfo";
    /**
     * 获取用户信息
     */
    public static final String TAG_MANAGER_LIST= SPLITTER1+"/userAction"+SPLITTER + "getAllUser";
    /**
     * 获取用户权限
     */
    public static final String TAG_USER_PERMISSION = SPLITTER1+"/userAction"+SPLITTER + "getPermission";
    /**
     * 获取标记点类型列表
     */
    public static final String TAG_GET_POINT_TYPE = SPLITTER1+"/tagAction"+SPLITTER+"getTagTypeList";
    /**
     * 获取变电所点类型列表
     */
    public static final String TAG_GET_CONSTANT_TYPE = SPLITTER1+"/tagAction"+SPLITTER+"getConstantTypeList";
    /**
     * 获取标记点终端类型列表
     */
    public static final String TAG_GET_POINT_TERMINAL_TYPE = SPLITTER1+"/tagAction"+SPLITTER+"getTagCeTypeList";
    /**
     * 获取标记点光缆类型列表
     */
    public static final String TAG_GET_POINT_LINE_TYPE = SPLITTER1+"/tagAction"+SPLITTER+"getTagLeTypeList";
    /**
     * 获取线路类型列表
     */
    public static final String TAG_GET_LINE_TYPE = SPLITTER1+"/tagAction"+SPLITTER+"getTagLineTypeList";

    /**
     * 获取设备类型列表
     */
    public static final String TAG_GET_DEVICE_TYPE = SPLITTER1+"/tagAction"+SPLITTER+"getTagMTypeList";
    /**
     * 获取所有标记点
     */
    public static final String TAG_GET_ALL_POINT = SPLITTER1+"/tagAction"+SPLITTER+"getAllTag";
    /**
     * 获取当前账户可查看标记点
     */
    public static final String TAG_GET_USER_POINT = SPLITTER1+"/tagAction"+SPLITTER+"getAllTagPermission";
    /**
     * 获取某个标记点
     */
    public static final String TAG_GET_SINGLE_POINT =SPLITTER1+"/tagAction"+SPLITTER+"getTag";
    /**
     *保存某个标记点
     */
    public static final String TAG_SAVE_SINGLE_POINT =SPLITTER1+"/tagAction"+SPLITTER+"saveTag";
    /**
     * 删除某个标记点
     */
    public static final String TAG_DELETE_SINGLE_POINT = SPLITTER1+"/tagAction"+SPLITTER+"delTag";
    /**
     * 获取所有线路
     */
    public static final String TAG_GET_ALL_LINE =SPLITTER1+"/tagAction"+SPLITTER+"getAllTagLine";
    /**
     * 获取当前账户可查看线路
     */
    public static final String TAG_GET_USER_LINE = SPLITTER1+"/tagAction"+SPLITTER+"getAllTagLinePermission";
    /**
     * 获取某条线路
     */
    public static final String TAG_GET_SINGLE_LINE =SPLITTER1+"/tagAction"+SPLITTER+"getTagLine";
    /**
     * 保存某条线路
     */
    public static final String TAG_SAVE_SINGLE_LINE = SPLITTER1+"/tagAction"+SPLITTER+"saveTagLine";
    /**
     * 添加辅助点
     */
    public static final String TAG_SAVE_ASSIST_POINT =SPLITTER1+"/tagAction"+SPLITTER+"addTagLineAssist";
    /**
     * 删除某条线路
     */
    public static final String TAG_DELETE_SINGLE_LINE =SPLITTER1+ "/tagAction"+SPLITTER+"delTagLine";
    /**
     * 获取维修点历史记录
     */
    public static final String TAG_GET_CHECK_HISTORY =SPLITTER1+"/tagAction"+SPLITTER+"getTagCheckHistory";
    /**
     * 获取维修点修改记录
     */
    public static final String TAG_GET_POINT_HISTORY =SPLITTER1+"/tagAction"+SPLITTER+"getTagChangeHistory";
    /**
     * 添加维修点历史记录
     */
    public static final String TAG_SAVE_CHECK =SPLITTER1+"/tagAction"+SPLITTER+"saveTagCheck";
    /**
     * 添加维修点
     */
    public static final String TAG_SAVE_LINE_CHECK =SPLITTER1+"/tagAction"+SPLITTER+"addTagLineCheck";
    /**
     * 获取维修点历史记录
     */
    public static final String TAG_GET_CHECK_LINE_HISTORY =SPLITTER1+"/tagAction"+SPLITTER+"getTagLineCheckHistory";
    /**
     * 获取维修点修改记录
     */
    public static final String TAG_GET_LINE_HISTORY =SPLITTER1+"/tagAction"+SPLITTER+"getTagLineChangeHistory";

    /**
     * 获取维修点历史记录
     */
    public static final String TAG_GET_NOTICE =SPLITTER1+"/noticeAction"+SPLITTER+"getLatestNotice";

    /**
     * 获取任务列表
     */
    public static final String TAG_GET_TASK_LIST =SPLITTER1+"/tagAction"+SPLITTER+"getCheckTaskList";

    /**
     * 获取下一步人员列表
     */
    public static final String TAG_GET_NEXT_STEP_LIST =SPLITTER1+"/tagAction"+SPLITTER+"getTagCheckUserList";

    /**
     * 获取任务详情
     */
    public static final String TAG_GET_CHECK_TASK =SPLITTER1+"/tagAction"+SPLITTER+"/getCheckTask";

    /**
     * 编辑任务详情
     * 	/tagAction/app/editTagCheck
     */
    public static final String TAG_EDIT_TAG_CHECK =SPLITTER1+"/tagAction"+SPLITTER+"/editTagCheck";

    /**
     * 任务打回
     * 	/tagAction/app/putBackTask
     */
    public static final String TAG_PUT_BACK_TASK =SPLITTER1+"/tagAction"+SPLITTER+"/putBackTask";

    /**
     * 提交消息状态
     */
    public static final String TAG_SET_NOTICE ="/noticeAction"+SPLITTER+"/getNotice";

    /**
     * 获取任务信息
     */
    public static final String TAG_GET_TASK_INFO ="/tagAction"+SPLITTER+"/getCheckTaskGenData";

    /**
     * 获取微信验证码
     */
    public static final String TAG_GET_WX_CODE ="/wxValidateCode";

    /**
     *获取微信二维码地址
     */
    public static final String TAG_GET_WX_CODE_IMG = "/wxqrcode";

}