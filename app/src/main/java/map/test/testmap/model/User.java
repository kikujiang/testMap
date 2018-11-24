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
}
