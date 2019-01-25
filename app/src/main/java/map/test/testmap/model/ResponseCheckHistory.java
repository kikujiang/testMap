package map.test.testmap.model;

import java.util.List;

/**
 * "status": 0,
 *             "createTime": "2019-01-14 18:18:05",
 *             "list": [
 *                 {
 *                     "id": 6,
 *                     "status": 0,
 *                     "createUserName": "admin",
 *                     "createTime": "2019-01-14 18:18:05",
 *                     "modifyUserName": "admin",
 *                     "modifyTime": "2019-01-14 18:18:05",
 *                     "remark": "erwerwe\r\newrwe",
 *                     "tagId": 50,
 *                     "imageList": [
 *                         {
 *                             "name": "7a1e32e696ca42b39639ac4414d0ee5a.jpg",
 *                             "path": "http://172.17.4.5:8080/resouce/pimages/tagCheck/15474610856470.jpg"
 *                         }
 *                     ],
 *                     "remarkHtml": "erwerwe<br/>ewrwe"
 *                 }
 *             ]
 */
public class ResponseCheckHistory {

    private int checkId;
    private int status;
    private String createTime;
    private List<State> list;
    private Point tag;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<State> getList() {
        return list;
    }

    public void setList(List<State> list) {
        this.list = list;
    }

    public Point getTag() {
        return tag;
    }

    public void setTag(Point tag) {
        this.tag = tag;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }
}
