package map.test.testmap.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 *  "id": 5,
 *  "notice_user_id": 0,
 *  "notice_userName": "",
 *  "notice_time": "2019-02-13 16:01:28",
 *  "operate_model": 1300,
 *  "operate_model_name": "线路",
 *  "notice_title": "编辑线路",
 *  "notice_content": "编辑线路【eee】,修改行为:</br>编号:ee --> ee1</br>",
 *  "operate_link": "/tagAction/app/getTagLine?id=26"
 */
public class Notice extends LitePalSupport {
    @SerializedName("id")
    private int messageId;
    private int notice_user_id;
    private String notice_userName;
    private String notice_time;
    private int operate_model;
    private String operate_model_name;
    private String notice_title;
    private String notice_content;
    private String operate_link;
    private int readStatus;

    public int getId() {
        return messageId;
    }

    public void setId(int id) {
        this.messageId = id;
    }

    public String getNotice_time() {
        return notice_time;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public String getNotice_content() {
        return notice_content;
    }

    public int getReadStatus() {
        return readStatus;
    }
}