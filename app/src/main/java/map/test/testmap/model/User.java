package map.test.testmap.model;

/**
 * 操作员对象
 *{
 * 	"username": "测试用户1",
 * 	"userAccount": "test_user",
 * 	"password": "",
 * 	"level": 0,
 * 	"id": 1
 * }
 */
public class User {
    private String username;
    private String userAccount;
    private String password;
    private int level;
    private int id;
    private String phone;
    private String email;
    private String reqUri;

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

    public String getReqUri() {
        return reqUri;
    }

    public void setReqUri(String reqUri) {
        this.reqUri = reqUri;
    }
}
