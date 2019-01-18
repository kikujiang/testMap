package map.test.testmap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class LineState implements Parcelable {

    private int id;
    private String name;
    private int status;
    private String statusStr;
    private String createUserName;
    private String createTime;
    private String modifyUserName;
    private String modifyTime;
    private String remark;
    private String pointName;
    private double latitude;
    private double longitude;
    private int tagId;
    private List<Image> imageList;

    public LineState() {
    }

    protected LineState(Parcel in) {
        id = in.readInt();
        name = in.readString();
        pointName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        status = in.readInt();
        statusStr = in.readString();
        createUserName = in.readString();
        createTime = in.readString();
        modifyUserName = in.readString();
        modifyTime = in.readString();
        remark = in.readString();
        tagId = in.readInt();
        imageList = in.readArrayList(null);
    }

    public static final Creator<LineState> CREATOR = new Creator<LineState>() {
        @Override
        public LineState createFromParcel(Parcel in) {
            return new LineState(in);
        }

        @Override
        public LineState[] newArray(int size) {
            return new LineState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(pointName);
        dest.writeInt(status);
        dest.writeString(statusStr);
        dest.writeString(createUserName);
        dest.writeString(createTime);
        dest.writeString(modifyUserName);
        dest.writeString(modifyTime);
        dest.writeString(remark);
        dest.writeInt(tagId);
        dest.writeList(imageList);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

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

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }
}
