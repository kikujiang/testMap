package map.test.testmap.model;

/**
 *  "id": 164947,
 *             "operate_user_id": 3,
 *             "operate_userName": "admin",
 *             "operate_time": "2019-02-18 18:31:48",
 *             "operate_model": 1300,
 *             "operate_model_name": "线路",
 *             "operate_content": "admin 在2019-02-18 18:31:48 编辑线路【eee】,修改行为:</br>编号:ee1 --> ee1u</br>",
 *             "operate_type": 2,
 *             "operate_type_name": "修改",
 *             "operate_link": "",
 *             "operate_time_long": 1550485908,
 *             "operate_from": 1,
 *             "operate_from_str": "PC",
 *             "operate_id": 26
 */
public class ResponseHistory {
    private int id;
    private int operate_user_id;
    private String operate_userName;
    private String operate_time;
    private int operate_model;
    private String operate_model_name;
    private String operate_content;
    private int operate_type;
    private String operate_type_name;
    private String operate_link;
    private long operate_time_long;
    private int operate_from;
    private String operate_from_str;
    private int operate_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperate_user_id() {
        return operate_user_id;
    }

    public void setOperate_user_id(int operate_user_id) {
        this.operate_user_id = operate_user_id;
    }

    public String getOperate_userName() {
        return operate_userName;
    }

    public void setOperate_userName(String operate_userName) {
        this.operate_userName = operate_userName;
    }

    public String getOperate_time() {
        return operate_time;
    }

    public void setOperate_time(String operate_time) {
        this.operate_time = operate_time;
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

    public String getOperate_content() {
        return operate_content;
    }

    public void setOperate_content(String operate_content) {
        this.operate_content = operate_content;
    }

    public int getOperate_type() {
        return operate_type;
    }

    public void setOperate_type(int operate_type) {
        this.operate_type = operate_type;
    }

    public String getOperate_type_name() {
        return operate_type_name;
    }

    public void setOperate_type_name(String operate_type_name) {
        this.operate_type_name = operate_type_name;
    }

    public String getOperate_link() {
        return operate_link;
    }

    public void setOperate_link(String operate_link) {
        this.operate_link = operate_link;
    }

    public long getOperate_time_long() {
        return operate_time_long;
    }

    public void setOperate_time_long(long operate_time_long) {
        this.operate_time_long = operate_time_long;
    }

    public int getOperate_from() {
        return operate_from;
    }

    public void setOperate_from(int operate_from) {
        this.operate_from = operate_from;
    }

    public String getOperate_from_str() {
        return operate_from_str;
    }

    public void setOperate_from_str(String operate_from_str) {
        this.operate_from_str = operate_from_str;
    }

    public int getOperate_id() {
        return operate_id;
    }

    public void setOperate_id(int operate_id) {
        this.operate_id = operate_id;
    }
}
