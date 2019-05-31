package map.test.testmap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.test.testmap.model.DataType;
import map.test.testmap.model.Image;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponsePoint;
import map.test.testmap.mvvm.ui.SearchActivity;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.MyViewPagerAdapter;
import map.test.testmap.utils.OkHttpClientManager;
import map.test.testmap.view.MultiSelectionSpinner;
import okhttp3.Response;

public class PointDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "point_detail";
    
    private EditText etName = null;
    private EditText etOther = null;
    private EditText etPhone = null;
    private EditText etPhone1 = null;
    private EditText etIP = null;
    private EditText etLatitude = null;
    private EditText etLongitude = null;
    private EditText etPointBelong= null;
    private EditText etPointLine = null;
    private EditText etIP2;
    private EditText etONU;
    private Spinner spinnerType = null;
    private Spinner spinnerTerminalType = null;
    private Spinner spinnerLineType = null;
    private MultiSelectionSpinner spinnerDeviceType = null;
    private Button confirmBtn = null;
    private Button pointHistoryBtn = null;
    private  Button naviBtn = null;
    private  Button cancelBtn = null;
    private Button locateBtn = null;
    private Button takePicBtn;
    private Button choosePicBtn;
    private Button stateBtn;
    private ViewPager pager;
    private TextView tvEmpty;
    private MyViewPagerAdapter adapter;
    private List<ImageView> imageViews = null;
    private int pagerWidth;
    // 照片所在的Uri地址
    private Uri imageUri;
    private Toolbar toolbar;
    private LinearLayout progressBar =null;

    private int pointId;
    private Point currentPoint = null;
    private List<String> imageLocalPath = null;
    private int currentType = 0;
    private int currentTerminalType = 0;
    private int currentLineType = 0;
    private int stationId = 0;
    private String currentDeviceType = "";
    public static final int CURRENT_TYPE_POINT = 1;
    public static final int CURRENT_TYPE_POINT_TERMINAL = 2;
    public static final int CURRENT_TYPE_POINT_LINE = 3;

    public static final int MSG_START = 100000;
    public static final int MSG_END = 100001;
    public static final int MSG_MSG_ERROR = 100002;
    public static final int MSG_MSG_FAILURE = 100003;
    public static final int MSG_SAVE_SINGLE_POINT_END = 100006;
    public static final int MSG_GET_SINGLE_POINT_END = 100007;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0;
    public final static int REQUEST_CODE_GALLERY = 1;
    public final static int REQUEST_CODE_SEARCH = 2;
    public final static int TYPE_LINE = 100008;
    public final static int TYPE_BDS = 100009;

    private int intentType;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START:
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "handleMessage: msg start");
                    break;
                case MSG_SAVE_SINGLE_POINT_END:

                    int id = (int) msg.obj;
                    Toast.makeText(PointDetailActivity.this,"保存成功！",Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("id",id);
//                    if(lineId != 0){
//                        resultIntent.putExtra("lineId",lineId);
//                    }
                    setResult(Activity.RESULT_OK,resultIntent);
                    finish();
                    break;
                case MSG_GET_SINGLE_POINT_END:
                    if(currentPoint.getImages() != null && currentPoint.getImages().size()>0){
                        Log.d(TAG, "handleMessage: "+ currentPoint.getImages().size());
                        configRemoteData(currentPoint.getImages());
                        showPager();
                    }
                    initState();
                    break;
                case MSG_MSG_ERROR:
                    String message = (String) msg.obj;
                    Log.d(TAG, "错误消息为:" + message);
                    Toast.makeText(PointDetailActivity.this,"服务器响应失败，请稍后重试！",Toast.LENGTH_LONG).show();
                    break;
                case MSG_MSG_FAILURE:
                    String failureMsg = (String) msg.obj;
                    Log.d(TAG, "失败消息为:" + failureMsg);
                    Toast.makeText(PointDetailActivity.this,failureMsg,Toast.LENGTH_LONG).show();
                    break;
                case MSG_END:
                    Log.d(TAG, "handleMessage: msg end");
                    progressBar.setVisibility(View.GONE);

                    break;
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyUp: ");
            Intent intent = new Intent();
            intent.putExtra("id",pointId);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointId = getIntent().getIntExtra("id",-1);
        intentType = getIntent().getIntExtra("type",-1);
        setContentView(R.layout.activity_point_detail);
        init();
    }

    private void init(){
        initView();
        new Thread(){
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_START);
                if(pointId != -1){
                    getSinglePoint(pointId+"");
                }else{
                    initState();
                }
                getAllType();

            }
        }.start();

    }

    private void checkCameraPermission(){
        Log.d(TAG, "=========================checkCameraPermission called!=========================");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "=========================checkCameraPermission called!111111111111111111111111=========================");
            int storagePermission = ContextCompat.checkSelfPermission(PointDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(PointDetailActivity.this, Manifest.permission.CAMERA);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED || storagePermission != PackageManager.PERMISSION_GRANTED) {
                //没有获取权限，发起申请
                ActivityCompat.requestPermissions(PointDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 2);
            } else {
                //doing everything what you want
                takePic();
            }
        }else{
            takePic();
        }
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar_point_detail);
        progressBar = findViewById(R.id.point_detail_loading);
        etName = findViewById(R.id.edit_name);
        etOther = findViewById(R.id.edit_other);
        etPhone = findViewById(R.id.edit_phone);
        etPhone1 = findViewById(R.id.edit_phone1);
        etIP = findViewById(R.id.edit_ip1);

        etLatitude = findViewById(R.id.edit_latitude);
        etLongitude = findViewById(R.id.edit_longitude);
        etPointBelong = findViewById(R.id.point_belong);
        etPointLine = findViewById(R.id.point_line);
        locateBtn = findViewById(R.id.locate);
        takePicBtn = findViewById(R.id.btn_take_pic);
        choosePicBtn = findViewById(R.id.btn_pick_pic);

        etIP2 = findViewById(R.id.edit_ip2);
        etONU = findViewById(R.id.et_onu);

        spinnerType = findViewById(R.id.spinner_type);
        spinnerTerminalType = findViewById(R.id.spinner_terminal_type);
        spinnerLineType = findViewById(R.id.spinner_line_type);
        spinnerDeviceType = findViewById(R.id.spinner_device_type);
        confirmBtn = findViewById(R.id.confirm);
        stateBtn = findViewById(R.id.state);
        naviBtn = findViewById(R.id.navi);
        cancelBtn = findViewById(R.id.cancel);

        pointHistoryBtn = findViewById(R.id.point_history);

        tvEmpty = findViewById(R.id.empty_text);
        pager = findViewById(R.id.viewPager);

        imageLocalPath = new ArrayList<>();
        imageViews = new ArrayList<>();

        Common.getInstance().setEditTextFalse(etPointBelong);

        initLocateInfo();

        if(intentType == 1234){
            Common.getInstance().setEditTextFalse(etName);
            Common.getInstance().setEditTextFalse(etOther);
            Common.getInstance().setEditTextFalse(etIP);
            Common.getInstance().setEditTextFalse(etLatitude);
            Common.getInstance().setEditTextFalse(etLongitude);
            Common.getInstance().setEditTextFalse(etPhone);
            Common.getInstance().setEditTextFalse(etPhone1);

            confirmBtn.setVisibility(View.GONE);
            stateBtn.setVisibility(View.GONE);
            naviBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            pointHistoryBtn.setVisibility(View.GONE);
            locateBtn.setVisibility(View.GONE);
            takePicBtn.setVisibility(View.GONE);
            choosePicBtn.setVisibility(View.GONE);
            etPointBelong.setOnClickListener(null);
        }
    }

    private void initSpinner(final Spinner spinner, List<DataType> dataList, int type){

        if (dataList == null){
            return;
        }

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
                        TextView tv = (TextView)view;
                        tv.setTextSize(15.0f);    //设置大小
                        String currentText = (String)spinner.getSelectedItem();
                        Log.d(TAG, "onItemClick 1: " + currentText);
                        for (DataType cur : Constants.pointTypeList) {
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
                        TextView tv = (TextView)view;
                        tv.setTextSize(15.0f);    //设置大小
                        String currentText = (String)spinner.getSelectedItem();
                        Log.d(TAG, "onItemClick 2: " + currentText);
                        for (DataType cur : Constants.pointTerminalTypeList) {
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
                        TextView tv = (TextView)view;
                        tv.setTextSize(15.0f);    //设置大小
                        String currentText = (String)spinner.getSelectedItem();
                        Log.d(TAG, "onItemClick 3: " + currentText);
                        for (DataType cur : Constants.pointLineTypeList) {
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

    private void initState(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stateBtn.setVisibility(View.GONE);
        naviBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        pointHistoryBtn.setVisibility(View.GONE);

        if(pointId == -1){
            confirmBtn.setText("添加");
        }else{
            confirmBtn.setText("修改");
        }

        currentType = 0;
        currentTerminalType = 0;
        currentLineType = 0;
        initSpinner(spinnerType,Constants.pointTypeList,CURRENT_TYPE_POINT);
        initSpinner(spinnerTerminalType,Constants.pointTerminalTypeList,CURRENT_TYPE_POINT_TERMINAL);
        initSpinner(spinnerLineType,Constants.pointLineTypeList,CURRENT_TYPE_POINT_LINE);

        currentDeviceType = "";
        String[] typeData =new String[Constants.pointDeviceTypeList.size()];
        for (int i=0;i<Constants.pointDeviceTypeList.size();i++){
            typeData[i] = Constants.pointDeviceTypeList.get(i).getOptionText();
        }
        spinnerDeviceType.setItems(typeData);
        spinnerDeviceType.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {

            }

            @Override
            public void selectedStrings(List<String> strings) {
                currentDeviceType = "";

                if(strings.size() > 0){
                    String[] result = new String[strings.size()];
                    int i = 0;
                    for (String item:
                            strings) {
                        for (DataType type: Constants.pointDeviceTypeList){
                            if(item == type.getOptionText()){
                                result[i] = type.getOptionValue()+"";
                                i++;
                            }
                        }
                    }

                    currentDeviceType = Common.getInstance().combine(result);
                    Log.d(TAG, "selectedStrings: "+currentDeviceType);
                }

            }
        });

        confirmBtn.setOnClickListener(this);
        locateBtn.setOnClickListener(this);
        takePicBtn.setOnClickListener(this);
        choosePicBtn.setOnClickListener(this);
        etPointBelong.setOnClickListener(this);

        if(pointId != -1 && currentPoint != null){
            currentType = currentPoint.getType();
            currentTerminalType = currentPoint.getCe_type();
            currentLineType = currentPoint.getLe_type();
            Log.d(TAG, "showPointBottomDialog: current is:" + currentType);
            int index = 0;
            for (DataType item:Constants.pointTypeList) {
                if(item.getOptionValue() == currentType){
                    break;
                }
                index++;
            }

            if (index == Constants.pointTypeList.size()){
                index = 0;
            }

            spinnerType.setSelection(index);
            index = 0;
            for (DataType item:Constants.pointTerminalTypeList) {
                if(item.getOptionValue() == currentTerminalType){
                    break;
                }
                index++;
            }
            if (index == Constants.pointTerminalTypeList.size()){
                index = 0;
            }
            spinnerTerminalType.setSelection(index);
            index = 0;
            for (DataType item:Constants.pointLineTypeList) {
                if(item.getOptionValue() == currentLineType){
                    break;
                }
                index++;
            }
            if (index == Constants.pointLineTypeList.size()){
                index = 0;
            }
            spinnerLineType.setSelection(index);

            String devicesItems = currentPoint.getMtype();

            if(devicesItems != null && !"".equals(devicesItems)){
                String[] result = Common.getInstance().splitStr(devicesItems);

                String[] indexStr = new String[result.length];
                if(result.length > 0){
                    int i = 0;
                    for (String item:result){
                        for (DataType cur:Constants.pointDeviceTypeList) {
                            if(cur.getOptionValue() == Integer.valueOf(item)){
                                indexStr[i] = cur.getOptionText();
                                i++;
                                break;
                            }
                        }
                    }
                }
                spinnerDeviceType.setSelection(indexStr);
            }else{
                String[] indexStr = new String[1];
                indexStr[0] = Constants.pointDeviceTypeList.get(0).getOptionText();
                spinnerDeviceType.setSelection(indexStr);
                spinnerDeviceType.setSelection(0);
            }

            etName.setText(currentPoint.getName());
            etOther.setText(currentPoint.getRemark());
            etPhone.setText(currentPoint.getPhone());
            etPhone1.setText(currentPoint.getPhone1());
            etIP.setText(currentPoint.getIp());
            etIP2.setText(currentPoint.getIp2());
            etONU.setText(currentPoint.getIp_onu());
            etPointLine.setText(currentPoint.getL_name());
            etPointBelong.setText(currentPoint.getStationName());

            etLatitude.setText(String.valueOf(currentPoint.getLocation_lat()));
            etLongitude.setText(String.valueOf(currentPoint.getLocation_long()));

        }else{
            spinnerType.setSelection(0);
            spinnerTerminalType.setSelection(0);
            spinnerLineType.setSelection(0);
            String[] indexStr = new String[1];
            indexStr[0] = Constants.pointDeviceTypeList.get(0).getOptionText();
            spinnerDeviceType.setSelection(indexStr);
            spinnerDeviceType.setSelection(0);
            etName.setText("");
            etOther.setText("");
            etPhone.setText("");
            etPhone1.setText("");
            etIP.setText("");
            etIP2.setText("");
            etONU.setText("");
            etLatitude.setText("");
            etLongitude.setText("");
            etPointBelong.setText("");
            etPointLine.setText("");
        }

        showPager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_pic:
                checkCameraPermission();
                break;
            case R.id.btn_pick_pic:
                choosePhoto();
                break;
            case R.id.confirm:
                //判断名称
                String name = etName.getText().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(PointDetailActivity.this,"名称不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断经度
                String longitudeStr = etLongitude.getText().toString();
                if(TextUtils.isEmpty(longitudeStr)){
                    Toast.makeText(PointDetailActivity.this,"经度不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
                    return;
                }

                //判断纬度
                String latitudeStr = etLatitude.getText().toString();
                if(TextUtils.isEmpty(latitudeStr)){
                    Toast.makeText(PointDetailActivity.this,"纬度不能为空，请输入后再提交！",Toast.LENGTH_LONG).show();
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
                    currentPoint.setMtype(currentDeviceType);
                    currentPoint.setTagNo("");
                    currentPoint.setRemark(remark);
                    if(!TextUtils.isEmpty(etPhone.getText().toString())){
                        currentPoint.setPhone(etPhone.getText().toString());
                    }else{
                        currentPoint.setPhone("");
                    }
                    if(!TextUtils.isEmpty(etPhone1.getText().toString())){
                        currentPoint.setPhone1(etPhone1.getText().toString());
                    }else{
                        currentPoint.setPhone1("");
                    }
                    if(!TextUtils.isEmpty(etIP.getText().toString())){
                        currentPoint.setIp(etIP.getText().toString());
                    }else{
                        currentPoint.setIp("");
                    }
                    if(!TextUtils.isEmpty(etIP2.getText().toString())){
                        currentPoint.setIp2(etIP2.getText().toString());
                    }else{
                        currentPoint.setIp2("");
                    }

                    if(!TextUtils.isEmpty(etONU.getText().toString())){
                        currentPoint.setIp_onu(etONU.getText().toString());
                    }else{
                        currentPoint.setIp_onu("");
                    }

                    if(!TextUtils.isEmpty(etPointLine.getText().toString())){
                        currentPoint.setL_name(etPointLine.getText().toString());
                    }else{
                        currentPoint.setL_name("");
                    }
                    currentPoint.setLocation_lat(latitude);
                    currentPoint.setLocation_long(longitude);
                }else{
                    currentPoint.setName(name);
                    currentPoint.setType(currentType);
                    currentPoint.setCe_type(currentTerminalType);
                    currentPoint.setLe_type(currentLineType);
                    currentPoint.setMtype(currentDeviceType);
                    currentPoint.setRemark(remark);
                    currentPoint.setLocation_lat(latitude);
                    currentPoint.setLocation_long(longitude);
                    if(!TextUtils.isEmpty(etPhone.getText().toString())){
                        currentPoint.setPhone(etPhone.getText().toString());
                    }else{
                        currentPoint.setPhone("");
                    }
                    if(!TextUtils.isEmpty(etPhone1.getText().toString())){
                        currentPoint.setPhone1(etPhone1.getText().toString());
                    }else{
                        currentPoint.setPhone1("");
                    }
                    if(!TextUtils.isEmpty(etIP.getText().toString())){
                        currentPoint.setIp(etIP.getText().toString());
                    }else{
                        currentPoint.setIp("");
                    }

                    if(!TextUtils.isEmpty(etIP2.getText().toString())){
                        currentPoint.setIp2(etIP2.getText().toString());
                    }else{
                        currentPoint.setIp2("");
                    }

                    if(!TextUtils.isEmpty(etONU.getText().toString())){
                        currentPoint.setIp_onu(etONU.getText().toString());
                    }else{
                        currentPoint.setIp_onu("");
                    }

                    if(!TextUtils.isEmpty(etPointLine.getText().toString())){
                        currentPoint.setL_name(etPointLine.getText().toString());
                    }else{
                        currentPoint.setL_name("");
                    }
                }

                savePoint(currentPoint,imageLocalPath);
                break;
            case R.id.locate:
                startLocation();
                break;
            case R.id.point_belong:
                enterQueryActivity(TYPE_BDS);
                break;
            case R.id.point_line:
//                enterQueryActivity(TYPE_LINE);
                break;
        }

    }

    private void enterQueryActivity(int type){
        Intent queryIntent = new Intent(PointDetailActivity.this,SearchActivity.class);
        queryIntent.putExtra("type",type);
        startActivityForResult(queryIntent,REQUEST_CODE_SEARCH);
    }

    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private void initLocateInfo(){
        //声明mlocationClient对象

        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    etLatitude.setText(aMapLocation.getLatitude()+"");
                    etLongitude.setText(aMapLocation.getLongitude()+"");
                }
            }
        });
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
    }

    private void startLocation(){
        if(mlocationClient != null){
            mlocationClient.stopLocation();
            mlocationClient.startLocation();
        }
    }

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

        if(currentPoint.getTagNo() != null){
            data.put("tagNo",currentPoint.getTagNo());
        }

        data.put("location_lat",currentPoint.getLocation_lat()+"");
        data.put("location_long",currentPoint.getLocation_long()+"");
        data.put("type",currentPoint.getType()+"");
        data.put("ceType",currentPoint.getCe_type()+"");
        data.put("leType",currentPoint.getLe_type()+"");
        data.put("remark",currentPoint.getRemark());
        data.put("mtypes",currentPoint.getMtype());
        data.put("tagIp",currentPoint.getIp());
        data.put("tagPhone",currentPoint.getPhone());
        data.put("tagPhone1",currentPoint.getPhone1());
        data.put("stationId",stationId+"");
        data.put("l_name",currentPoint.getL_name());
        data.put("tagIp1",currentPoint.getIp2());
        data.put("ip_onu",currentPoint.getIp_onu());

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

    /**
     * 调用本地相册选择图片功能
     */
    private void choosePhoto(){
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
        startActivityForResult(intentToPickPic, REQUEST_CODE_GALLERY);
    }

    private void configRemoteData(List<Image> images){
        for (final Image cur: images) {
            ImageView current = new ImageView(this);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PointDetailActivity.this,ImageActivity.class);
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
        currentFileName = Common.getInstance().getFilePath(this);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(currentFileName)));
        startActivityForResult(intentToTakePhoto, REQUEST_CODE_TAKE_PICTURE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id",pointId);
                setResult(Activity.RESULT_OK,resultIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void showPager(){

        Log.d(TAG, "showPager: "+ imageViews.size());
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

    /**
     * 获取所有的类型信息
     */
    private void getAllType(){
        if(Constants.pointTypeList == null){
            getPointType();
        }

        if(Constants.pointTerminalTypeList == null){
            getPointTerminalType();
        }

        if(Constants.pointLineTypeList == null){
            getPointLineType();
        }

        if(Constants.pointDeviceTypeList == null){
            getDeviceType();
        }
        mHandler.sendEmptyMessage(MSG_END);
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
                        Constants.pointTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点类型数量是：" + Constants.pointTypeList.size());
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
                        Constants.pointTerminalTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点终端类型数量是：" + Constants.pointTerminalTypeList.size());
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
                        Constants.pointLineTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点光缆类型数量是：" + Constants.pointLineTypeList.size());
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

    private void getDeviceType(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_DEVICE_TYPE;
        Log.d(TAG, "请求获取点设备类型接口"+ url);
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
                        Constants.pointDeviceTypeList = currentData.getList();
                        Log.d(TAG, "收到消息为："+ result+"\n 当前标记点设备类型数量是：" + Constants.pointDeviceTypeList.size());
                        mHandler.sendEmptyMessage(MSG_END);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PICTURE:

                    Bitmap originalBmp = BitmapFactory.decodeFile(currentFileName);

                    final String oriPath = Common.getInstance().saveBitmap(this,originalBmp);
                    Log.d(TAG, "onActivityResult: path is" + oriPath+",angle is:" + Common.getInstance().readPictureDegree(oriPath));
                    imageLocalPath.add(oriPath);
                    ImageView cur = new ImageView(this);
                    cur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(PointDetailActivity.this,ImageActivity.class);
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
                            final String path = Common.getInstance().saveBitmap(this,bit);
                            Log.d(TAG, "onActivityResult: path is" + path);
                            imageLocalPath.add(path);
                            ImageView choose = new ImageView(this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PointDetailActivity.this,ImageActivity.class);
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
                case REQUEST_CODE_SEARCH:
                    int id = data.getIntExtra("id",-1);
                    int type = data.getIntExtra("type",-1);
                    String typeStr = data.getStringExtra("str");
                    if(id > -1 && type > -1){
                        switch (type){
                            case TYPE_BDS:
                                stationId = id;
                                etPointBelong.setText(typeStr);
                                break;
                            case TYPE_LINE:
//                                lineId = id;
//                                etPointLine.setText(typeStr);
                                break;
                        }
                    }
                    break;
                default:
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mlocationClient != null){
            mlocationClient.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //通过请求码进行筛选
        switch (requestCode) {
            case 2:
                Log.d(TAG, "onRequestPermissionsResult: 2");
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
                    Toast.makeText(PointDetailActivity.this, "应用未开启拍照权限，请开启后重试", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}