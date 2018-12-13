package map.test.testmap;

public class Constants {

//    public static final String TEST_URL = "http://172.19.26.52:8080/em";
//    public static final String TEST_URL = "http://172.17.4.5:8080/em_test";
    public static final String TEST_URL = "http://119.3.71.206";
    public static final String SPLITTER = "/";

    /**
     * 登陆
     */
    public static final String TAG_LOGIN = SPLITTER + "loginClient";
    /**
     * 获取标记点类型列表
     */
    public static final String TAG_GET_POINT_TYPE = "/tagAction"+SPLITTER+"getTagTypeList";
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
