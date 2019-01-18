package map.test.testmap.model;

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
 * }
 *
 */
public class Line {
    private int id;
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
    private Point tag_end;
    private String typeStr;
    private String posTypeStr;
    private List<Point> checkPoints;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
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

    public int getHasBox() {
        return hasBox;
    }

    public void setHasBox(int hasBox) {
        this.hasBox = hasBox;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getTag_begin_location_lat() {
        return tag_begin_location_lat;
    }

    public void setTag_begin_location_lat(double tag_begin_location_lat) {
        this.tag_begin_location_lat = tag_begin_location_lat;
    }

    public double getTag_begin_location_long() {
        return tag_begin_location_long;
    }

    public void setTag_begin_location_long(double tag_begin_location_long) {
        this.tag_begin_location_long = tag_begin_location_long;
    }

    public double getTag_end_location_lat() {
        return tag_end_location_lat;
    }

    public void setTag_end_location_lat(double tag_end_location_lat) {
        this.tag_end_location_lat = tag_end_location_lat;
    }

    public double getTag_end_location_long() {
        return tag_end_location_long;
    }

    public void setTag_end_location_long(double tag_end_location_long) {
        this.tag_end_location_long = tag_end_location_long;
    }

    public Point getTag_end() {
        return tag_end;
    }

    public void setTag_end(Point tag_end) {
        this.tag_end = tag_end;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getPosTypeStr() {
        return posTypeStr;
    }

    public void setPosTypeStr(String posTypeStr) {
        this.posTypeStr = posTypeStr;
    }

    public List<Point> getCheckPoints() {
        return checkPoints;
    }

    public void setCheckPoints(List<Point> checkPoints) {
        this.checkPoints = checkPoints;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}
