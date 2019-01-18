package map.test.testmap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.test.testmap.model.DataType;
import map.test.testmap.model.Image;
import map.test.testmap.model.Line;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponsePoint;
import map.test.testmap.model.UserPermission;
import map.test.testmap.model.UserPermissionDetail;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import map.test.testmap.utils.OkHttpClientManager;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "map";
    public static final int CURRENT_TYPE_POINT = 1;
    public static final int CURRENT_TYPE_POINT_TERMINAL = 2;
    public static final int CURRENT_TYPE_POINT_LINE = 3;

    private boolean isPointAdd = false;
    private boolean isPointRead = false;
    private boolean isPointWrite = false;
    private boolean isLineRead = false;
    private boolean isHistoryItemADD = false;
    private boolean isHistoryItemQuery = false;
    private boolean isHistoryItemFix = false;
    private boolean isLineCheckAdd = false;


    private MapView mMapView;

    private AMap aMap;

    private Toolbar toolbar;

    private AMapLocation currentLocation = null;
    private TextView tvLocation = null;

    private int currentType = 0;
    private int currentTerminalType = 0;
    private int currentLineType = 0;

    private List<Point> pointList = null;
    private List<Line> lineList = null;
    private Point currentPoint = null;
    private Line currentLine = null;
    private String currentSearch = "";
    private List<DataType> pointTypeList = null;
    private List<DataType> pointTerminalTypeList = null;
    private List<DataType> pointLineTypeList = null;
    private List<Marker> markerList = null;
    private LinearLayout progressBar =null;

    public static final int MSG_START = 100000;
    public static final int MSG_END = 100001;
    public static final int MSG_MSG_ERROR = 100002;
    public static final int MSG_MSG_FAILURE = 100003;
    public static final int MSG_LOGIN_FAILED = 100010;
    public static final int MSG_GET_ALL_POINT_END = 100004;
    public static final int MSG_GET_ALL_LINE_END = 100005;
    public static final int MSG_SAVE_SINGLE_POINT_END = 100006;
    public static final int MSG_GET_SINGLE_POINT_END = 100007;
    public static final int MSG_GET_SINGLE_LINE_END = 100008;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0;
    public final static int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_MAINTENANCE_TAKE_PICTURE = 2;
    public final static int REQUEST_CODE_MAINTENANCE_GALLERY = 3;
    public final static int TYPE_MAIN = 4;
    public final static int TYPE_MAINTENANCE = 5;

    private int currentImageType = TYPE_MAIN;

    private Marker currentMarker = null;

    private boolean isAdd = false;
    private boolean isMaintenanceAdd = true;
    private String showMsg= "";

    private List<String> imageLocalPath = null;

    private List<String> searchData = null;
    private HashMap<String,Object> searchDataMarkerMap = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START:
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "handleMessage: msg start");
                    break;
                case MSG_GET_ALL_POINT_END:
                    if(pointList != null){
                        if(pointList.size() > 0){
                            showAllPoint();
                        }
                    }
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

                    int id = (int) msg.obj;
                    Toast.makeText(MainActivity.this,"保存成功！",Toast.LENGTH_LONG).show();
                    getSinglePoint(id+"");

                    if (pointDialog != null&& pointDialog.isShowing()){
                        pointDialog.dismiss();
                    }

                    if(maintenanceDialog  != null && maintenanceDialog.isShowing()){
                        maintenanceDialog.dismiss();
                    }
                    break;
                case MSG_GET_SINGLE_POINT_END:
                    if (currentMarker != null){
                        currentMarker.destroy();
                    }

                    if (isAdd){
                        pointList.add(currentPoint);
                    }

                    clearImageData();
                    if(currentPoint.getImages() != null){
                        Log.d(TAG, "handleMessage: "+ currentPoint.getImages().size());
                        configRemoteData(currentPoint.getImages());
                    }
                    addMarker(currentPoint);
                    break;
                case MSG_MSG_ERROR:
                    String message = (String) msg.obj;
                    Log.d(TAG, "错误消息为:" + message);
                    Toast.makeText(MainActivity.this,"服务器响应失败，请稍后重试！",Toast.LENGTH_LONG).show();
                    break;
                case MSG_MSG_FAILURE:
                    String failureMsg = (String) msg.obj;
                    Log.d(TAG, "失败消息为:" + failureMsg);
                    Toast.makeText(MainActivity.this,failureMsg,Toast.LENGTH_LONG).show();
                    break;
                case MSG_LOGIN_FAILED:
                    String loginError = (String) msg.obj;
                    Log.d(TAG, "登录错误消息为:" + loginError);
                    Toast.makeText(MainActivity.this,loginError,Toast.LENGTH_LONG).show();
                    break;
                case MSG_END:
                    Log.d(TAG, "handleMessage: msg end");
                    progressBar.setVisibility(View.GONE);
                    break;
                case MSG_GET_SINGLE_LINE_END:
                    if(currentLine != null){
                        Log.d(TAG, "onPolylineClick: "+currentLine.getName());

                        if(currentPolyLine != null){
                            currentPolyLine.remove();
                        }

                        List<LatLng> latLngs = new ArrayList<>();

                        for (Point item:currentLine.getCheckPoints()) {
                            LatLng cur = new LatLng(item.getLocation_lat(),item.getLocation_long());
                            latLngs.add(cur);
                        }

                        switch (currentLine.getStatus()){
                            case 0:
                            case 1:
                                currentPolyLine = aMap.addPolyline(new PolylineOptions().
                                        addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#4096FB")));
                                currentPolyLine.setVisible(true);
                                break;
                            case 2:
                                currentPolyLine = aMap.addPolyline(new PolylineOptions().
                                        addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#73736A")));
                                currentPolyLine.setVisible(true);
                                break;
                            case 3:
                                currentPolyLine = aMap.addPolyline(new PolylineOptions().
                                        addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#DC0A0A")));
                                currentPolyLine.setVisible(true);
                                break;
                            case 4:
                                currentPolyLine = aMap.addPolyline(new PolylineOptions().
                                        addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#380C0B")));
                                currentPolyLine.setVisible(true);
                                break;
                        }
                        showLineBottomDialog();
                    }else {
                        Toast.makeText(MainActivity.this,"当前线路信息异常，请重新刷新！",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private int userId = 0;
    private boolean isExistAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = getIntent().getIntExtra("userId",0);
        String mac = Common.getInstance().getMacAddress();
        Log.d(TAG, "=========================onCreate called!========================= user is is：" + userId+",and mac is:" + mac);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        checkLocationPermission();
    }

    private void checkLocationPermission(){
        Log.d(TAG, "=========================checkLocationPermission called!=========================");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                //没有获取权限，发起申请
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_SETTINGS}, 1);
            } else {
                //doing everything what you want
                initial();
            }
        }else{
            initial();
        }
    }


    private void enterCheckActivity(){
        Intent stateIntent = new Intent(MainActivity.this,StateActivity.class);
        Log.d(TAG, "传入的点id为：" + currentPoint.getId());
        stateIntent.putExtra("point_id",currentPoint.getId());
        stateIntent.putExtra("itemAdd",isHistoryItemADD);
        stateIntent.putExtra("itemQuery",isHistoryItemQuery);
        stateIntent.putExtra("itemFix",isHistoryItemFix);
        startActivityForResult(stateIntent,Constants.REQUEST_CODE);
    }

    private void enterLineCheckActivity(){
        Intent stateIntent = new Intent(MainActivity.this,LineStateHistoryActivity.class);
        Log.d(TAG, "传入的点id为：" + currentLine.getId());
        stateIntent.putExtra("line_id",currentLine.getId());
        startActivityForResult(stateIntent,Constants.REQUEST_LINE_CODE);
    }

    /**
     * 获取所有的类型信息
     */
    private void getAllType(){
        getPointType();
        getPointTerminalType();
        getPointLineType();
    }

    private void initial(){
        Log.d(TAG, "=========================initial called!=========================");
        initControl();
        initMap();
        refreshLocation();
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

    private void sendErrorMsg(Exception e){
        Message msg = Message.obtain();
        msg.what = MSG_MSG_ERROR;
        msg.obj = e.getMessage();
        mHandler.sendMessage(msg);
    }

    private void sendFailureMsg(String message){
        Message msg = Message.obtain();
        msg.what = MSG_MSG_FAILURE;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }

    /**
     * 退出app
     */
    private void logOut(int userId, final boolean isClearAllData){
        Log.d(TAG, "=========================logOut called!=========================");
        HttpUtils.getInstance().logOut(userId, new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {

                if(responseMapBean == null){
                    Toast.makeText(MainActivity.this,"访问服务器超时，请稍后重试！",Toast.LENGTH_LONG).show();
                    return;
                }

                ResponseBean current = (ResponseBean)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    sendFailureMsg(current.getDesc());
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

                if(current.getResult() == Constants.RESULT_OK){
                    isExistAlready = true;
                    if(isClearAllData){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"退出成功",Toast.LENGTH_LONG).show();
                                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        });
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                sendFailureMsg(e.getMessage());
            }
        });
    }

    /**
     * 获取用户权限的信息
     */
    private void getUserPermission(int userId){
        mHandler.sendEmptyMessage(MSG_START);
        HttpUtils.getInstance().getUserPermission(userId, new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {

                if(responseMapBean == null){
                    Toast.makeText(MainActivity.this,"访问服务器超时，请稍后重试！",Toast.LENGTH_LONG).show();
                    return;
                }

                ResponseBean<UserPermission> current = (ResponseBean<UserPermission>)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    sendFailureMsg(current.getDesc());
                }

                if(current.getResult() == Constants.RESULT_OK){
                    resetState();
                    List<UserPermission> permissionList = current.getList();
                    setUserPermission(permissionList);
                    getDataFromServer();
                }
            }

            @Override
            public void fail(Throwable e) {
                sendFailureMsg(e.getMessage());
            }
        });
    }

    private void setUserPermission(List<UserPermission> permissionList){
        for (UserPermission cur:permissionList) {
            if(cur.getmId() == 1200){
                //检查点的权限
                for (UserPermissionDetail detail: cur.getPermissions()) {
                    switch (detail.getpId()){
                        case 101:
                            isPointRead = detail.isCheck();
                            break;
                        case 102:
                            isPointAdd = detail.isCheck();
                            break;
                        case 103:
                            isPointWrite = detail.isCheck();
                            break;
                        case 105:
                            isHistoryItemQuery = detail.isCheck();
                            break;
                        case 106:
                            isHistoryItemFix = detail.isCheck();
                            break;
                        case 107:
                            isHistoryItemADD = detail.isCheck();
                            break;
                    }
                }
            }

            if(cur.getmId() == 1300){
                //检查点的权限
                for (UserPermissionDetail detail: cur.getPermissions()) {
                    switch (detail.getpId()){
                        case 101:
                            isLineRead = detail.isCheck();
                            break;
                    }
                }
            }
        }
    }

    private void resetState(){
        isPointAdd = false;
        isPointRead = false;
        isPointWrite = false;
        isLineRead = false;

        isHistoryItemADD = false;
        isHistoryItemFix = false;
        isHistoryItemQuery = false;
        isLineCheckAdd = false;
    }

    private void getDataFromServer(){
        if(isPointRead){
            getAllType();
            getALLPoint();
        }

        if(isPointAdd && pointTypeList == null){
            getAllType();
        }

        if(isLineRead){
            getALLLine();
        }

    }

    /**
     * 获取所有点的信息
     */
    private void getALLPoint(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_ALL_POINT;
        Log.d(TAG, "请求获取所有点信息接口" + url);
        final Map<String,String> data = new HashMap<>();
        aMap.clear();
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<Point>>(){}.getType();

                    ResponseBean<Point> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg(currentData.getDesc());
                        return;
                    }

                    if (currentData.getResult() == Constants.RESULT_OK){
                        pointList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"/n 当前标记点数量是：" + pointList.size());
                        mHandler.sendEmptyMessage(MSG_GET_ALL_POINT_END);
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

    private void getALLLine(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_ALL_LINE;
        Log.d(TAG, "请求获取所有线接口" + url);
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<Line>>(){}.getType();

                    ResponseBean<Line> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg(currentData.getDesc());
                        return;
                    }

                    if (currentData.getResult() == Constants.RESULT_OK){
                        lineList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前线数量是：" + lineList.size());
                        mHandler.sendEmptyMessage(MSG_GET_ALL_LINE_END);
                    }
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
        final String url = Constants.WEB_URL + Constants.TAG_GET_POINT_TYPE;
        Log.d(TAG, "请求获取点类型接口" + url);
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<DataType>>(){}.getType();
                    ResponseBean<DataType> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg(currentData.getDesc());
                        return;
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){
                        pointTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点类型数量是：" + pointTypeList.size());
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

    private void getPointTerminalType(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_POINT_TERMINAL_TYPE;
        Log.d(TAG, "请求获取点终端类型接口"+ url);
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<DataType>>(){}.getType();
                    ResponseBean<DataType> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg(currentData.getDesc());
                        return;
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){
                        pointTerminalTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点终端类型数量是：" + pointTerminalTypeList.size());
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

    private void getPointLineType(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_POINT_LINE_TYPE;
        Log.d(TAG, "请求获取点光缆类型接口"+ url);
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<DataType>>(){}.getType();
                    ResponseBean<DataType> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg(currentData.getDesc());
                        return;
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){
                        pointLineTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点光缆类型数量是：" + pointLineTypeList.size());
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

    private void initControl(){
        Log.d(TAG, "=========================initControl called!=========================");
        toolbar = findViewById(R.id.id_toolbar);
        progressBar = findViewById(R.id.loading);
        tvLocation = findViewById(R.id.tv_show_location);
        searchView = findViewById(R.id.search_view);

        progressBar.setVisibility(View.GONE);
        initToolBar();
        initSearchView();
        initImageData();
        getUserPermission(userId);
    }

    private void initSearchView(){
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: "+query);
                if(query.contains(",")){
                    currentSearch = query;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                Log.d(TAG, "onSearchViewShown: ");
                currentSearch = "";
            }

            @Override
            public void onSearchViewClosed() {
                Log.d(TAG, "onSearchViewClosed: ");
                if(!"".equals(currentSearch)){
                    Marker curMarker = (Marker) searchDataMarkerMap.get(currentSearch);
                    String currentId = currentSearch.split(",")[1];
                    getSinglePoint(currentId);
                    curMarker.showInfoWindow();
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(curMarker.getPosition()));
                }
            }
        });
    }

    private MaterialSearchView searchView = null;

    private void initToolBar(){
        toolbar.setTitle("电力地理信息");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.main,menu);
        MenuItem item = menu.findItem(R.id.search);
        searchView.setMenuItem(item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.collect:
                if(isPointAdd){
                    clearImageData();
                    isAdd = true;
                    if(currentPoint != null){
                        currentMarker.hideInfoWindow();
                    }

                    currentPoint = null;
                    currentMarker = null;
                    if(pointTypeList != null){
                        showPointBottomDialog(null);
                    }else{
                        Toast.makeText(MainActivity.this,"与服务器连接失败，点击刷新重试！",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"当前账户无权限，请授予权限后重试！",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.refresh:
                clearMap();
                getUserPermission(userId);
                refreshLocation();
                break;
            case R.id.reLocate:
                refreshLocation();
                break;
            case R.id.clear:
                clearMap();
                break;
            case R.id.exit:
                logOut(userId,true);
                break;
            case R.id.update:
                checkUpdate();
                break;
                default:
        }
        return true;
    }

    private void checkUpdate(){
        HttpUtils.getInstance().checkUpdate(new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {
                if(responseMapBean == null){
                    Log.d(TAG, "服务器返回对象为空");
                    return;
                }

                ResponseBean current = (ResponseBean)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    Log.d(TAG, "fail message is:" + current.getDesc());
                    Toast.makeText(MainActivity.this,current.getDesc(),Toast.LENGTH_LONG).show();
                }

                if(current.getResult() == Constants.RESULT_OK){
                    int serverVersion = current.getId();
                    int currentVersion = Common.getInstance().getVersionCode(MainActivity.this);

                    if(currentVersion < serverVersion){
                        download(current.getDesc());
                    }else{
                        Toast.makeText(MainActivity.this,"当前是最新版本",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail: "+e.getMessage());
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void download(final String url) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("版本更新")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Common.getInstance().downloadApk(MainActivity.this,url,"下载中","电力地理系统");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private double tempLat = 0.0d;
    private double tempLng = 0.0d;
    private double tempStartLat = 0.0d;
    private double tempStartLng = 0.0d;
    private boolean isDrag = false;
    private boolean isEmptyMarker = false;

    private void initMap(){
        Log.d(TAG, "=========================initMap called!=========================");
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        AMap.OnPolylineClickListener l = new AMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d(TAG, "onPolylineClick: ");
                double startLat = polyline.getPoints().get(0).latitude;
                double startLng = polyline.getPoints().get(0).longitude;
                double endLat = polyline.getPoints().get(polyline.getPoints().size() -1).latitude;
                double endLng = polyline.getPoints().get(polyline.getPoints().size() -1).longitude;

                Log.d(TAG, "onPolylineClick: "+startLat+","+startLng+",  \nend is:"+endLat+","+endLng);

                for (Line cur: lineList) {
                    if (cur.getCheckPoints().get(0).getLocation_lat() == startLat &&
                            cur.getCheckPoints().get(0).getLocation_long() == startLng &&
                            cur.getCheckPoints().get(cur.getCheckPoints().size()-1).getLocation_lat() == endLat &&
                            cur.getCheckPoints().get(cur.getCheckPoints().size()-1).getLocation_long() == endLng){
                        currentLine = cur;
                        currentPolyLine = polyline;
                    }
                }

                getSingleLine(currentLine.getId()+"");
            }
        };

        aMap.setOnPolylineClickListener(l);
        AMap.OnInfoWindowClickListener listener = new AMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker currentMaker) {
                Log.d(TAG, "onInfoWindowClick: " + currentPoint);
                isAdd = false;

                if(currentPoint == null){
                    double lat = currentMarker.getPosition().latitude;
                    double lng = currentMarker.getPosition().longitude;
                    for (Point current:pointList) {
                        if(current.getLocation_lat() == lat && current.getLocation_long() == lng){
                            currentPoint = current;
                        }
                    }
                }

                if(currentPoint != null){

                    if(currentPoint.getType() == 101){
                        isMaintenanceAdd = false;
                        showMaintenanceDialog();
                    }else{
                        showPointBottomDialog(null);
                    }
                } else{
                    Toast.makeText(MainActivity.this,"当前点信息异常，请重新刷新！",Toast.LENGTH_LONG).show();
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
                currentMarker = marker;
                double lat = currentMarker.getPosition().latitude;
                double lng = currentMarker.getPosition().longitude;
                for (Point current:pointList) {
                    if(current.getLocation_lat() == lat && current.getLocation_long() == lng){
                        currentPoint = current;
                    }
                }
                getSinglePoint(currentPoint.getId()+"");
                currentMarker.showInfoWindow();
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
        
        
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "onMarkerDragStart: "+marker.getPosition().latitude+","+marker.getPosition().longitude);
                if(currentPoint == null || currentMarker.getPosition().longitude != marker.getPosition().longitude){
                    tempStartLat = marker.getPosition().latitude;
                    tempStartLng = marker.getPosition().longitude;
                    isEmptyMarker = true;
                }
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(isEmptyMarker){
                    isEmptyMarker = false;
                    marker.setPosition(new LatLng(tempStartLat,tempStartLng));
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(tempStartLat,tempStartLng)));
                }else{
                    tempLat = marker.getPosition().latitude;
                    tempLng = marker.getPosition().longitude;
                    Log.d(TAG, "onMarkerDragEnd: "+marker+",lat==="+tempLat);
                    isDrag = true;
                    showPointBottomDialog(null);
                }
            }
        });
        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng current) {
                if(isPointAdd){
                    clearImageData();
                    isAdd = true;

                    if(currentPoint != null){
                        currentMarker.hideInfoWindow();
                    }

                    currentPoint = null;
                    currentMarker = null;
                    if(pointTypeList != null){
                        showPointBottomDialog(current);
                    }else{
                        Toast.makeText(MainActivity.this,"与服务器连接失败，点击刷新重试！",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"当前账户无权限，请授予权限后重试！",Toast.LENGTH_LONG).show();
                }
            }
        });
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
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    showMsg = "当前经度："+aMapLocation.getLongitude()+"，纬度："+aMapLocation.getLatitude();
                    tvLocation.setText(showMsg);
                    currentLocation = aMapLocation;
                    locate();
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Toast.makeText(MainActivity.this,"定位失败,原因是："+aMapLocation.getErrorInfo()+"。请重新尝试！",Toast.LENGTH_LONG).show();
                    Log.e("Error","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
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
//        myLocationStyle.anchor(0.0f,0.5f);
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("map", "onMyLocationChange: "+ location.toString());
                showMsg = "当前经度："+location.getLongitude()+"，纬度："+location.getLatitude();
                tvLocation.setText(showMsg);
                mHandler.sendEmptyMessage(MSG_END);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "==============================onDestroy: "+isExistAlready);
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        if(!isExistAlready){
            Log.d(TAG, "==============================如果没有退出，发送退出消息");
            logOut(userId,false);
        }
        mMapView.onDestroy();
        mLocationClient.onDestroy();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "==============================onResume: ");
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();

    }
    @Override
    protected void onPause() {
        Log.d(TAG, "==============================onPause: ");
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
                //判断名称
                String name = etName.getText().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this,"名称不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断经度
                String longitudeStr = etLongitude.getText().toString();
                if(TextUtils.isEmpty(longitudeStr)){
                    Toast.makeText(MainActivity.this,"经度不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断纬度
                String latitudeStr = etLatitude.getText().toString();
                if(TextUtils.isEmpty(latitudeStr)){
                    Toast.makeText(MainActivity.this,"纬度不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                String remark = etOther.getText().toString();
                double latitude = Double.valueOf(latitudeStr);
                double longitude = Double.valueOf(longitudeStr);

                if(currentPoint == null){
                    currentPoint = new Point();
                    currentPoint.setId(0);
                    currentPoint.setName(name);
                    currentPoint.setType(currentType);
                    currentPoint.setCe_type(currentTerminalType);
                    currentPoint.setLe_type(currentLineType);
                    currentPoint.setTagNo("");
                    currentPoint.setRemark(remark);
                    currentPoint.setLocation_lat(latitude);
                    currentPoint.setLocation_long(longitude);
                }else{
                    currentPoint.setName(name);
                    currentPoint.setType(currentType);
                    currentPoint.setCe_type(currentTerminalType);
                    currentPoint.setLe_type(currentLineType);
                    currentPoint.setRemark(remark);
                    currentPoint.setLocation_lat(latitude);
                    currentPoint.setLocation_long(longitude);
                }

                savePoint(currentPoint,imageLocalPath);
                break;
            case R.id.cancel:

                if(pointDialog != null){
                    pointDialog.dismiss();
                }

                if(isDrag){
                    isDrag = false;
                    currentMarker.setPosition(new LatLng(currentPoint.getLocation_lat(),currentPoint.getLocation_long()));
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
                }
                break;
            case R.id.navi:
                if(currentPoint == null){
                    Toast.makeText(MainActivity.this,"当前节点数据为空，请重新刷新！",Toast.LENGTH_LONG).show();
                    return;
                }
                invokingGD(currentPoint.getLocation_lat(),currentPoint.getLocation_long());
                break;
            case R.id.locate:
                refreshLocation();
                if(currentLocation != null){
                    etLatitude.setText(String.valueOf(currentLocation.getLatitude()));
                    etLongitude.setText(String.valueOf(currentLocation.getLongitude()));
                }
                break;
            case R.id.btn_take_pic:
                checkCameraPermission();
                break;
            case R.id.btn_pick_pic:
                choosePhoto();
                break;
            case R.id.btn_ok:
                if(lineDialog != null){
                    lineDialog.dismiss();
                }
                break;
            case R.id.state:
                if(pointDialog != null && pointDialog.isShowing()){
                    pointDialog.dismiss();
                }
                enterCheckActivity();
                break;
            case R.id.line_state:
                if(lineDialog != null){
                    lineDialog.dismiss();
                }
                enterLineCheckActivity();
                break;
            case R.id.line_create_point:
                if(lineDialog != null){
                    lineDialog.dismiss();
                }
                isMaintenanceAdd = true;
                showMaintenanceDialog();
                break;
            case R.id.maintenance_close:
                if(maintenanceDialog  != null){
                    maintenanceDialog.dismiss();
                }
                currentImageType = TYPE_MAIN;
                break;
            case R.id.maintenance_add:

                //判断名称
                String mName = maintenanceName.getText().toString();
                if(TextUtils.isEmpty(mName)){
                    Toast.makeText(MainActivity.this,"名称不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断经度
                String mLongitudeStr = maintenanceLng.getText().toString();
                if(TextUtils.isEmpty(mLongitudeStr)){
                    Toast.makeText(MainActivity.this,"经度不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断纬度
                String mLatitudeStr = maintenanceLat.getText().toString();
                if(TextUtils.isEmpty(mLatitudeStr)){
                    Toast.makeText(MainActivity.this,"纬度不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断名称
                String mRemark = maintenanceRemark.getText().toString();
                if(TextUtils.isEmpty(mName)){
                    Toast.makeText(MainActivity.this,"备注不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                HttpUtils.getInstance().saveLineTagInfo(0,0,mName, mRemark,currentLine.getId(),2,mLatitudeStr, mLongitudeStr, maintenanceLocalPath, new OnResponseListener() {
                    @Override
                    public void success(final retrofit2.Response responseMapBean) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ResponseBean resultBean = ( ResponseBean)responseMapBean.body();
                                if(resultBean.getResult() == 1){
                                    if(maintenanceDialog  != null && maintenanceDialog.isShowing()){
                                        maintenanceDialog.dismiss();
                                    }
                                    Log.d(TAG, "获取维修点id为:" + resultBean.getTagId());
                                    getSinglePoint(resultBean.getTagId()+"");
                                    getSingleLine(currentLine.getId()+"");
                                }else if(resultBean.getResult() == 2){
                                    if(null == resultBean.getDesc()||"".equals(resultBean.getDesc())){
                                        Toast.makeText(MainActivity.this,"提交失败",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(MainActivity.this,resultBean.getDesc(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }

                        @Override
                        public void fail(Throwable e) {
                            Log.d(TAG, "fail: "+e.getMessage());
                            Toast.makeText(MainActivity.this,"服务器出现问题，稍后重试！",Toast.LENGTH_LONG).show();
                        }
                    });

                break;
            case R.id.maintenance_take_pic:
                checkCameraPermission();
                break;
            case R.id.maintenance_pick_pic:
                choosePhoto();
                break;
            case R.id.maintenance_state:
                if(maintenanceDialog != null && maintenanceDialog.isShowing()){
                    maintenanceDialog.dismiss();
                }
                enterCheckActivity();
                break;
            case R.id.maintenance_locate:
                refreshLocation();
                if(currentLocation != null){
                    maintenanceLat.setText(String.valueOf(currentLocation.getLatitude()));
                    maintenanceLng.setText(String.valueOf(currentLocation.getLongitude()));
                }
                break;
                default:
        }
    }

    private void initImageData(){
        imageViews = new ArrayList<>();
        imageLocalPath = new ArrayList<>();
    }

    private void clearImageData(){
        imageViews.clear();
        imageLocalPath.clear();
        initImageData();
        adapter = null;
    }

    private void configRemoteData(List<Image> images){
        for (final Image cur: images) {
            ImageView current = new ImageView(MainActivity.this);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                    intent.putExtra("http",cur.getPath());
                    startActivity(intent);
                }
            });
            Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)).load(cur.getPath()).into(current);
            imageViews.add(current);
        }
        Log.d(TAG, "configRemoteData: "+ imageViews.size());
    }

    /**
     * 调用本地相机拍照功能
     */
    private String currentFileName = null;
    private void takePic(){
        // 跳转到系统的拍照界面
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentFileName = Common.getInstance().getFilePath(MainActivity.this);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(currentFileName)));
        if(currentImageType == TYPE_MAIN){
            startActivityForResult(intentToTakePhoto, REQUEST_CODE_TAKE_PICTURE);
        }else{
            startActivityForResult(intentToTakePhoto, REQUEST_CODE_MAINTENANCE_TAKE_PICTURE);
        }

    }

    private void checkCameraPermission(){
        Log.d(TAG, "=========================checkCameraPermission called!=========================");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "=========================checkCameraPermission called!111111111111111111111111=========================");
            int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED || storagePermission != PackageManager.PERMISSION_GRANTED) {
                //没有获取权限，发起申请
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 2);
            } else {
                //doing everything what you want
                takePic();
            }
        }else{
            takePic();
        }
    }

    /**
     * 调用本地相册选择图片功能
     */
    private void choosePhoto(){
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
        if(currentImageType == TYPE_MAIN){
            startActivityForResult(intentToPickPic, REQUEST_CODE_GALLERY);
        }else{
            startActivityForResult(intentToPickPic, REQUEST_CODE_MAINTENANCE_GALLERY);
        }
    }

    private EditText etLatitude;
    private EditText etLongitude;

    /**
     * 参数 id=标记点ID(如新增则为0), name=名称,tagNo=编号,location_lat=纬度,location_long=经度,type=类型,remark=备注
     * @param currentPoint
     */
    private void savePoint(Point currentPoint,List<String> imagePath){
        Log.d(TAG, "请求保存点接口");
        final String url = Constants.WEB_URL + Constants.TAG_SAVE_SINGLE_POINT;
        final Map<String,String> data = new HashMap<>();
        data.put("id",currentPoint.getId()+"");
        data.put("name",currentPoint.getName());
        data.put("tagNo",currentPoint.getTagNo());
        data.put("location_lat",currentPoint.getLocation_lat()+"");
        data.put("location_long",currentPoint.getLocation_long()+"");
        data.put("type",currentPoint.getType()+"");
        data.put("ceType",currentPoint.getCe_type()+"");
        data.put("leType",currentPoint.getLe_type()+"");
        data.put("remark",currentPoint.getRemark());

        Log.d(TAG, "savePoint data is:" + data.toString());
        OkHttpClientManager.getInstance().sendFileToServer(url, imagePath, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<ResponsePoint>>(){}.getType();
                    ResponseBean<ResponsePoint> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg("保存失败，原因是："+currentData.getDesc());
                        return;
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){
                        Log.d(TAG, "收到消息为："+ result);
                        Message message = Message.obtain();
                        message.obj = currentData.getId();
                        message.what = MSG_SAVE_SINGLE_POINT_END;
                        mHandler.sendMessage(message);
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

    private void getSinglePoint(String pointId){
        final String url = Constants.WEB_URL + Constants.TAG_GET_SINGLE_POINT;
        Log.d(TAG, "请求获取某个点接口" + url);
        final Map<String,String> data = new HashMap<>();
        data.put("id",pointId);
        Log.d(TAG, "getSinglePoint data is:" + data.toString());
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Type type = new TypeToken<ResponseBean<Point>>(){}.getType();
                    ResponseBean<Point> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg("保存失败，原因是："+currentData.getDesc());
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){
                        currentPoint = currentData.getObject();
                        Log.d(TAG, "收到消息为："+ result);
                        mHandler.sendEmptyMessage(MSG_GET_SINGLE_POINT_END);
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

    private void showAllLine(){
        for (Line current:lineList) {
            Log.d(TAG, "showAllLine: called");
            List<LatLng> latLngs = new ArrayList<>();

            if(current.getCheckPoints().isEmpty()){
                continue;
            }

            for (Point item:current.getCheckPoints()) {
                LatLng cur = new LatLng(item.getLocation_lat(),item.getLocation_long());
                latLngs.add(cur);
            }

            switch (current.getStatus()){
                case 0:
                case 1:
                    Polyline line1 = aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#4096FB")));
                    line1.setVisible(true);
                    break;
                case 2:
                    Polyline line2 = aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#73736A")));
                    line2.setVisible(true);
                    break;
                case 3:
                    Polyline line3 = aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#DC0A0A")));
                    line3.setVisible(true);
                    break;
                case 4:
                    Polyline line4 = aMap.addPolyline(new PolylineOptions().
                            addAll(latLngs).setDottedLine(true).width(20).color(Color.parseColor("#380C0B")));
                    line4.setVisible(true);
                    break;
            }
        }
    }

    private Polyline currentPolyLine = null;

    private void getSingleLine(String lineId){
        final String url = Constants.WEB_URL + Constants.TAG_GET_SINGLE_LINE;
        Log.d(TAG, "请求获取某条线接口" + url);
        final Map<String,String> data = new HashMap<>();
        data.put("id",lineId);
        Log.d(TAG, "getSingleLine data is:" + data.toString());
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();

                    Type type = new TypeToken<ResponseBean<Line>>(){}.getType();
                    ResponseBean<Line> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        sendFailureMsg("保存失败，原因是："+currentData.getDesc());
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){

                        currentLine = currentData.getObject();
                        Log.d(TAG, "收到消息为："+ result);
                        mHandler.sendEmptyMessage(MSG_GET_SINGLE_LINE_END);
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

    private void showAllPoint(){

        if(markerList == null){
            markerList = new ArrayList<>();
        }else{
            markerList.clear();
        }

        if (searchData == null){
            searchData = new ArrayList<>();
            searchDataMarkerMap = new HashMap<>();
        }else{
            searchData.clear();
            searchDataMarkerMap.clear();
        }

        for (Point current: pointList) {

            if(current.getType() == 101 && current.getCheckStatus() == 1){
                continue;
            }

            String cur = current.getName() + "," +current.getId();
            searchData.add(cur);
            Log.d(TAG, "showAllPoint: "+current.getLocation_lat()+","+current.getLocation_long());
            if(current.getLocation_lat() == 0.0 || current.getLocation_long() == 0.0){
                continue;
            }
            LatLng latLng = new LatLng(current.getLocation_lat(),current.getLocation_long());
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            String type = "";
            for (DataType currentType:pointTypeList
                 ) {
                if(currentType.getOptionValue() == current.getType()){
                    type = currentType.getOptionText();
                }
            }
            markerOption.title(current.getName()).snippet(type);

            markerOption.draggable(true);//设置Marker可拖动
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(true);//设置marker平贴地图效果
            Marker marker = aMap.addMarker(markerOption);

            switch (current.getCheckStatus()){
                case 0:
                case 1:
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.mark_bs)));
                    break;
                case 2:
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.mark_warn)));
                    break;
                case 3:
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.mark_repair_2)));
                    break;
                case 4:
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),R.mipmap.mark_repair_1)));
                    break;
            }

            Animation animation = new AlphaAnimation(0,1);
            long duration = 1000L;
            animation.setDuration(duration);
            animation.setInterpolator(new LinearInterpolator());

            marker.setAnimation(animation);
            marker.startAnimation();
            searchDataMarkerMap.put(cur,marker);
            markerList.add(marker);
        }

        String[] sData = new String[searchData.size()];

        searchData.toArray(sData);
        searchView.setSuggestions(sData);
    }

    private void addMarker(Point currentPoint){

        if(currentPoint.getType() == 101 && currentPoint.getCheckStatus() == 1){
            Log.d(TAG, "维修点已经修复！");
            return;
        }

        LatLng latLng = new LatLng(currentPoint.getLocation_lat(),currentPoint.getLocation_long());
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);

        String type = "";
        for (DataType currentType:pointTypeList
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

        switch (currentPoint.getCheckStatus()){
            case 0:
            case 1:
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.mipmap.mark_bs)));
                break;
            case 2:
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.mipmap.mark_warn)));
                break;
            case 3:
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.mipmap.mark_repair_2)));
                break;
            case 4:
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.mipmap.mark_repair_1)));
                break;
        }

        currentMarker.showInfoWindow();
        Log.d(TAG, "current point is:" + currentPoint.getLocation_long()+" and "+currentPoint.getLocation_lat());
        if(aMap.getMaxZoomLevel() >= 20){
            Log.d(TAG, "addMarker: haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            currentMarker.setDraggable(true);
        }else{

        }

//        Toast.makeText(this,"采集点操作成功！",Toast.LENGTH_LONG).show();
    }
    private BottomSheetDialog pointDialog = null;
    private BottomSheetDialog lineDialog = null;
    private BottomSheetDialog maintenanceDialog = null;

    private EditText etName = null;
    private EditText etOther = null;
    private Spinner spinnerType = null;
    private Spinner spinnerTerminalType = null;
    private Spinner spinnerLineType = null;
    private  Button confirmBtn = null;
    private  Button naviBtn = null;
    private Button locateBtn = null;
    private Button takePicBtn = null;
    private Button choosePicBtn = null;
    private Button stateBtn = null;
    private ViewPager pager = null;
    private TextView tvEmpty = null;
    private List<ImageView> imageViews = new ArrayList<>();
    private int pagerWidth;
    // 照片所在的Uri地址
    private Uri imageUri;

    private void showPager(){

        if (imageViews.size() < 1){
            pager.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }else{
            pager.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }

        configPager();
    }

    private MyViewPagerAdapter adapter = null;
    private void configPager(){

        if(adapter == null){
            pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels);
            ViewGroup.LayoutParams lp = pager.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                lp.width = pagerWidth;
            }
            pager.setLayoutParams(lp);
            adapter = new MyViewPagerAdapter(imageViews);
            pager.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    private void showPointBottomDialog(LatLng current) {
        View view = getLayoutInflater().inflate(R.layout.popup_list2, null);
        if(pointDialog == null ){

            pointDialog = new BottomSheetDialog(this);

            pointDialog.setContentView(view);
            pointDialog.setCancelable(false);
            pointDialog.setCanceledOnTouchOutside(false);

            etName = view.findViewById(R.id.edit_name);
            etOther = view.findViewById(R.id.edit_other);

            etLatitude = view.findViewById(R.id.edit_latitude);
            etLongitude = view.findViewById(R.id.edit_longitude);
            locateBtn = view.findViewById(R.id.locate);
            takePicBtn = view.findViewById(R.id.btn_take_pic);
            choosePicBtn = view.findViewById(R.id.btn_pick_pic);

            spinnerType = view.findViewById(R.id.spinner_type);
            spinnerTerminalType = view.findViewById(R.id.spinner_terminal_type);
            spinnerLineType = view.findViewById(R.id.spinner_line_type);
            confirmBtn = view.findViewById(R.id.confirm);
            stateBtn = view.findViewById(R.id.state);
            naviBtn = view.findViewById(R.id.navi);

            tvEmpty = view.findViewById(R.id.empty_text);
            Button cancelBtn = view.findViewById(R.id.cancel);
            pager = view.findViewById(R.id.viewPager);

            currentType = 0;
            currentTerminalType = 0;
            currentLineType = 0;
            initSpinner(spinnerType,pointTypeList,CURRENT_TYPE_POINT);
            initSpinner(spinnerTerminalType,pointTerminalTypeList,CURRENT_TYPE_POINT_TERMINAL);
            initSpinner(spinnerLineType,pointLineTypeList,CURRENT_TYPE_POINT_LINE);

            stateBtn.setOnClickListener(this);
            confirmBtn.setOnClickListener(this);
            locateBtn.setOnClickListener(this);
            takePicBtn.setOnClickListener(this);
            choosePicBtn.setOnClickListener(this);
            naviBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
        }

        if(currentPoint != null){
            currentType = currentPoint.getType();
            currentTerminalType = currentPoint.getCe_type();
            currentLineType = currentPoint.getLe_type();
            Log.d(TAG, "showPointBottomDialog: current is:" + currentType);
            int index = 0;
            for (DataType item:pointTypeList) {
                if(item.getOptionValue() == currentType){
                    break;
                }
                index++;
            }

            if (index == pointTypeList.size()){
                index = 0;
            }

            spinnerType.setSelection(index);
            index = 0;
            for (DataType item:pointTerminalTypeList) {
                if(item.getOptionValue() == currentTerminalType){
                    break;
                }
                index++;
            }
            if (index == pointTerminalTypeList.size()){
                index = 0;
            }
            spinnerTerminalType.setSelection(index);
            index = 0;
            for (DataType item:pointLineTypeList) {
                if(item.getOptionValue() == currentLineType){
                    break;
                }
                index++;
            }
            if (index == pointLineTypeList.size()){
                index = 0;
            }
            spinnerLineType.setSelection(index);

            etName.setText(currentPoint.getName());
            etOther.setText(currentPoint.getRemark());

            if(isDrag){
                etLatitude.setText(String.valueOf(tempLat));
                etLongitude.setText(String.valueOf(tempLng));
            }else{
                etLatitude.setText(String.valueOf(currentPoint.getLocation_lat()));
                etLongitude.setText(String.valueOf(currentPoint.getLocation_long()));
            }

        }else{
            spinnerType.setSelection(0);
            spinnerTerminalType.setSelection(0);
            spinnerLineType.setSelection(0);
            etName.setText("");
            etOther.setText("");
            if(isDrag){
                etLatitude.setText(String.valueOf(tempLat));
                etLongitude.setText(String.valueOf(tempLng));
            }else{
                if(current != null){
                    etLatitude.setText(String.valueOf(current.latitude));
                    etLongitude.setText(String.valueOf(current.longitude));
                }else{
                    etLatitude.setText("");
                    etLongitude.setText("");
                }
            }
        }

        if (isAdd){
            confirmBtn.setText("添加");
            naviBtn.setVisibility(View.GONE);
            stateBtn.setVisibility(View.GONE);
            Common.getInstance().setEditTextTrue(etName);
            Common.getInstance().setEditTextTrue(etOther);
            Common.getInstance().setEditTextTrue(etLatitude);
            Common.getInstance().setEditTextTrue(etLongitude);
            takePicBtn.setVisibility(View.VISIBLE);
            choosePicBtn.setVisibility(View.VISIBLE);
            confirmBtn.setVisibility(View.VISIBLE);
            locateBtn.setVisibility(View.VISIBLE);
        }else{
            confirmBtn.setText("修改");
            naviBtn.setVisibility(View.VISIBLE);
            stateBtn.setVisibility(View.VISIBLE);
            if(isPointWrite){
                Common.getInstance().setEditTextTrue(etName);
                Common.getInstance().setEditTextTrue(etOther);
                Common.getInstance().setEditTextTrue(etLatitude);
                Common.getInstance().setEditTextTrue(etLongitude);
                takePicBtn.setVisibility(View.VISIBLE);
                choosePicBtn.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.VISIBLE);
                locateBtn.setVisibility(View.VISIBLE);
            }else{
                Common.getInstance().setEditTextFalse(etName);
                Common.getInstance().setEditTextFalse(etOther);
                Common.getInstance().setEditTextFalse(etLatitude);
                Common.getInstance().setEditTextFalse(etLongitude);
                takePicBtn.setVisibility(View.GONE);
                choosePicBtn.setVisibility(View.GONE);
                confirmBtn.setVisibility(View.GONE);
                locateBtn.setVisibility(View.GONE);
            }
        }

        showPager();
        Log.d(TAG, "showPointBottomDialog:show");
        pointDialog.show();
    }

    private void initSpinner(final Spinner spinner, List<DataType> dataList, int type){
        Log.d(TAG, "initSpinner: " + dataList.size());
        String[] typeData =new String[dataList.size()];
        for (int i=0;i<dataList.size();i++){
            typeData[i] = dataList.get(i).getOptionText();
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,typeData);
        spinner.setAdapter(adapter);
        switch (type){
            case CURRENT_TYPE_POINT:
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String currentText = (String)spinner.getSelectedItem();
                        Log.d(TAG, "onItemClick 1: " + currentText);
                        for (DataType cur : pointTypeList) {
                            if(cur.getOptionText().equals(currentText)){
                                currentType = cur.getOptionValue();
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case CURRENT_TYPE_POINT_TERMINAL:
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String currentText = (String)spinner.getSelectedItem();
                        Log.d(TAG, "onItemClick 2: " + currentText);
                        for (DataType cur : pointTerminalTypeList) {
                            if(cur.getOptionText().equals(currentText)){
                                currentTerminalType = cur.getOptionValue();
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case CURRENT_TYPE_POINT_LINE:
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String currentText = (String)spinner.getSelectedItem();
                        Log.d(TAG, "onItemClick 3: " + currentText);
                        for (DataType cur : pointLineTypeList) {
                            if(cur.getOptionText().equals(currentText)){
                                currentLineType = cur.getOptionValue();
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            default:
        }
    }

    private EditText etLineName;
    private EditText etLineType;
    private EditText etLineType2;
    private EditText etLineNum;
    private EditText etLineBelong;
    private EditText etLineRemark;
    private EditText etLineLocation;
    private EditText etLineIsUpload;
    private Button btnLineConfirm;
    private Button btnLineState;
    private Button btnLineCreatePoint;

    private void showLineBottomDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_line, null);
        if(lineDialog == null ){

            lineDialog = new BottomSheetDialog(this);

            lineDialog.setContentView(view);
            lineDialog.setCancelable(false);
            lineDialog.setCanceledOnTouchOutside(false);
//
            etLineName = view.findViewById(R.id.et_line_name);
            etLineNum = view.findViewById(R.id.et_line_num);
            etLineType = view.findViewById(R.id.et_line_type);
            etLineType2 = view.findViewById(R.id.et_line_type2);

            etLineBelong = view.findViewById(R.id.et_line_belong);
            etLongitude = view.findViewById(R.id.edit_longitude);
            etLineRemark = view.findViewById(R.id.et_line_remark);
            etLineLocation = view.findViewById(R.id.et_line_location);
            etLineIsUpload = view.findViewById(R.id.et_line_upload);

            Common.getInstance().setEditTextFalse(etLineName);
            Common.getInstance().setEditTextFalse(etLineType);
            Common.getInstance().setEditTextFalse(etLineType2);
            Common.getInstance().setEditTextFalse(etLineNum);
            Common.getInstance().setEditTextFalse(etLineBelong);
            Common.getInstance().setEditTextFalse(etLineRemark);
            Common.getInstance().setEditTextFalse(etLineLocation);
            Common.getInstance().setEditTextFalse(etLineIsUpload);

//            spinnerLineType = view.findViewById(R.id.spinner_line_type);
            btnLineConfirm = view.findViewById(R.id.btn_ok);
            btnLineState = view.findViewById(R.id.line_state);
            btnLineCreatePoint = view.findViewById(R.id.line_create_point);


            btnLineConfirm.setOnClickListener(this);
            btnLineState.setOnClickListener(this);
            btnLineCreatePoint.setOnClickListener(this);
        }
        etLineName.setText(currentLine.getName());
        etLineNum.setText(currentLine.getLineCount()+"");
        etLineRemark.setText(currentLine.getRemark());
        etLineType.setText(currentLine.getTypeStr());
        etLineType2.setText(currentLine.getPosTypeStr());
        lineDialog.show();
    }

    private ViewPager maintenancePager = null;
    private TextView maintenanceTvEmpty = null;
    private Button maintenanceBtnClose;
    private Button maintenanceBtnAdd;
    private Button maintenanceBtnState;
    private Button maintenanceBtnTakePic;
    private Button maintenanceBtnPickPic;
    private Button maintenanceBtnLocate;
    private List<ImageView> maintenanceImageViews = null;
    private List<String> maintenanceLocalPath = null;
    private EditText maintenanceLat;
    private EditText maintenanceLng;
    private TextView maintenanceLine;
    private EditText maintenanceName;
    private EditText maintenanceRemark;
    private MyViewPagerAdapter maintenanceAdapter = null;
    private LinearLayout lineLayout;
    private LinearLayout photoLayout;

    private void showMaintenanceDialog() {
        currentImageType = TYPE_MAINTENANCE;
        maintenanceImageViews = new ArrayList<>();
        maintenanceLocalPath = new ArrayList<>();
        maintenanceAdapter = null;
        View view = getLayoutInflater().inflate(R.layout.popup_maintenance, null);
        if(maintenanceDialog == null){

            maintenanceDialog = new BottomSheetDialog(this);

            maintenanceDialog.setContentView(view);
            maintenanceDialog.setCancelable(false);
            maintenanceDialog.setCanceledOnTouchOutside(false);

            maintenanceName = view.findViewById(R.id.maintenance_et_name);
            maintenanceRemark = view.findViewById(R.id.maintenance_et_remark);
            maintenanceLine = view.findViewById(R.id.maintenance_line);
            maintenanceTvEmpty = view.findViewById(R.id.maintenance_empty_text);
            maintenanceLat = view.findViewById(R.id.maintenance_edit_latitude);
            maintenanceLng = view.findViewById(R.id.maintenance_edit_longitude);
            maintenancePager = view.findViewById(R.id.maintenance_viewPager);
            maintenanceBtnClose = view.findViewById(R.id.maintenance_close);
            maintenanceBtnState = view.findViewById(R.id.maintenance_state);
            maintenanceBtnAdd = view.findViewById(R.id.maintenance_add);
            maintenanceBtnTakePic = view.findViewById(R.id.maintenance_take_pic);
            maintenanceBtnPickPic = view.findViewById(R.id.maintenance_pick_pic);
            maintenanceBtnLocate = view.findViewById(R.id.maintenance_locate);
            lineLayout = view.findViewById(R.id.maintenance_line_layout);
            photoLayout = view.findViewById(R.id.photo_layout);

            maintenanceBtnAdd.setOnClickListener(this);
            maintenanceBtnClose.setOnClickListener(this);
            maintenanceBtnState.setOnClickListener(this);
            maintenanceBtnTakePic.setOnClickListener(this);
            maintenanceBtnPickPic.setOnClickListener(this);
            maintenanceBtnLocate.setOnClickListener(this);
        }

        if(isMaintenanceAdd){
            maintenanceBtnAdd.setText("添加");
            maintenanceBtnState.setVisibility(View.GONE);
            lineLayout.setVisibility(View.GONE);
            photoLayout.setVisibility(View.GONE);
            maintenanceName.setText("");
            maintenanceBtnAdd.setVisibility(View.VISIBLE);
            maintenanceBtnPickPic.setVisibility(View.GONE);
            maintenanceBtnTakePic.setVisibility(View.GONE);
            maintenanceBtnLocate.setVisibility(View.VISIBLE);
            Common.getInstance().setEditTextTrue(maintenanceName);
            Common.getInstance().setEditTextTrue(maintenanceLat);
            Common.getInstance().setEditTextTrue(maintenanceLng);
            Common.getInstance().setEditTextTrue(maintenanceRemark);

        }else{
            maintenanceBtnState.setVisibility(View.VISIBLE);
            lineLayout.setVisibility(View.VISIBLE);
            photoLayout.setVisibility(View.GONE);
            maintenanceBtnPickPic.setVisibility(View.GONE);
            maintenanceBtnAdd.setVisibility(View.GONE);
            maintenanceBtnTakePic.setVisibility(View.GONE);
            maintenanceBtnLocate.setVisibility(View.GONE);
            Common.getInstance().setEditTextFalse(maintenanceName);
            Common.getInstance().setEditTextFalse(maintenanceLat);
            Common.getInstance().setEditTextFalse(maintenanceLng);
            Common.getInstance().setEditTextFalse(maintenanceRemark);
            int lineId = currentPoint.getLineId();

            for (Line item:lineList) {
                if(item.getId() == lineId){
                    maintenanceLine.setText(item.getName());
                }
            }
            maintenanceName.setText(currentPoint.getName());
            maintenanceLat.setText(currentPoint.getLocation_lat()+"");
            maintenanceLng.setText(currentPoint.getLocation_long()+"");

            if(currentPoint.getImages() != null && currentPoint.getImages().size() > 0){
                for (final Image cur: currentPoint.getImages()) {
                    ImageView current = new ImageView(MainActivity.this);
                    current.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                            intent.putExtra("http",cur.getPath());
                            startActivity(intent);
                        }
                    });
                    Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)).load(cur.getPath()).into(current);
                    maintenanceImageViews.add(current);
                }
                Log.d(TAG, "configRemoteData: "+ maintenanceImageViews.size());
            }
        }

        showMaintenancePager();
        maintenanceDialog.show();
    }

    private void showMaintenancePager(){

        if (maintenanceImageViews.size() < 1){
            maintenancePager.setVisibility(View.GONE);
            maintenanceTvEmpty.setVisibility(View.VISIBLE);
            return;
        }else{
            maintenancePager.setVisibility(View.VISIBLE);
            maintenanceTvEmpty.setVisibility(View.GONE);
        }

        configMaintenancePager();
    }

    private void configMaintenancePager(){

        if(maintenanceAdapter == null){
            pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels);
            ViewGroup.LayoutParams lp = maintenancePager.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                lp.width = pagerWidth;
            }
            maintenancePager.setLayoutParams(lp);
            maintenanceAdapter = new MyViewPagerAdapter(maintenanceImageViews);
            maintenancePager.setAdapter(maintenanceAdapter);
        }else {
            maintenanceAdapter.notifyDataSetChanged();
        }
    }

    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    public void invokingGD(double lat, double lon){

        String params = "androidamap://navi?sourceApplication=amap&lat="+lat+"&lon="+lon+ "&dev=0";
        Log.d(TAG, "invokingGD: "+params);
        //  com.autonavi.minimap这是高德地图的包名
        Intent intent = new Intent("android.intent.action.VIEW",android.net.Uri.parse(params));
        intent.setPackage("com.autonavi.minimap");

        if(isInstallByread("com.autonavi.minimap")){
            startActivity(intent);
            Log.d(TAG, "高德地图客户端已经安装") ;
        }else{
            Toast.makeText(this, "没有安装高德地图客户端", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PICTURE:

                    Bitmap originalBmp = BitmapFactory.decodeFile(currentFileName);

                    final String oriPath = Common.getInstance().saveBitmap(MainActivity.this,originalBmp);
                    Log.d(TAG, "onActivityResult: path is" + oriPath+",angle is:" + Common.getInstance().readPictureDegree(oriPath));
                    imageLocalPath.add(oriPath);
                    ImageView cur = new ImageView(MainActivity.this);
                    cur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                            intent.putExtra("path",oriPath);
                            startActivity(intent);
                        }
                    });
                    cur.setImageBitmap(originalBmp);
                    imageViews.add(0,cur);
                    adapter = null;
                    showPager();
                    break;
                case REQUEST_CODE_GALLERY:
                    try {
                        //该uri是上一个Activity返回的
                        imageUri = data.getData();
                        if(imageUri!=null) {
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            final String path = Common.getInstance().saveBitmap(MainActivity.this,bit);
                            Log.d(TAG, "onActivityResult: path is" + path);
                            imageLocalPath.add(path);
                            ImageView choose = new ImageView(MainActivity.this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                                    intent.putExtra("path",path);
                                    startActivity(intent);
                                }
                            });
                            choose.setImageBitmap(bit);
                            imageViews.add(0,choose);
                            adapter = null;
                            showPager();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MaterialSearchView.REQUEST_VOICE:
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            searchView.setQuery(searchWrd, false);
                        }
                    }
                    break;
                case REQUEST_CODE_MAINTENANCE_TAKE_PICTURE:
                    Bitmap originalBmp1 = BitmapFactory.decodeFile(currentFileName);

                    final String path = Common.getInstance().saveBitmap(MainActivity.this,originalBmp1);
                    maintenanceLocalPath.add(path);
                    ImageView cur1 = new ImageView(MainActivity.this);
                    cur1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    });
                    cur1.setImageBitmap(originalBmp1);
                    maintenanceImageViews.add(0,cur1);
                    maintenanceAdapter = null;
                    showMaintenancePager();
                    break;
                case REQUEST_CODE_MAINTENANCE_GALLERY:
                    try {
                        //该uri是上一个Activity返回的
                        imageUri = data.getData();
                        if(imageUri!=null) {
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            final String path2 = Common.getInstance().saveBitmap(MainActivity.this,bit);
                            Log.d(TAG, "onActivityResult: path is" + path2);
                            maintenanceLocalPath.add(path2);
                            ImageView choose = new ImageView(MainActivity.this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this,ImageActivity.class);
                                    intent.putExtra("path",path2);
                                    startActivity(intent);
                                }
                            });
                            choose.setImageBitmap(bit);
                            maintenanceImageViews.add(0,choose);
                            maintenanceAdapter = null;
                            showMaintenancePager();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Constants.REQUEST_CODE:
                    int pointId = data.getIntExtra("pointId",-1);
                    Log.d(TAG, "onActivityResult: id:" + pointId);
                    if(pointId != -1){
                        getSinglePoint(pointId+"");
                    }
                    break;
                case Constants.REQUEST_LINE_CODE:
                    int lineId = data.getIntExtra("lineId",-1);
                    int pointId1 = data.getIntExtra("pointId",-1);
                    Log.d(TAG, "onActivityResult: 线id:" + lineId+",point id is:" + pointId1);
                    if(lineId != -1){
                        getSingleLine(lineId+"");
                    }
                    if(pointId1 != -1){
                        getSinglePoint(pointId1+"");
                    }
                    break;
                    default:
            }
        }
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
                    Toast.makeText(this, "应用未开启定位权限，请开启后重试", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:

                int length = grantResults.length;
                boolean isOk = false;
                for (int i = 0; i < length; i++) {
                    int permission = grantResults[i];
                    if(permission == PackageManager.PERMISSION_GRANTED){
                        isOk = true;
                    }else{
                        isOk = false;
                        break;
                    }
                }

                if(isOk){
                    takePic();
                }else {
                    //用户拒绝获取权限，则Toast出一句话提醒用户
                    Toast.makeText(this, "应用未开启拍照权限，请开启后重试", Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enterCheckActivity();
                } else {
                    //用户拒绝获取权限，则Toast出一句话提醒用户
                    Toast.makeText(this, "应用未开启权限，请开启后重试", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}