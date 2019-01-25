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
import android.support.design.widget.BottomSheetDialog;
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
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseCheckHistory;
import map.test.testmap.model.State;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import retrofit2.Response;

public class StateActivity extends AppCompatActivity implements StateListFragment.OnTouchListener,View.OnClickListener {

    private static final String TAG = "StateActivity";
    public static final int REQUEST_CODE_TAKE_PICTURE = 0;
    public final static int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_TYPE_MAIN = 2;
    public final static int REQUEST_TYPE_DIALOG = 3;
    public static final int REQUEST_CODE_TAKE_PICTURE_DIALOG = 4;
    public final static int REQUEST_CODE_GALLERY_DIALOG = 5;

    private int currentType;
    private TextView checkName;
    private TextView checkTime;
    private TextView checkState;
    private EditText checkRemark;

    private Button btnModifyNormal;
    private Button btnModifySerious;
    private Button btnFinish;
    private Button btnTakePic;
    private Button btnSelectPic;
    private Button btnAdd;

    private ViewPager pager = null;
    private TextView tvEmpty = null;
    private List<ImageView> imageViews = null;
    private int pagerWidth;

    private List<String> imageLocalPath = null;
    private List<String> imageRemotePath = null;

    private List<String> dialogImageLocalPath = null;
    private List<ImageView> dialogImageViews = null;
    private Toolbar toolbar;
    private LinearLayout loadingLayout;

    private int pointId;
    private State currentState;

    //添加权限
    private boolean isHistoryItemADD;
    //审核权限
    private boolean isHistoryItemVerify;
    //修改权限
    private boolean isHistoryItemFix;

    private int lastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        pointId = getIntent().getIntExtra("point_id",-1);
        isHistoryItemADD = getIntent().getBooleanExtra("itemAdd",false);
        isHistoryItemVerify = getIntent().getBooleanExtra("itemQuery",false);
        isHistoryItemFix = getIntent().getBooleanExtra("itemFix",false);
        Log.d(TAG, "收到的的点id为：" + pointId);
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
        btnAdd = findViewById(R.id.check_add_item);
        btnAdd.setOnClickListener(this);
        currentType = REQUEST_TYPE_MAIN;
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
        getMenuInflater().inflate(R.menu.state,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    private void getDataFromServer(){
        showLoading(true);
        HttpUtils.getInstance().getCheckHistoryInfo(pointId, new OnResponseListener() {
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
                                for (ResponseCheckHistory history: data.getList()
                                     ) {
                                    if(!history.getList().isEmpty()){

                                        for (State item:
                                        history.getList()) {
                                            item.setCheckId(history.getCheckId());
                                            stateList.add(item);
                                        }
                                    }
                                }
                                lastId = stateList.get(0).getId();
                                setData(stateList.get(0));
                            }else{
                                setData(null);
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
                        Toast.makeText(StateActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void saveCheckInfo(int checkId,int status,String remark,List<String> imagePath){
        Log.d(TAG, "saveCheckInfo: check id is:" + checkId);
        HttpUtils.getInstance().saveCheckInfo(pointId, checkId,status,remark,imagePath, new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                ResponseBean data = ( ResponseBean)responseMapBean.body();

                if(data.getResult() == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StateActivity.this,"提交成功",Toast.LENGTH_LONG).show();
                            setLayoutFalse();
                            getDataFromServer();
                        }
                    });
                }else if(data.getResult() == 2){
                    if(null == data.getDesc()||"".equals(data.getDesc())){
                        Toast.makeText(StateActivity.this,"提交失败",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(StateActivity.this,data.getDesc(),Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                Toast.makeText(StateActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showListFragment(){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_list,StateListFragment.newInstance(stateList)).commit();
    }

    private ArrayList<State> stateList;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check_modify_normal:
                String remark1 = checkRemark.getText().toString();
                saveCheckInfo(currentState.getCheckId(),4,remark1,imageLocalPath);
                break;
            case R.id.check_modify_serious:
                String remark2 = checkRemark.getText().toString();
                saveCheckInfo(currentState.getCheckId(),3,remark2,imageLocalPath);
                break;
            case R.id.check_finish:
                String remark3 = checkRemark.getText().toString();

                if(btnFinish.getText().toString().equals("修复完成")){
                    saveCheckInfo(currentState.getCheckId(),5,remark3,imageLocalPath);
                }else if(btnFinish.getText().toString().equals("审核通过")){
                    saveCheckInfo(currentState.getCheckId(),1,remark3,imageLocalPath);
                }
                break;
            case R.id.check_take_pic:
                currentType = REQUEST_TYPE_MAIN;
                checkCameraPermission();
                break;
            case R.id.check_pick_pic:
                currentType = REQUEST_TYPE_MAIN;
                choosePhoto();
                break;
            case R.id.dialog_add_normal:
                String remarkNormal = dialogRemark.getText().toString();
                saveCheckInfo(0,4,remarkNormal,dialogImageLocalPath);
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                currentType = REQUEST_TYPE_MAIN;
                break;
            case R.id.dialog_add_serious:
                String remarkSerious = dialogRemark.getText().toString();
                saveCheckInfo(0,3,remarkSerious,dialogImageLocalPath);
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                currentType = REQUEST_TYPE_MAIN;
                break;
            case R.id.dialog_pick_pic:
                currentType = REQUEST_TYPE_DIALOG;
                choosePhoto();
                break;
            case R.id.dialog_take_pic:
                currentType = REQUEST_TYPE_DIALOG;
                checkCameraPermission();
                break;
            case R.id.check_add_item:
                addClick();
                break;
        }
    }

    @Override
    public void addClick() {
        if(isHistoryItemADD){
            dialogImageViews = new ArrayList<>();
            dialogImageLocalPath = new ArrayList<>();
            showBottomDialog();
        }else{
            Toast.makeText(StateActivity.this,"当前无权限，请到后台申请后重试！",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setData(State current) {

        if(current == null){
            setLayoutFalse();
            btnAdd.setVisibility(View.VISIBLE);
            return;
        }

        currentState = current;
        clearImageData();
        btnAdd.setVisibility(View.GONE);
        checkName.setText(current.getCreateUserName());
        checkTime.setText(current.getCreateTime());
        checkState.setText(current.getStatusStr());
        checkRemark.setText(current.getRemark());
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
                case 0:
                case 1:
                    setLayoutFalse();
                    break;
                case 3:
                case 4:
                    if(isHistoryItemFix){
                        btnTakePic.setVisibility(View.VISIBLE);
                        btnSelectPic.setVisibility(View.VISIBLE);
                        btnModifySerious.setVisibility(View.GONE);
                        btnModifyNormal.setVisibility(View.GONE);
                        btnFinish.setVisibility(View.VISIBLE);
                        Common.getInstance().setEditTextTrue(checkRemark);
                        btnFinish.setText("修复完成");
                    }else{
                        setLayoutFalse();
                        Toast.makeText(StateActivity.this,"当前无修复权限",Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:

                    if(isHistoryItemVerify){
                        btnTakePic.setVisibility(View.VISIBLE);
                        btnSelectPic.setVisibility(View.VISIBLE);
                        btnModifySerious.setVisibility(View.VISIBLE);
                        btnModifyNormal.setVisibility(View.VISIBLE);
                        btnFinish.setVisibility(View.VISIBLE);
                        Common.getInstance().setEditTextTrue(checkRemark);
                        btnFinish.setText("审核通过");
                    }else{
                        setLayoutFalse();
                        Toast.makeText(StateActivity.this,"当前无审核权限",Toast.LENGTH_LONG).show();
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
            intent.putExtra("pointId",pointId);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return false;
    }

    private void configRemoteData(List<Image> images){
        for (final Image cur: images) {
            ImageView current = new ImageView(StateActivity.this);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StateActivity.this,ImageActivity.class);
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
        currentFileName = Common.getInstance().getFilePath(StateActivity.this);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(currentFileName)));
        if(currentType == REQUEST_TYPE_DIALOG){
            startActivityForResult(intentToTakePhoto, REQUEST_CODE_TAKE_PICTURE_DIALOG);
        }else{
            startActivityForResult(intentToTakePhoto, REQUEST_CODE_TAKE_PICTURE);
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
        if(currentType == REQUEST_TYPE_DIALOG){
            startActivityForResult(intentToPickPic, REQUEST_CODE_GALLERY_DIALOG);
        }else{
            startActivityForResult(intentToPickPic, REQUEST_CODE_GALLERY);
        }
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

    private EditText dialogRemark;
    private TextView dialogTvEmpty;
    private ViewPager dialogPager;
    private Button dialogNormalBtnAdd;
    private Button dialogSeriousBtnAdd;
    private Button dialogBtnTakePic;
    private Button dialogBtnPickPic;

    private BottomSheetDialog dialog;
    private void showBottomDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_list, null);
        if(dialog == null ){

            dialog = new BottomSheetDialog(this);

            dialog.setContentView(view);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);

            dialogRemark = view.findViewById(R.id.dialog_remark);

            dialogTvEmpty = view.findViewById(R.id.dialog_empty_text);
            dialogPager = view.findViewById(R.id.dialog_viewPager);
            dialogNormalBtnAdd = view.findViewById(R.id.dialog_add_normal);
            dialogSeriousBtnAdd = view.findViewById(R.id.dialog_add_serious);
            dialogBtnTakePic = view.findViewById(R.id.dialog_take_pic);
            dialogBtnPickPic = view.findViewById(R.id.dialog_pick_pic);

            dialogNormalBtnAdd.setOnClickListener(this);
            dialogSeriousBtnAdd.setOnClickListener(this);
            dialogBtnTakePic.setOnClickListener(this);
            dialogBtnPickPic.setOnClickListener(this);
        }
        currentType = REQUEST_TYPE_DIALOG;
        showDialogPager();
        dialog.show();
    }

    private void showDialogPager(){

        if (dialogImageViews.size() < 1){
            dialogPager.setVisibility(View.GONE);
            dialogTvEmpty.setVisibility(View.VISIBLE);
            return;
        }else{
            dialogPager.setVisibility(View.VISIBLE);
            dialogTvEmpty.setVisibility(View.GONE);
            configDialogPager();
        }
    }

    private MyViewPagerAdapter dialogAdapter = null;
    private void configDialogPager(){

        if(dialogAdapter == null){
            pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels);
            ViewGroup.LayoutParams lp = dialogPager.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                lp.width = pagerWidth;
            }
            dialogPager.setLayoutParams(lp);
            dialogAdapter = new MyViewPagerAdapter(dialogImageViews);
            dialogPager.setAdapter(dialogAdapter);
        }else {
            dialogAdapter.notifyDataSetChanged();
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
                    final String oriPath = Common.getInstance().saveBitmap(StateActivity.this,originalBmp);
                    Log.d(TAG, "onActivityResult: path is" + oriPath);
                    imageLocalPath.add(oriPath);
                    ImageView cur = new ImageView(StateActivity.this);
                    cur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StateActivity.this,ImageActivity.class);
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
                            final String path = Common.getInstance().saveBitmap(StateActivity.this,bit);
                            Log.d(TAG, "onActivityResult: path is" + path);
                            imageLocalPath.add(path);
                            ImageView choose = new ImageView(StateActivity.this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(StateActivity.this,ImageActivity.class);
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
                case REQUEST_CODE_TAKE_PICTURE_DIALOG:

                    Bitmap bmp = BitmapFactory.decodeFile(currentFileName);
                    final String path = Common.getInstance().saveBitmap(StateActivity.this,bmp);
                    Log.d(TAG, "onActivityResult: path is" + path);
                    dialogImageLocalPath.add(path);
                    ImageView cur1 = new ImageView(StateActivity.this);
                    cur1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StateActivity.this,ImageActivity.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    });
                    cur1.setImageBitmap(bmp);
                    dialogImageViews.add(0,cur1);
                    dialogAdapter = null;
                    showDialogPager();
                    break;
                case REQUEST_CODE_GALLERY_DIALOG:
                    try {
                        //该uri是上一个Activity返回的
                        imageUri = data.getData();
                        if(imageUri!=null) {
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            final String galleryPath = Common.getInstance().saveBitmap(StateActivity.this,bit);
                            Log.d(TAG, "onActivityResult: path is" + galleryPath);
                            dialogImageLocalPath.add(galleryPath);
                            ImageView choose = new ImageView(StateActivity.this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(StateActivity.this,ImageActivity.class);
                                    intent.putExtra("path",galleryPath);
                                    startActivity(intent);
                                }
                            });
                            choose.setImageBitmap(bit);
                            dialogImageViews.add(0,choose);
                            dialogAdapter = null;
                            showDialogPager();
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
