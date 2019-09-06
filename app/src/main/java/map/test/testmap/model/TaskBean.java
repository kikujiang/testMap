package map.test.testmap.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {
 *                 "id": 94,
 *                 "name": "线路 rrr 维修",
 *                 "tag": {
 *                     "id": 158,
 *                     "name": "测试帝豪",
 *                     "location_lat": 31.261888,
 *                     "location_long": 120.732375,
 *                     "type": 0,
 *                     "createTimeLong": 0,
 *                     "checkStatus": 0,
 *                     "tagCheckId": 0,
 *                     "lineId": 13,
 *                     "status_f": 0,
 *                     "lineName": "rrr",
 *                     "ce_type": 0,
 *                     "le_type": 0
 *                 },
 *                 "createTime": "2019-01-18 17:18:47",
 *                 "desc": "哈哈",
 *                 "status": 4,
 *                 "statusStr": "待修复(一般)",
 *                 "isLineCheck": true
 *             }
 */
public class TaskBean implements Parcelable {

    private int id;
    private String name;
    private Point tag;
    private String createTime;
    private String desc;
    private int status;
    private String statusStr;
    private boolean isLineCheck;
    private boolean isPassSelf;
    private int publishItemId;
    private long taskTime;
    private int checkStatus;

    protected TaskBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        status = in.readInt();
        checkStatus = in.readInt();
        publishItemId = in.readInt();
        taskTime = in.readLong();
        statusStr = in.readString();
        createTime = in.readString();
        desc = in.readString();
        isLineCheck = in.readByte()!=0;
        isPassSelf = in.readByte()!=0;
        tag = in.readParcelable(Point.class.getClassLoader());
    }

    public static final Creator<TaskBean> CREATOR = new Creator<TaskBean>() {
        @Override
        public TaskBean createFromParcel(Parcel in) {
            return new TaskBean(in);
        }

        @Override
        public TaskBean[] newArray(int size) {
            return new TaskBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(publishItemId);
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeInt(checkStatus);
        dest.writeString(statusStr);
        dest.writeString(createTime);
        dest.writeLong(taskTime);
        dest.writeByte((byte)(isLineCheck ?1:0));
        dest.writeByte((byte)(isPassSelf ?1:0));
        dest.writeParcelable(tag,flags);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Point getTag() {
        return tag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getDesc() {
        return desc;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public boolean isLineCheck() {
        return isLineCheck;
    }

    public int getPublishItemId() {
        return publishItemId;
    }

    public boolean isPassSelf() {
        return isPassSelf;
    }

    public long getTaskTime() {
        return taskTime;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    @Override
    public String toString() {
        return name+":"+status+"-"+statusStr;
    }
}