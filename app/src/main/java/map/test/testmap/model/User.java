package map.test.testmap.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 操作员对象
 *{
 * 	"username": "测试用户1",
 * 	"userAccount": "test_user",
 * 	"password_selector": "",
 * 	"level": 0,
 * 	"id": 1
 *
 * }
 *
 * "id": 3,
 *         "username": "admin",
 *         "userAccount": "admin",
 *         "level": 3,
 *         "phone": "13566782347",
 *         "email": "sdfs@sina.com",
 *         "levelName": "管理岗位人员",
 *         "sex": 1,
 *         "sexName": "男",
 *         "workNo": "",
 *         "address": "",
 *         "hometown": "",
 *         "birth_time": 0,
 *         "in_date_time": 0,
 *         "desc": "",
 *         "descHtml": "",
 *         "macLimit": "",
 *         "macLimitHtml": "",
 *         "status": 0
 *
 */
public class User implements Parcelable{
    private String username;
    private String userAccount;
    private String password;
    private int level;
    private int id;
    private String phone;
    private String email;
    private String loginTag;

    protected User(Parcel in) {
        username = in.readString();
        userAccount = in.readString();
        password = in.readString();
        phone = in.readString();
        level = in.readInt();
        id = in.readInt();
        email = in.readString();
        loginTag = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(userAccount);
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(loginTag);
        dest.writeInt(level);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id:" + id+",level is:" + level + ",username is:" + username+",userAccount is:" +userAccount;
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

    public String getLoginTag() {
        return loginTag;
    }
}
