package map.test.testmap.mvvm.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *  {
 *             "id": 463,
 *             "name": "通知故障报警为维修(一般)",
 *             "status": 4,
 *             "statusStr": "待修复(一般)",
 *             "createUserName": "吴波",
 *             "createTime": "2019-03-22 15:01:05",
 *             "modifyUserName": "吴波",
 *             "modifyTime": "2019-03-22 15:01:05",
 *             "remark": "youwenti",
 *             "tagId": 272,
 *             "putUserName": "admin",
 *             "overtime": false,
 *             "imageList": [
 *                 {
 *                     "name": "20190322150052.jpg",
 *                     "path": "http://10.206.2.164:8081/resouce/pimages/tagCheck/15532380650690.jpg"
 *                 }
 *             ],
 *             "remarkHtml": "youwenti"
 *         }
 */
public class TaskDetailBean {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private int status;

    @SerializedName("statusStr")
    private String statusStr;

    @SerializedName("createUserName")
    private String createUserName;

    @SerializedName("createTime")
    private String createTime;

    @SerializedName("modifyUserName")
    private String modifyUserName;

    @SerializedName("modifyTime")
    private String modifyTime;

    @SerializedName("remark")
    private String remark;

    @SerializedName("tagId")
    private int tagId;

    @SerializedName("putUserId")
    private int putUserId;

    @SerializedName("putUserName")
    private String putUserName;

    @SerializedName("overtime")
    private boolean overtime;

    @SerializedName("remarkHtml")
    private String remarkHtml;

    @SerializedName("imageList")
    private List<ImageBean> imageList;

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

    public String getCreateUserName() {
        return createUserName;
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

    public String getPutUserName() {
        return putUserName;
    }

    public boolean isOvertime() {
        return overtime;
    }

    public void setOvertime(boolean overtime) {
        this.overtime = overtime;
    }

    public List<ImageBean> getImageList() {
        return imageList;
    }

    public int getPutUserId() {
        return putUserId;
    }
}
