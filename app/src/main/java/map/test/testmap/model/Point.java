package map.test.testmap.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
public class Point implements Parcelable {

    private String name;
    private String tagNo;
    private double location_lat;
    private double location_long;
    private int type;
    private String typeStr;
    private String ceTypeStr;
    private String leTypeStr;
    private String mtypeStr;
    private int ce_type;
    private int le_type;
    private String createTime;
    private User createUser;
    private String remark;
    private int id;
    private int tagCheckId;
    private int checkStatus;
    private int lineId;
    private int stationId;
    private String ip;
    private String mtype;
    private String phone;
    private String phone1;
    private List<Image> imageList;
    private String statusIconUrl;
    private String statusIconUrl_APP;
    private String lineName;
    private String stationName;

    @SerializedName("ip1")
    private String ip2;
    @SerializedName("ip_onu")
    private String ip_onu;
    private String l_name;

    public Point() {
        // TODO Auto-generated constructor stub
    }

    protected Point(Parcel in) {
        name = in.readString();
        tagNo = in.readString();
        location_lat = in.readDouble();
        location_long = in.readDouble();
        type = in.readInt();
        typeStr = in.readString();
        ceTypeStr = in.readString();
        leTypeStr = in.readString();
        mtypeStr = in.readString();
        ce_type = in.readInt();
        le_type = in.readInt();
        stationId = in.readInt();
        createTime = in.readString();
        createUser = in.readParcelable(User.class.getClassLoader());
        remark = in.readString();
        id = in.readInt();
        tagCheckId = in.readInt();
        checkStatus = in.readInt();
        lineId = in.readInt();
        ip = in.readString();
        ip2 = in.readString();
        ip_onu = in.readString();
        mtype = in.readString();
        phone = in.readString();
        phone1 = in.readString();
        lineName = in.readString();
        stationName = in.readString();
        statusIconUrl = in.readString();
        statusIconUrl_APP = in.readString();
        l_name = in.readString();
        imageList = in.readArrayList(Image.class.getClassLoader());
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(tagNo);
        dest.writeDouble(location_lat);
        dest.writeDouble(location_long);
        dest.writeInt(type);
        dest.writeInt(stationId);
        dest.writeString(typeStr);
        dest.writeString(ceTypeStr);
        dest.writeString(leTypeStr);
        dest.writeInt(ce_type);
        dest.writeInt(le_type);
        dest.writeString(createTime);
        dest.writeString(lineName);
        dest.writeString(stationName);
        dest.writeParcelable(createUser,flags);
        dest.writeString(remark);
        dest.writeInt(id);
        dest.writeInt(tagCheckId);
        dest.writeInt(checkStatus);
        dest.writeInt(lineId);
        dest.writeString(ip);
        dest.writeString(ip2);
        dest.writeString(ip_onu);
        dest.writeString(l_name);
        dest.writeString(mtype);
        dest.writeString(mtypeStr);
        dest.writeString(phone);
        dest.writeString(phone1);
        dest.writeString(statusIconUrl);
        dest.writeString(statusIconUrl_APP);
        dest.writeList(imageList);
    }

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getMtypeStr() {
        return mtypeStr;
    }

    public void setMtypeStr(String mtypeStr) {
        this.mtypeStr = mtypeStr;
    }

    public String getStatusIconUrl() {
        return statusIconUrl;
    }

    public String getLineName() {
        return lineName;
    }

    public String getStationName() {
        return stationName;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStatusIconUrl_APP() {
        return statusIconUrl_APP;
    }

    public String getIp2() {
        return ip2;
    }

    public String getIp_onu() {
        return ip_onu;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public void setIp_onu(String ip_onu) {
        this.ip_onu = ip_onu;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    @Override
    public String toString() {
        return "id is:"+ id+"name is:" + name;
    }
}