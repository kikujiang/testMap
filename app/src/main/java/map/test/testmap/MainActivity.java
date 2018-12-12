package map.test.testmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.test.testmap.model.Line;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.Point;
import map.test.testmap.model.PointType;
import map.test.testmap.model.ResponsePoint;
import map.test.testmap.model.User;
import map.test.testmap.utils.OkHttpClientManager;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "map";
    
    private MapView mMapView;

    private AMap aMap;

    private Toolbar toolbar;

    private AMapLocation currentLocation = null;
    private TextView tvLocation = null;

    private int currentType = 0;
    private int currentTypeIndex = 0;

    private List<Point> pointList = null;
    private List<Line> lineList = null;
    private Point currentPoint = null;
    private List<PointType> pointTypeList = null;
    private User user = null;
    private ProgressBar progressBar =null;

    public static final int MSG_START = 100000;
    public static final int MSG_END = 100001;
    public static final int MSG_MSG_ERROR = 100002;
    public static final int MSG_LOGIN_END = 100003;
    public static final int MSG_LOGIN_FAILED = 100008;
    public static final int MSG_GET_ALL_POINT_END = 100004;
    public static final int MSG_GET_ALL_LINE_END = 100005;
    public static final int MSG_SAVE_SINGLE_POINT_END = 100006;
    public static final int MSG_GET_SINGLE_POINT_END = 100007;

    private Marker currentMarker = null;

    private boolean isAdd = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case MSG_LOGIN_END:
                    getPointType();
                    getALLPoint();
                    break;
                case MSG_GET_ALL_POINT_END:
                    if(pointList != null){
                        if(pointList.size() > 0){
                            showAllPoint();
                        }
                    }
                    getALLLine();
                    break;
                case MSG_GET_ALL_LINE_END:
                    if(lineList != null){
                        if(lineList.size() <= 0){
                            return;
                        }else{
                            showAllLine();
                        }
                    }
                    break;
                case MSG_SAVE_SINGLE_POINT_END:

                    ResponsePoint responsePoint = (ResponsePoint) msg.obj;
                    if(responsePoint.getResult() == 1){
                        Toast.makeText(MainActivity.this,"保存成功！",Toast.LENGTH_LONG).show();
                        getSinglePoint(responsePoint.getId()+"");
                    }else{
                        Toast.makeText(MainActivity.this,"保存失败，原因是：" + responsePoint.getDesc(),Toast.LENGTH_LONG).show();
                    }
                    if (dialog != null){
                        dialog.dismiss();
                    }

                    break;
                case MSG_GET_SINGLE_POINT_END:
                    if (currentMarker != null){
                        currentMarker.destroy();
                    }

                    if (isAdd){
                        pointList.add(currentPoint);
                    }

                    addMarker(currentPoint);
                    break;
                case MSG_MSG_ERROR:
                    String message = (String) msg.obj;
                    Log.d(TAG, "错误消息为:" + message);
                    Toast.makeText(MainActivity.this,"服务器响应失败，请稍后重试！",Toast.LENGTH_LONG).show();
                    break;
                case MSG_LOGIN_FAILED:
                    String loginError = (String) msg.obj;
                    Log.d(TAG, "登录错误消息为:" + loginError);
                    Toast.makeText(MainActivity.this,loginError,Toast.LENGTH_LONG).show();
                    break;
                case MSG_END:
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=========================onCreate called!=========================");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        checkLocationPermission();
    }

    private void checkLocationPermission(){
        Log.d(TAG, "=========================checkLocationPermission called!=========================");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //没有获取权限，发起申请
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                //doing everything what you want
                initial();
            }
        }else{
            initial();
        }
    }

    private void initial(){
        Log.d(TAG, "=========================initial called!=========================");
        initControl();
        initMap();
        refreshLocation();
        getInfoFromServer();
    }

    /**
     * 定位操作
     */
    private void refreshLocation(){
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }

    private void clearMap(){
        aMap.clear();
    }

    private void getInfoFromServer(){
        Log.d(TAG, "=========================getInfoFromServer called!=========================");
        new Thread(){
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_START);
                getLoginInfo();
                mHandler.sendEmptyMessage(MSG_END);
            }
        }.start();
    }

    /**
     * 获取登录相关信息
     */
    private void getLoginInfo(){
       final String url = Constants.TEST_URL + Constants.TAG_LOGIN;
       Log.d(TAG, "请求登录接口======地址为：" + url );
       final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Log.d(TAG, "登录返回消息为：" + result);
                    if(result.contains("result")){
                        JSONObject object = new JSONObject(result);
                        Message currentMsg = Message.obtain();
                        currentMsg.what = MSG_LOGIN_FAILED;
                        currentMsg.obj = object.getString("desc");
                        mHandler.sendMessage(currentMsg);
                    }else{
                        user = new Gson().fromJson(result,User.class);
                        Log.d(TAG, "收到消息为："+ result+"\n 用户账号为：" + user.getUserAccount());
                        mHandler.sendEmptyMessage(MSG_LOGIN_END);
                    }

                }catch (Exception e){
                    sendErrorMsg(e);
                }
            }

            @Override
            public void fail(Exception e) {
                sendErrorMsg(e);
            }
        });

    }

    private void sendErrorMsg(Exception e){
        Message msg = Message.obtain();
        msg.what = MSG_MSG_ERROR;
        msg.obj = e.getMessage();
        mHandler.sendMessage(msg);
    }

    /**
     * 获取所有点的信息
     */
    private void getALLPoint(){
        Log.d(TAG, "请求获取所有点信息接口");
        final String url = Constants.TEST_URL + Constants.TAG_GET_ALL_POINT;
        final Map<String,String> data = new HashMap<>();
        aMap.clear();
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<List<Point>>(){}.getType();
                    pointList = new Gson().fromJson(result,type);

                    Log.d(TAG, "收到消息为："+ result+"/n 当前标记点数量是：" + pointList.size());
                    mHandler.sendEmptyMessage(MSG_GET_ALL_POINT_END);

                }catch (Exception e){
                    sendErrorMsg(e);
                }
            }

            @Override
            public void fail(Exception e) {
                sendErrorMsg(e);
            }
        });
    }

    private void getALLLine(){
        Log.d(TAG, "请求获取所有线接口");
        final String url = Constants.TEST_URL + Constants.TAG_GET_ALL_LINE;
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<List<Line>>(){}.getType();
                    lineList = new Gson().fromJson(result,type);

                    Log.d(TAG, "收到消息为："+ result+"\n 当前线数量是：" + lineList.size());
                    mHandler.sendEmptyMessage(MSG_GET_ALL_LINE_END);

                }catch (Exception e){
                    sendErrorMsg(e);
                }
                Log.d(TAG,"success: "+responseMapBean.body().toString());
            }

            @Override
            public void fail(Exception e) {
                sendErrorMsg(e);
            }
        });
    }

    private void getPointType(){
        Log.d(TAG, "请求获取点类型接口");
        final String url = Constants.TEST_URL + Constants.TAG_GET_POINT_TYPE;
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<List<PointType>>(){}.getType();
                    pointTypeList = new Gson().fromJson(result,type);
                    Log.d(TAG, "收到消息为："+ result+"\n 当前标记点类型数量是：" + pointTypeList.size());
                }catch (Exception e){
                    sendErrorMsg(e);
                }
            }

            @Override
            public void fail(Exception e) {
                sendErrorMsg(e);
            }
        });
    }

    private void initControl(){
        Log.d(TAG, "=========================initControl called!=========================");
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        tvLocation = (TextView) findViewById(R.id.tv_show_location);

        progressBar.setVisibility(View.GONE);
        initToolBar();
    }

    private void initToolBar(){
        toolbar.setTitle("电力地理信息");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.collect:
                isAdd = true;
                currentPoint = null;
                if(pointTypeList != null){
                    showBottomDialog();
                }else{
                    Toast.makeText(MainActivity.this,"与服务器连接失败，点击刷新重试！",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.refresh:
                getInfoFromServer();
                break;
            case R.id.reLocate:
                refreshLocation();
                break;
            case R.id.clear:
                clearMap();
                break;
                default:
        }
        return true;
    }

    private void initMap(){
        Log.d(TAG, "=========================initMap called!=========================");
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        AMap.OnPolylineClickListener l = new AMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d(TAG, "onPolylineClick: ");
            }
        };

        aMap.setOnPolylineClickListener(l);
        AMap.OnInfoWindowClickListener listener = new AMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker currentMaker) {
                isAdd = false;
                double lat = currentMarker.getPosition().latitude;
                double lng = currentMarker.getPosition().longitude;

                if(currentPoint == null){
                    for (Point current:pointList) {
                        if(current.getLocation_lat() == lat && current.getLocation_long() == lng){
                            currentPoint = current;
                            showBottomDialog();
                        }
                    }
                }else{
                    showBottomDialog();
                }

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
//                marker.setMarkerOptions(marker.getOptions().set);
                currentMarker = marker;
                double lat = currentMarker.getPosition().latitude;
                double lng = currentMarker.getPosition().longitude;
                for (Point current:pointList) {
                    if(current.getLocation_lat() == lat && current.getLocation_long() == lng){
                        currentPoint = current;
                    }
                }
                currentMarker.showInfoWindow();
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
        initLocationInfo();
    }

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器

    private void initLocationInfo(){
        Log.d(TAG, "=========================initLocationInfo called!=========================");
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        AMapLocationListener mLocationListener = new AMapLocationListener(){
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Log.d(TAG, "initLocationInfo onLocationChanged: called:"+ aMapLocation.toString());
                tvLocation.setText(aMapLocation.getLongitude()+","+aMapLocation.getLatitude());
                currentLocation = aMapLocation;
                locate();
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationClient.setLocationOption(mLocationOption);
    }

    private void locate(){

        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
//        myLocationStyle.anchor((float)currentLocation.getLatitude(),(float)currentLocation.getLongitude());
        myLocationStyle.anchor(0.0f,1.0f);
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("map", "onMyLocationChange: "+ location.toString());
                tvLocation.setText(location.getLongitude()+","+location.getLatitude());

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mLocationClient.onDestroy();
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
            case R.id.confirm:

                String name = etName.getText().toString();
                String remark = etOther.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this,"名称不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                if(currentPoint == null){
                    currentPoint = new Point();
                    currentPoint.setId(0);
                    currentPoint.setName(name);
                    currentPoint.setType(currentType);
                    currentPoint.setTagNo("");
                    currentPoint.setRemark(remark);
                    currentPoint.setLocation_lat(currentLocation.getLatitude());
                    currentPoint.setLocation_long(currentLocation.getLongitude());
                }else{
                    currentPoint.setName(name);
                    currentPoint.setType(currentType);
                    currentPoint.setRemark(remark);
                }
                int radioId = radioLocate.getCheckedRadioButtonId();
                if(radioId == R.id.radio_locate_yes) {
                    //
                    currentPoint.setLocation_lat(currentLocation.getLatitude());
                    currentPoint.setLocation_long(currentLocation.getLongitude());
                }

                savePoint(currentPoint);
                break;
            case R.id.cancel:
                if(dialog != null){
                    dialog.dismiss();
                }
                break;
                default:
        }
    }

    /**
     * 参数 id=标记点ID(如新增则为0), name=名称,tagNo=编号,location_lat=纬度,location_long=经度,type=类型,remark=备注
     * @param currentPoint
     */
    private void savePoint(Point currentPoint){
        Log.d(TAG, "请求保存点接口");
        final String url = Constants.TEST_URL + Constants.TAG_SAVE_SINGLE_POINT;
        final Map<String,String> data = new HashMap<>();
        data.put("id",currentPoint.getId()+"");
        data.put("name",currentPoint.getName());
        data.put("tagNo",currentPoint.getTagNo());
        data.put("location_lat",currentPoint.getLocation_lat()+"");
        data.put("location_long",currentPoint.getLocation_long()+"");
        data.put("type",currentPoint.getType()+"");
        data.put("remark",currentPoint.getRemark());
        Log.d(TAG, "savePoint data is:" + data.toString());
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    ResponsePoint responsePoint = new Gson().fromJson(result,ResponsePoint.class);
                    Log.d(TAG, "收到消息为："+ result);
                    Message message = Message.obtain();
                    message.obj = responsePoint;
                    message.what = MSG_SAVE_SINGLE_POINT_END;
                    mHandler.sendMessage(message);
                }catch (Exception e){
                    sendErrorMsg(e);
                }
            }

            @Override
            public void fail(Exception e) {
                sendErrorMsg(e);
            }
        });
    }

    private void getSinglePoint(String pointId){
        Log.d(TAG, "请求获取某个点接口");
        final String url = Constants.TEST_URL + Constants.TAG_GET_SINGLE_POINT;
        final Map<String,String> data = new HashMap<>();
        data.put("id",pointId);
        Log.d(TAG, "getSinglePoint data is:" + data.toString());
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    currentPoint = new Gson().fromJson(result,Point.class);
                    Log.d(TAG, "收到消息为："+ result);
                    mHandler.sendEmptyMessage(MSG_GET_SINGLE_POINT_END);
                }catch (Exception e){
                    sendErrorMsg(e);
                }
            }

            @Override
            public void fail(Exception e) {
                sendErrorMsg(e);
            }
        });
    }

    private void showAllLine(){
        for (Line current:lineList) {
            Log.d(TAG, "showAllLine: called");
            List<LatLng> latLngs = new ArrayList<>();
            LatLng start = new LatLng(current.getTag_begin_location_lat(),current.getTag_begin_location_long());
            LatLng end = new LatLng(current.getTag_end_location_lat(),current.getTag_end_location_long());
            latLngs.add(start);
            latLngs.add(end);
            Polyline line = aMap.addPolyline(new PolylineOptions().
                    addAll(latLngs).setDottedLine(true).width(20).color(Color.argb(255, 255, 0, 0)));
            line.setVisible(true);
        }
    }

    private void showAllPoint(){
        for (Point current: pointList) {
            LatLng latLng = new LatLng(current.getLocation_lat(),current.getLocation_long());
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            String type = "";
            for (PointType currentType:pointTypeList
                 ) {
                if(currentType.getOptionValue() == current.getType()){
                    type = currentType.getOptionText();
                }
            }
            markerOption.title(current.getName()).snippet(type);

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
//            marker.showInfoWindow();

//            Toast.makeText(this,"采集点添加成功！",Toast.LENGTH_LONG).show();
        }
    }

    private void addMarker(Point currentPoint){

        LatLng latLng = new LatLng(currentPoint.getLocation_lat(),currentPoint.getLocation_long());
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        String type = "";
        for (PointType currentType:pointTypeList
                ) {
            if(currentType.getOptionValue() == currentPoint.getType()){
                type = currentType.getOptionText();
            }
        }
        markerOption.title(currentPoint.getName()).snippet(type);

        markerOption.draggable(true);//设置Marker可拖动
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        currentMarker = aMap.addMarker(markerOption);
        currentMarker.showInfoWindow();

        Toast.makeText(this,"采集点操作成功！",Toast.LENGTH_LONG).show();
    }

    private BottomSheetDialog dialog = null;

    private EditText etName = null;
    private EditText etOther = null;
    private Spinner spinnerType = null;
    private RadioGroup radioLocate = null;
    private RadioButton radioNo = null;
    private  Button confirmBtn = null;

    private void showBottomDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_list2, null);
        if(dialog == null ){

            dialog = new BottomSheetDialog(this);

            dialog.setContentView(view);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            etName = view.findViewById(R.id.edit_name);
            etOther = view.findViewById(R.id.edit_other);
            radioLocate = view.findViewById(R.id.radio_locate);
            radioNo = view.findViewById(R.id.radio_locate_no);

            spinnerType = view.findViewById(R.id.spinner_type);
            confirmBtn = view.findViewById(R.id.confirm);
            Button cancelBtn = view.findViewById(R.id.cancel);

            String[] typeData =new String[pointTypeList.size()];

            for (int i=0;i<pointTypeList.size();i++){
                typeData[i] = pointTypeList.get(i).getOptionText();
            }
            currentType = 0;
            initSpinner(spinnerType,typeData);

            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i(TAG, "类型选择当前的选择序号为：" + i);
                    currentTypeIndex = i;
                    currentType = pointTypeList.get(i).getOptionValue();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Log.i(TAG, "类型选择当前的选择序号为：0");
                    currentTypeIndex = 0;
                }
            });

            confirmBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);

        }

        if (isAdd){
            confirmBtn.setText("添加");
        }else{
            confirmBtn.setText("修改");
        }

        radioNo.setChecked(true);
        if(currentPoint != null){
            currentType = currentPoint.getType();
            int index = 0;
            for (PointType item:pointTypeList
                 ) {
                if(item.getOptionValue() == currentType){
                    break;
                }
                index++;
            }

            spinnerType.setSelection(index);
            etName.setText(currentPoint.getName());
            etOther.setText(currentPoint.getRemark());
        }else{
            spinnerType.setSelection(0);
            etName.setText("");
            etOther.setText("");
        }
        dialog.show();
    }

    private void initSpinner(Spinner spinner,String[] data){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,data);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //通过请求码进行筛选
        switch (requestCode) {
            case 1:
                //条件符合说明获取运行时权限成功
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initial();
                } else {
                    //用户拒绝获取权限，则Toast出一句话提醒用户
                    Toast.makeText(this, "应用未开启定位权限，请开启后重试", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}