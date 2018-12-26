package map.test.testmap.model;

/**
 * {
 *    "pId": 101,
 *     name": "查看标记点(APP)",
 *     "check": true
 *  }
 */
public class UserPermissionDetail {
    private int pId;
    private String name;
    private boolean check;

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
