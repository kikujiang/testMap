package map.test.testmap.view;

import com.amap.api.maps.model.LatLng;

import map.test.testmap.Point;

public interface IMapView {
    //添加一个marker标记
    void addMarker(LatLng currentMarker);
    //重新定位
    void reLocate();
    //显示定位错误信息
    void showLocationErrorMsg();
    //显示所有的标记点
    void showAllMarkers();

    void showMarkerInfo(Point currentPoint);
}
