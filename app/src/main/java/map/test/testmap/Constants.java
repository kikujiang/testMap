package map.test.testmap;

public class Constants {

    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 2;
    public static final int REQUEST_CODE = 1000;
    public static final int REQUEST_LINE_CODE = 1001;
    public static final int TYPE_HISTORY_POINT = 10010;
    public static final int TYPE_HISTORY_LINE = 10011;
    public static String channelId = "";
    public static boolean isLogin = false;
    public static int userId = 0;
//    public static final String WEB_URL = "http://172.19.26.52:8080/em";
//    public static final String TEST_SPLITTER = "em";
//    public static final String TEST_SPLITTER = "";
//    public static final String WEB_URL = "http://172.17.4.5:8080";
//    public static final String WEB_URL = "http://10.206.0.28";
    //正式环境
    public static final String WEB_URL = "http://119.3.71.206";
    private static final String SPLITTER = "/app/";

    public static boolean isExistAlready = false;

    /**
     * 登陆
     */
    public static final String TAG_UPDATE = SPLITTER + "version";
    /**
     * 登陆
     */
    public static final String TAG_LOGIN = SPLITTER + "loginClient";
    /**
     * 登出
     */
    public static final String TAG_LOGOUT = SPLITTER + "logoutClient";
    /**
     * 获取用户信息
     */
    public static final String TAG_USER_INFO = "/userAction"+SPLITTER + "getUserInfo";
    /**
     * 获取用户权限
     */
    public static final String TAG_USER_PERMISSION = "/userAction"+SPLITTER + "getPermission";
    /**
     * 获取标记点类型列表
     */
    public static final String TAG_GET_POINT_TYPE = "/tagAction"+SPLITTER+"getTagTypeList";
    /**
     * 获取标记点终端类型列表
     */
    public static final String TAG_GET_POINT_TERMINAL_TYPE = "/tagAction"+SPLITTER+"getTagCeTypeList";
    /**
     * 获取标记点光缆类型列表
     */
    public static final String TAG_GET_POINT_LINE_TYPE = "/tagAction"+SPLITTER+"getTagLeTypeList";
    /**
     * 获取线路类型列表
     */
    public static final String TAG_GET_LINE_TYPE = "/tagAction"+SPLITTER+"getTagLineTypeList";

    /**
     * 获取设备类型列表
     */
    public static final String TAG_GET_DEVICE_TYPE = "/tagAction"+SPLITTER+"getTagMTypeList";
    /**
     * 获取所有标记点
     */
    public static final String TAG_GET_ALL_POINT = "/tagAction"+SPLITTER+"getAllTag";
    /**
     * 获取某个标记点
     */
    public static final String TAG_GET_SINGLE_POINT = "/tagAction"+SPLITTER+"getTag";
    /**
     *保存某个标记点
     */
    public static final String TAG_SAVE_SINGLE_POINT = "/tagAction"+SPLITTER+"saveTag";
    /**
     * 删除某个标记点
     */
    public static final String TAG_DELETE_SINGLE_POINT = "/tagAction"+SPLITTER+"delTag";
    /**
     * 获取所有线路
     */
    public static final String TAG_GET_ALL_LINE = "/tagAction"+SPLITTER+"getAllTagLine";
    /**
     * 获取某条线路
     */
    public static final String TAG_GET_SINGLE_LINE = "/tagAction"+SPLITTER+"getTagLine";
    /**
     * 保存某条线路
     */
    public static final String TAG_SAVE_SINGLE_LINE = "/tagAction"+SPLITTER+"saveTagLine";
    /**
     * 添加辅助点
     */
    public static final String TAG_SAVE_ASSIST_POINT = "/tagAction"+SPLITTER+"addTagLineAssist";
    /**
     * 删除某条线路
     */
    public static final String TAG_DELETE_SINGLE_LINE = "/tagAction"+SPLITTER+"delTagLine";
    /**
     * 获取维修点历史记录
     */
    public static final String TAG_GET_CHECK_HISTORY = "/tagAction"+SPLITTER+"getTagCheckHistory";
    /**
     * 获取维修点修改记录
     */
    public static final String TAG_GET_POINT_HISTORY = "/tagAction"+SPLITTER+"getTagChangeHistory";
    /**
     * 添加维修点历史记录
     */
    public static final String TAG_SAVE_CHECK = "/tagAction"+SPLITTER+"saveTagCheck";
    /**
     * 添加维修点
     */
    public static final String TAG_SAVE_LINE_CHECK = "/tagAction"+SPLITTER+"addTagLineCheck";
    /**
     * 获取维修点历史记录
     */
    public static final String TAG_GET_CHECK_LINE_HISTORY = "/tagAction"+SPLITTER+"getTagLineCheckHistory";
    /**
     * 获取维修点修改记录
     */
    public static final String TAG_GET_LINE_HISTORY = "/tagAction"+SPLITTER+"getTagLineChangeHistory";

    /**
     * 获取维修点历史记录
     */
    public static final String TAG_GET_NOTICE = "/noticeAction"+SPLITTER+"getLatestNotice";
}