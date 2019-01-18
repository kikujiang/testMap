package map.test.testmap.model;

import java.util.List;

/**
 * 点对象
 * {
 * 	"name": "reteter",
 * 	"tagNo": "reterterr",
 * 	"location_lat": 31.386173,
 * 	"location_long": 120.513498,
 * 	"type": 1,
 * 	"createTime": "Nov 16, 2018 7:32:01 PM",
 * 	"createUser": {
 * 		"level": 0,
 * 		"id": 1
 *        },
 * 	"remark": "rtretreter",
 * 	"id": 4
 * 	"checkStatus": 0,
 *             "tagCheckId": 0,
 * }
 */
public class Point {

    private String name;
    private String tagNo;
    private double location_lat;
    private double location_long;
    private int type;
    private String ceTypeStr;
    private String leTypeStr;
    private int ce_type;
    private int le_type;
    private String createTime;
    private User createUser;
    private String remark;
    private int id;
    private int tagCheckId;
    private int checkStatus;
    private int lineId;
    private List<Image> imageList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }

    public double getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(double location_lat) {
        this.location_lat = location_lat;
    }

    public double getLocation_long() {
        return location_long;
    }

    public void setLocation_long(double location_long) {
        this.location_long = location_long;
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

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return imageList;
    }

    public void setImages(List<Image> imageList) {
        this.imageList = imageList;
    }

    public String getCeTypeStr() {
        return ceTypeStr;
    }

    public void setCeTypeStr(String ceTypeStr) {
        this.ceTypeStr = ceTypeStr;
    }

    public String getLeTypeStr() {
        return leTypeStr;
    }

    public void setLeTypeStr(String leTypeStr) {
        this.leTypeStr = leTypeStr;
    }

    public int getCe_type() {
        return ce_type;
    }

    public void setCe_type(int ce_type) {
        this.ce_type = ce_type;
    }

    public int getLe_type() {
        return le_type;
    }

    public void setLe_type(int le_type) {
        this.le_type = le_type;
    }

    public int getTagCheckId() {
        return tagCheckId;
    }

    public void setTagCheckId(int tagCheckId) {
        this.tagCheckId = tagCheckId;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    @Override
    public String toString() {
        return "id is:"+ id+"name is:" + name;
    }
}
