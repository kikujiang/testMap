package map.test.testmap.model;

import com.google.gson.annotations.SerializedName;

/**
 * {
 *  "id":13,
 *  "username":"阿尔",
 *  "userAccount":"ae",
 *  "level":1,
 *  "phone":"13706219680",
 *  "email":"",
 *  "levelName":"踩点员",
 *  "sex":1,
 *  "sexName":"男",
 *  "workNo":"",
 *  "address":"",
 *  "hometown":"",
 *  "birth_time":0,
 *  "in_date_time":0,
 *  "desc":"",
 *  "status":0
 *  }
 */
public class ManagerUser {

    @SerializedName("id")
    private int userId;
    @SerializedName("username")
    private String name;
    @SerializedName("userAccount")
    private String account;
    private int level;
    private String phone;
    private String email;
    private String levelName;
    private int sex;
    private String sexName;
    private String workNo;
    private String address;
    private String hometown;
    private int birth_time;
    private int in_date_time;
    private String desc;
    private int status;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public int getBirth_time() {
        return birth_time;
    }

    public void setBirth_time(int birth_time) {
        this.birth_time = birth_time;
    }

    public int getIn_date_time() {
        return in_date_time;
    }

    public void setIn_date_time(int in_date_time) {
        this.in_date_time = in_date_time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}