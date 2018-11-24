package map.test.testmap.model;

/**
 * {
 * 	"result": 2,
 * 	"desc": "失败原因"
 * }
 */
public class ResponsePoint {

    private int result;
    private String desc;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
