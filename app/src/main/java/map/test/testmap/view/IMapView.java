package map.test.testmap.view;

import com.amap.api.maps.model.LatLng;

public interface IMapView {
    //添加一个标记点
    void addMarker(LatLng currentLatLng);
    //获取所有的点对象和线对象
    void showAllMarkersAndLines();
    //提示定位错误信息
    void showLocationErrorMsg();
    //重新定位
    void reLocation();

    void showLoading();
    void cancelLoading();

}
