package map.test.testmap.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 线对象
 * {
 * 	"id": 2,
 * 	"name": "ret",
 * 	"lineNo": "222",
 * 	"lineCount": 111,
 * 	"lineHeight": 333.0,
 * 	"type": 1,
 * 	"hasBox": 1,
 * 	"createTime": "Nov 20, 2018 11:16:32 AM",
 * 	"remark": "eewr\r\newrwe",
 * 	"tag_begin_location_lat": 31.353928,
 * 	"tag_begin_location_long": 120.531351,
 * 	"tag_end_location_lat": 31.386173,
 * 	"tag_end_location_long": 120.513498
 * 	"tag_begin_name":"好久"
 * 	"tag_end_name":"记得"
 * }
 *
 */
public class Line extends LitePalSupport {
    private String manageUserName;
    private String path;
    @SerializedName("id")
    private int lineId;
    private String name;
    private String lineNo;
    private int lineCount;
    private double lineHeight;
    private int type;
    private int hasBox;
    private String createTime;
    private String remark;
    private int status;
    private String statusStr;
    private double tag_begin_location_lat;
    private double tag_begin_location_long;
    private double tag_end_location_lat;
    private double tag_end_location_long;

    private int tag_begin_id;
    private int tag_end_id;
    private Point tag_end;
    private String typeStr;
    private String posTypeStr;
    private List<Point> checkPoints;
    private String statusIconColor;
    private String mac1;
    private String mac2;
    private String ip1;
    private String ip2;
    private String ip_onu;
    private String stationName;
    private String l_name;
    private float lineLength;
    private String tag_begin_name;
    private String tag_end_name;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return lineId;
    }

    public void setId(int id) {
        this.lineId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLineCount() {
        return lineCount;
    }

    public double getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public String getPosTypeStr() {
        return posTypeStr;
    }

    public List<Point> getCheckPoints() {
        return checkPoints;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusIconColor() {
        return statusIconColor;
    }

    public String getMac1() {
        return mac1;
    }

    public String getMac2() {
        return mac2;
    }

    public String getIp1() {
        return ip1;
    }

    public String getIp2() {
        return ip2;
    }

    public String getIp_onu() {
        return ip_onu;
    }

    public String getStationName() {
        return stationName;
    }

    public String getL_name() {
        return l_name;
    }

    public float getLineLength() {
        return lineLength;
    }

    public int getTag_begin_id() {
        return tag_begin_id;
    }

    public int getTag_end_id() {
        return tag_end_id;
    }

    public String getTag_begin_name() {
        return tag_begin_name;
    }

    public String getTag_end_name() {
        return tag_end_name;
    }

    public void setCheckPoints(List<Point> checkPoints) {
        this.checkPoints = checkPoints;
    }

    public String getManageUserName() {
        return manageUserName;
    }

    public void setManageUserName(String manageUserName) {
        this.manageUserName = manageUserName;
    }
}