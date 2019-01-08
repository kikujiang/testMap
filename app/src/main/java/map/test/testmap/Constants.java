package map.test.testmap;

public class Constants {

    public static final int RESULT_OK = 1;
    public static final int RESULT_FAIL = 2;
//    public static final String WEB_URL = "http://172.19.26.52:8080/em";
//    public static final String TEST_SPLITTER = "em";
//    public static final String TEST_SPLITTER = "";
    public static final String WEB_URL = "http://172.17.4.5:8080";
//    public static final String WEB_URL = "http://119.3.71.206";
    private static final String SPLITTER = "/app/";

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
    public static final String TAG_USER_INFO = SPLITTER + "getUserInfo";
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
     * 删除某条线路
     */
    public static final String TAG_DELETE_SINGLE_LINE = "/tagAction"+SPLITTER+"delTagLine";
}
