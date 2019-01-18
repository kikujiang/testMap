package map.test.testmap.model;

import java.util.List;

/**
 * 服务器返回对象
 * @param <T>
 */
public class ResponseBean<T> {

    private T object;
    private List<T> list;
    private int result;
    private String desc;
    private int id;
    private int tagId;

    public T getObject() {
        return object;
    }

    public void setOject(T oject) {
        this.object = oject;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
