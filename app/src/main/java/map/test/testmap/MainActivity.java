package map.test.testmap;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "map";
    
    private MapView mMapView;

    private AMap aMap;

    private Button btnCheck;
    private Button btnCollect;
    private Button btnEdit;

    private Location currentLocation = null;
    private Polyline polyline = null;

    private int currentTypeIndex = 0;
//    private boolean isHasBox = false;

    private List<Point> pointList = null;
    private Point currentPoint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        btnCheck = (Button) findViewById(R.id.check);
        btnCollect = (Button) findViewById(R.id.collect);
        btnEdit = (Button) findViewById(R.id.edit);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        initMap();

        btnCheck.setOnClickListener(this);
        btnCollect.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void initMap(){
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        AMap.OnInfoWindowClickListener listener = new AMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker currentMaker) {


            }
        };
        //绑定信息窗点击事件
        aMap.setOnInfoWindowClickListener(listener);
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "onMarkerClick: 被点击啦！！！！");
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
        locate();
    }

    private void locate(){
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("map", "onMyLocationChange: "+ location.toString());
                currentLocation = location;
            }
        });
    }

    private void showMakerInfo(Marker currentMarker){
        double longitude = currentMarker.getOptions().getPosition().longitude;
        double latitude = currentMarker.getOptions().getPosition().latitude;

        for (Point point: pointList ) {
            if(point.getLatitude() == latitude && point.getLongitude() == longitude){
                currentPoint = point;
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.check:
                aMap.clear();
                break;
            case R.id.collect:
//                addMarker();
                dhPopupView();
                break;
            case R.id.edit:
                showAllMarker();
                break;
            case R.id.btn1:
                showAllMarker();
                break;
            case R.id.btn2:
                break;
            case R.id.btn3:
                break;
            case R.id.btn4:
                break;
            case R.id.confirm:

                dealCollectInfo();

                if (window != null){
                    window.dismiss();
                }
                break;
        }
    }

    private void dealCollectInfo(){
        String name = etName.getText().toString();
        String other = etOther.getText().toString();

        Point currentPoint = new Point();
        currentPoint.setName(name);
        currentPoint.setType(currentTypeIndex);
        currentPoint.setOther(other);

        addMarker(currentPoint);
    }

    private void showAllMarker(){
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(31.162829,120.633466));
        latLngs.add(new LatLng(31.062829,120.433466));
        latLngs.add(new LatLng(31.062829,120.233466));
        latLngs.add(new LatLng(31.162829,120.033466));
        polyline =aMap.addPolyline(new PolylineOptions().
                addAll(latLngs).width(10).color(Color.argb(255, 255, 0, 0)));
    }

    private void addMarker(Point currentPoint){

        if(currentLocation == null){
            Toast.makeText(MainActivity.this,"当前定位失败，请重试！",Toast.LENGTH_LONG).show();
            return;
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title(currentPoint.getName()).snippet(currentPoint.getName());

        markerOption.draggable(true);//设置Marker可拖动
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        final Marker marker = aMap.addMarker(markerOption);
        Animation animation = new AlphaAnimation(0,1);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();
        marker.showInfoWindow();

        Toast.makeText(this,"采集点添加成功！",Toast.LENGTH_LONG).show();
    }

    private PopupWindow window = null;

    private EditText etName = null;
    private EditText etOther = null;
    private Spinner spinnerType = null;

    private void dhPopupView() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_list, null);

        // : 2016/5/17 创建PopupWindow对象，指定宽度和高度
//        window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window = new PopupWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        window.setContentView(popupView);
        //设置宽高
        window.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        etName = popupView.findViewById(R.id.edit_name);
        etOther = popupView.findViewById(R.id.edit_other);

        spinnerType = popupView.findViewById(R.id.spinner_type);
        String[] typeData =new String[]{"耐脏","垂直","其他"};
        initSpinner(spinnerType,typeData);
        
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "类型选择当前的选择序号为：" + i);
                currentTypeIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "类型选择当前的选择序号为：0");
                currentTypeIndex = 0;
            }
        });

        Button confirmBtn = popupView.findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(this);

        // : 2016/5/17 设置动画
//        window.setAnimationStyle(R.style.popup_window_anim);
        // : 2016/5/17 设置背景颜色
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88323232")));
        // : 2016/5/17 设置可以获取焦点
        window.setFocusable(true);
        // : 2016/5/17 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // ：更新popupwindow的状态
        window.update();
        window.setClippingEnabled(false);
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int winHeight = getWindow().getDecorView().getHeight();
        window.showAtLocation(popupView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, winHeight - rect.bottom);
//        window.showAsDropDown(btnPopup, 0, 20);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.showAsDropDown(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }*/
    }

    private void initSpinner(Spinner spinner,String[] data){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,data);
        spinner.setAdapter(adapter);
    }
}
