package map.test.testmap.model;

import java.util.HashMap;
import java.util.Map;

import map.test.testmap.utils.OkHttpClientManager;

public class MapBiz implements IMapBiz{

    public static final String url = "";

    @Override
    public void getALLInfo(OnInfoListener listener) {
        Map<String,String> data = new HashMap<>();
        data.put("act","getResUrl");
        OkHttpClientManager.getInstance().post(url,data,listener);
    }

    @Override
    public void addPoint(Point point,OnInfoListener listener) {

    }

    @Override
    public void editPoint(Point point,OnInfoListener listener) {

    }
}
