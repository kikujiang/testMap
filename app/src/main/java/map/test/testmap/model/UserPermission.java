package map.test.testmap.model;

import java.util.List;

/**
 * {
 *     "result": 1,
 *     "list": [
 *         {
 *             "mId": 1200,
 *             "mName": "标记点",
 *             "permissions": [
 *                 {
 *                     "pId": 101,
 *                     "name": "查看标记点(APP)",
 *                     "check": true
 *                 },
 *                 {
 *                     "pId": 102,
 *                     "name": "添加标记点(APP)",
 *                     "check": true
 *                 },
 *                 {
 *                     "pId": 103,
 *                     "name": "修改标记点(APP)",
 *                     "check": true
 *                 },
 *                 {
 *                     "pId": 104,
 *                     "name": "删除标记点(APP)",
 *                     "check": true
 *                 }
 *             ]
 *         },
 *         {
 *             "mId": 1300,
 *             "mName": "线路",
 *             "permissions": [
 *                 {
 *                     "pId": 101,
 *                     "name": "查看线路(APP)",
 *                     "check": true
 *                 },
 *                 {
 *                     "pId": 102,
 *                     "name": "添加线路(APP)",
 *                     "check": true
 *                 },
 *                 {
 *                     "pId": 103,
 *                     "name": "修改线路(APP)",
 *                     "check": true
 *                 },
 *                 {
 *                     "pId": 104,
 *                     "name": "删除线路(APP)",
 *                     "check": true
 *                 }
 *             ]
 *         },
 *         {
 *             "mId": 1000,
 *             "mName": "登陆",
 *             "permissions": [
 *                 {
 *                     "pId": 101,
 *                     "name": "登陆APP",
 *                     "check": true
 *                 }
 *             ]
 *         }
 *     ]
 * }
 */
public class UserPermission {
    private int mId;
    private String mName;
    private List<UserPermissionDetail> permissions;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public List<UserPermissionDetail> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<UserPermissionDetail> permissions) {
        this.permissions = permissions;
    }
}
