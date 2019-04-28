package map.test.testmap.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 "id": 21,
 "name": "将故障报警排除",
 "status": 1,
 "statusStr": "正常",
 "createUserName": "admin",
 "createTime": "2019-01-15 19:04:56",
 "modifyUserName": "admin",
 "modifyTime": "2019-01-16 16:53:07",
 "remark": "ssss\r\nddd\r\ndddd",
 "tagId": 50,
 "imageList": [
 {
 "name": "08_avatar_small.jpg",
 "path": "http://172.17.4.5:8080/resouce/pimages/tagCheck/15476287566560.jpg"
 },
 {
 "name": "11.png",
 "path": "http://172.17.4.5:8080/resouce/pimages/tagCheck/15476287875310.png"
 }
 ],
 "remarkHtml": "ssss<br/>ddd<br/>dddd"
 */
public class State implements Parcelable {

    private int checkId;
    private int id;
    private String name;
    private int status;
    private String statusStr;
    private String createUserName;
    private String createTime;
    private String modifyUserName;
    private String putUserName;
    private String modifyTime;
    private String remark;
    private int tagId;
    private List<Image> imageList;


    public State(){
    }

    protected State(Parcel in) {
        checkId = in.readInt();
        id = in.readInt();
        name = in.readString();
        status = in.readInt();
        statusStr = in.readString();
        createUserName = in.readString();
        createTime = in.readString();
        modifyUserName = in.readString();
        putUserName = in.readString();
        modifyTime = in.readString();
        remark = in.readString();
        tagId = in.readInt();
        imageList = in.readArrayList(null);
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(checkId);
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeString(statusStr);
        dest.writeString(createUserName);
        dest.writeString(createTime);
        dest.writeString(modifyUserName);
        dest.writeString(putUserName);
        dest.writeString(modifyTime);
        dest.writeString(remark);
        dest.writeInt(tagId);
        dest.writeList(imageList);
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

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public String getPutUserName() {
        return putUserName;
    }
}