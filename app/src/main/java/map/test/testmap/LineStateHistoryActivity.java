package map.test.testmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import map.test.testmap.model.Image;
import map.test.testmap.model.LineState;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseCheckHistory;
import map.test.testmap.model.State;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import retrofit2.Response;

public class LineStateHistoryActivity extends AppCompatActivity implements View.OnClickListener,LineStateListFragment.OnTouchListener {

    private static final String TAG = "LineHistory";
    public static final int REQUEST_CODE_TAKE_PICTURE = 0;
    public final static int REQUEST_CODE_GALLERY = 1;

    private TextView checkName;
    private TextView checkTime;
    private TextView checkState;
    private EditText checkRemark;
    private TextView checkLat;
    private TextView checkLong;

    private Button btnModifyNormal;
    private Button btnModifySerious;
    private LineState currentState;
    private Button btnFinish;
    private Button btnTakePic;
    private Button btnSelectPic;

    private ViewPager pager = null;
    private TextView tvEmpty = null;
    private List<ImageView> imageViews = null;
    private int pagerWidth;

    private List<String> imageLocalPath = null;
    private List<String> imageRemotePath = null;

    private Toolbar toolbar;
    private LinearLayout loadingLayout;

    private int lineId;

    private int lastId;
    private int pointId;
    private boolean isModify;
    private boolean isVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_state_history);
        lineId = getIntent().getIntExtra("line_id",-1);
        isModify = getIntent().getBooleanExtra("line_modify",false);
        isVerify = getIntent().getBooleanExtra("line_verify",false);

        Log.d(TAG, "收到的的线id为：" + lineId);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        toolbar = findViewById(R.id.id_toolbar);
        checkName = findViewById(R.id.et_name);
        checkTime = findViewById(R.id.et_time);
        checkState = findViewById(R.id.sp_state);
        checkRemark = findViewById(R.id.et_remark);
        checkLat = findViewById(R.id.check_lat);
        checkLong = findViewById(R.id.check_long);
        tvEmpty = findViewById(R.id.check_empty_text);
        pager = findViewById(R.id.check_viewPager);
        loadingLayout = findViewById(R.id.loading);

        btnModifyNormal = findViewById(R.id.check_modify_normal);
        btnModifyNormal.setOnClickListener(this);
        btnModifySerious = findViewById(R.id.check_modify_serious);
        btnModifySerious.setOnClickListener(this);
        btnFinish = findViewById(R.id.check_finish);
        btnFinish.setOnClickListener(this);
        btnTakePic = findViewById(R.id.check_take_pic);
        btnTakePic.setOnClickListener(this);
        btnSelectPic = findViewById(R.id.check_pick_pic);
        btnSelectPic.setOnClickListener(this);
        initData();

    }

    private void setLayoutFalse(){
        btnTakePic.setVisibility(View.GONE);
        btnSelectPic.setVisibility(View.GONE);
        btnModifySerious.setVisibility(View.GONE);
        btnModifyNormal.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
        Common.getInstance().setEditTextFalse(checkRemark);
    }

    private void initData(){

        setLayoutFalse();
        initImageData();
        showPager();
        initToolBar();
        getDataFromServer();
    }

    private void showLoading(boolean isShow){
        if(isShow){
            loadingLayout.setVisibility(View.VISIBLE);
        }else{
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void initToolBar(){
        toolbar.setTitle("维修信息");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.line_state,menu);
        return true;
    }

    private void getDataFromServer(){
        showLoading(true);
        HttpUtils.getInstance().getLineCheckHistoryInfo(lineId, new OnResponseListener() {
            @Override
            public void success(final Response responseMapBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBean<ResponseCheckHistory> data = ( ResponseBean<ResponseCheckHistory>)responseMapBean.body();

                        if(data.getResult() == 2){
                            stateList = new ArrayList<>();
                            showListFragment();
                        }else if(data.getResult() == 1){
                            stateList = new ArrayList<>();

                            if(data.getList() != null && data.getList().size() > 0){
                                for (ResponseCheckHistory history: data.getList()) {


                                    String name = history.getTag().getName();
                                    double lat = history.getTag().getLocation_lat();
                                    double lng = history.getTag().getLocation_long();
                                    int checkId = history.getCheckId();

                                    if(history.getList().size() > 0){

                                        for (State item1:history.getList()) {
                                            LineState item = new LineState();
                                            item.setCheckId(checkId);
                                            item.setLatitude(lat);
                                            item.setLongitude(lng);
                                            item.setPointName(name);
                                            item.setId(item1.getId());
                                            item.setCreateTime(item1.getCreateTime());
                                            item.setImageList(item1.getImageList());
                                            item.setCreateUserName(item1.getCreateUserName());
                                            item.setModifyTime(item1.getModifyTime());
                                            item.setName(item1.getName());
                                            item.setRemark(item1.getRemark());
                                            item.setTagId(item1.getTagId());
                                            item.setModifyUserName(item1.getModifyUserName());
                                            item.setStatus(item1.getStatus());
                                            item.setStatusStr(item1.getStatusStr());
                                            stateList.add(item);
                                        }

                                    }
                                }
                                lastId = stateList.get(0).getId();
                                pointId = stateList.get(0).getTagId();

                                setData(stateList.get(0));
                            }

                            showListFragment();
                        }
                        showLoading(false);
                    }
                });
            }

            @Override
            public void fail(final Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        Toast.makeText(LineStateHistoryActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void saveCheckInfo(int status,String remark,List<String> imagePath){
        Log.d(TAG, "saveCheckInfo: "+" checkId:"+currentState.getCheckId()+",tag id:"+currentState.getTagId());
        HttpUtils.getInstance().saveCheckInfo(currentState.getTagId(), currentState.getCheckId(),status,remark,imagePath, new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                ResponseBean data = ( ResponseBean)responseMapBean.body();

                if(data.getResult() == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LineStateHistoryActivity.this,"提交成功",Toast.LENGTH_LONG).show();
                            setLayoutFalse();
                            getDataFromServer();
                        }
                    });
                }else if(data.getResult() == 2){
                    if(null == data.getDesc()||"".equals(data.getDesc())){
                        Toast.makeText(LineStateHistoryActivity.this,"提交失败",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(LineStateHistoryActivity.this,data.getDesc(),Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                Toast.makeText(LineStateHistoryActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showListFragment(){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_list,LineStateListFragment.newInstance(stateList)).commit();
    }

    private ArrayList<LineState> stateList;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check_modify_normal:
                String remark1 = checkRemark.getText().toString();
                saveCheckInfo(4,remark1,imageLocalPath);
                break;
            case R.id.check_modify_serious:
                String remark2 = checkRemark.getText().toString();
                saveCheckInfo(3,remark2,imageLocalPath);
                break;
            case R.id.check_finish:
                String remark3 = checkRemark.getText().toString();
                if(btnFinish.getText().toString().equals("修复完成")){
                    saveCheckInfo(5,remark3,imageLocalPath);
                }else if(btnFinish.getText().toString().equals("审核通过")){
                    saveCheckInfo(1,remark3,imageLocalPath);
                }
                break;
            case R.id.check_take_pic:
                checkCameraPermission();
                break;
            case R.id.check_pick_pic:
                choosePhoto();
                break;
        }
    }

    @Override
    public void setData(LineState current) {
        currentState = current;
        clearImageData();
        checkName.setText(current.getCreateUserName());
        checkTime.setText(current.getCreateTime());
        checkState.setText(current.getStatusStr());
        checkRemark.setText(current.getRemark());
        checkLat.setText(current.getLatitude()+"");
        checkLong.setText(current.getLongitude()+"");
        List<Image> images = current.getImageList();
        if(null != images && !images.isEmpty()){
            configRemoteData(images);
        }
        showPager();

        if(lastId == current.getId()){
            Common.getInstance().setEditTextTrue(checkRemark);
            btnTakePic.setVisibility(View.VISIBLE);
            btnSelectPic.setVisibility(View.VISIBLE);
            btnModifySerious.setVisibility(View.VISIBLE);
            btnModifyNormal.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.VISIBLE);
            switch (current.getStatus()){
                case 1:
                    setLayoutFalse();
                    break;
                case 3:
                case 4:
                    if(isModify){
                        btnModifySerious.setVisibility(View.GONE);
                        btnModifyNormal.setVisibility(View.GONE);
                        btnFinish.setText("修复完成");
                    }else{
                        setLayoutFalse();
                        Toast.makeText(LineStateHistoryActivity.this,"当前无修复权限",Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    if(isVerify){
                        btnModifySerious.setVisibility(View.VISIBLE);
                        btnModifyNormal.setVisibility(View.VISIBLE);
                        btnFinish.setVisibility(View.VISIBLE);
                        btnFinish.setText("审核通过");
                    }else{
                        setLayoutFalse();
                        Toast.makeText(LineStateHistoryActivity.this,"当前无审核权限",Toast.LENGTH_LONG).show();
                    }
                    break;
            }

        }else{
            setLayoutFalse();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyUp: ");
            Intent intent = new Intent();
            intent.putExtra("lineId",lineId);
            intent.putExtra("pointId",pointId);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return false;
    }

    private void configRemoteData(List<Image> images){
        for (final Image cur: images) {
            ImageView current = new ImageView(LineStateHistoryActivity.this);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LineStateHistoryActivity.this,ImageActivity.class);
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
        Log.d(TAG, "takePic: ");
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentFileName = Common.getInstance().getFilePath(LineStateHistoryActivity.this);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(currentFileName)));
        startActivityForResult(intentToTakePhoto, REQUEST_CODE_TAKE_PICTURE);
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
        startActivityForResult(intentToPickPic, REQUEST_CODE_GALLERY);
    }

    private Uri imageUri;

    private void initImageData(){
        imageRemotePath = new ArrayList<>();
        imageViews = new ArrayList<>();
        imageLocalPath = new ArrayList<>();
    }

    private void clearImageData(){
        imageRemotePath.clear();
        imageViews.clear();
        imageLocalPath.clear();
        initImageData();
        adapter = null;
    }

    private void showPager(){

        if (imageViews.size() < 1){
            pager.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }else{
            pager.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            configPager(pager);
        }
    }

    private MyViewPagerAdapter adapter = null;
    private void configPager(ViewPager pager){

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //通过请求码进行筛选
        switch (requestCode) {
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
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PICTURE:

                    Bitmap originalBmp = BitmapFactory.decodeFile(currentFileName);
                    final String oriPath = Common.getInstance().saveBitmap(LineStateHistoryActivity.this,originalBmp);
                    Log.d(TAG, "onActivityResult: path is" + oriPath);
                    imageLocalPath.add(oriPath);
                    ImageView cur = new ImageView(LineStateHistoryActivity.this);
                    cur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LineStateHistoryActivity.this,ImageActivity.class);
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
                            final String path = Common.getInstance().saveBitmap(LineStateHistoryActivity.this,bit);
                            Log.d(TAG, "onActivityResult: path is" + path);
                            imageLocalPath.add(path);
                            ImageView choose = new ImageView(LineStateHistoryActivity.this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LineStateHistoryActivity.this,ImageActivity.class);
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
                default:
            }
        }
    }
}