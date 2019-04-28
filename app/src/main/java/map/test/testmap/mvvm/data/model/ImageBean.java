package map.test.testmap.mvvm.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * {
 *  *                     "name": "20190322150052.jpg",
 *  *                     "path": "http://10.206.2.164:8081/resouce/pimages/tagCheck/15532380650690.jpg"
 *  *                 }
 */
public class ImageBean {
    @SerializedName("name")
    private String name;

    @SerializedName("path")
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
