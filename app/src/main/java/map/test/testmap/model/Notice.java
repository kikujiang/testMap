package map.test.testmap.model;

/**
 * "id": 5,
 *             "notice_user_id": 0,
 *             "notice_userName": "",
 *             "notice_time": "2019-02-13 16:01:28",
 *             "operate_model": 1300,
 *             "operate_model_name": "线路",
 *             "notice_title": "编辑线路",
 *             "notice_content": "编辑线路【eee】,修改行为:</br>编号:ee --> ee1</br>",
 *             "operate_link": "/tagAction/app/getTagLine?id=26"
 */
public class Notice {
    private int id;
    private int notice_user_id;
    private String notice_userName;
    private String notice_time;
    private int operate_model;
    private String operate_model_name;
    private String notice_title;
    private String notice_content;
    private String operate_link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotice_user_id() {
        return notice_user_id;
    }

    public void setNotice_user_id(int notice_user_id) {
        this.notice_user_id = notice_user_id;
    }

    public String getNotice_userName() {
        return notice_userName;
    }

    public void setNotice_userName(String notice_userName) {
        this.notice_userName = notice_userName;
    }

    public String getNotice_time() {
        return notice_time;
    }

    public void setNotice_time(String notice_time) {
        this.notice_time = notice_time;
    }

    public int getOperate_model() {
        return operate_model;
    }

    public void setOperate_model(int operate_model) {
        this.operate_model = operate_model;
    }

    public String getOperate_model_name() {
        return operate_model_name;
    }

    public void setOperate_model_name(String operate_model_name) {
        this.operate_model_name = operate_model_name;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public void setNotice_title(String notice_title) {
        this.notice_title = notice_title;
    }

    public String getNotice_content() {
        return notice_content;
    }

    public void setNotice_content(String notice_content) {
        this.notice_content = notice_content;
    }

    public String getOperate_link() {
        return operate_link;
    }

    public void setOperate_link(String operate_link) {
        this.operate_link = operate_link;
    }
}