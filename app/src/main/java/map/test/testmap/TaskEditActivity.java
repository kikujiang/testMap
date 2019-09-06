package map.test.testmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import map.test.testmap.model.Image;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseTaskUserBean;
import map.test.testmap.model.User;
import map.test.testmap.mvvm.data.model.ImageBean;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import map.test.testmap.utils.PreferencesUtils;
import map.test.testmap.view.MultiSelectionSpinner;
import retrofit2.Response;

public class TaskEditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_TAKE_PICTURE = 0;
    public final static int REQUEST_CODE_GALLERY = 1;

    private Toolbar toolbar;
    private EditText remark;
    private TextView tvEmpty;
    private ViewPager pager;
    private Button normalBtnAdd;
    private Button seriousBtnAdd;
    private Button dialogBtnTakePic;
    private Button dialogBtnPickPic;
    private Spinner spinnerWorkerList;
    private int workerId;
    private int taskId;
    private int itemId;
    private int pagerWidth;
    private List<ImageView> imageViews = null;

    private List<String> imageLocalPath = null;
    private List<String> imageRemotePath = null;

    private LinearLayout loading = null;

    private static final String TAG = "edit";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        toolbar = findViewById(R.id.edit_toolbar);

        taskId = getIntent().getIntExtra("checkId",-1);
        itemId = getIntent().getIntExtra("item_id",-1);
        Log.d(TAG, "任务id是:"+taskId+",item id是:"+itemId);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        initImageData();
        getUserList();

    }

    private void init(){
        remark = findViewById(R.id.dialog_remark);

        tvEmpty = findViewById(R.id.dialog_empty_text);
        pager = findViewById(R.id.dialog_viewPager);
        normalBtnAdd = findViewById(R.id.dialog_add_normal);
        seriousBtnAdd = findViewById(R.id.dialog_add_serious);
        dialogBtnTakePic = findViewById(R.id.dialog_take_pic);
        dialogBtnPickPic = findViewById(R.id.dialog_pick_pic);
        spinnerWorkerList = findViewById(R.id.spinner_worker_list);
        loading = findViewById(R.id.loading);

        normalBtnAdd.setOnClickListener(this);
        seriousBtnAdd.setOnClickListener(this);
        dialogBtnTakePic.setOnClickListener(this);
        dialogBtnPickPic.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_add_normal:
                String remarkNormal = remark.getText().toString();
                if(workerId == 0){
                    Toast.makeText(TaskEditActivity.this,"检修人不能为空，请选择后在重试！",Toast.LENGTH_LONG).show();
                    return;
                }
                saveCheckInfo(workerId,4,remarkNormal,imageLocalPath);
                break;
            case R.id.dialog_add_serious:
                if(workerId == 0){
                    Toast.makeText(TaskEditActivity.this,"检修人不能为空，请选择后在重试！",Toast.LENGTH_LONG).show();
                    return;
                }
                String remarkSerious = remark.getText().toString();
                saveCheckInfo(workerId,3,remarkSerious,imageLocalPath);
                break;
            case R.id.dialog_take_pic:
                checkCameraPermission();
                break;
            case R.id.dialog_pick_pic:
                choosePhoto();
                break;
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

    public void saveCheckInfo(int userId,int status,String remark,List<String> imagePath){
        Log.d(TAG, "saveCheckInfo: user id is:" + userId+",and item id is:"+itemId);
        HttpUtils.getInstance().editTagCheck(itemId, status,remark,userId,imagePath, new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                ResponseBean data = ( ResponseBean)responseMapBean.body();

                if(data.getResult() == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TaskEditActivity.this,"提交成功",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }else if(data.getResult() == 2){

                    if(data.getDesc().equals("登陆已过期,请重新登陆")){
                        Toast.makeText(TaskEditActivity.this,"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                        PreferencesUtils.putString(TaskEditActivity.this,"account",null);
                        PreferencesUtils.putString(TaskEditActivity.this,"password_selector",null);
                        Intent intent = new Intent(TaskEditActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if(null == data.getDesc()||"".equals(data.getDesc())){
                        Toast.makeText(TaskEditActivity.this,"提交失败",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(TaskEditActivity.this,data.getDesc(),Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                Toast.makeText(TaskEditActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //执行人列表
    private List<User> executeUserList;

    private void getUserList(){
        HttpUtils.getInstance().getNextUserList(new OnResponseListener() {
            @Override
            public void success(final Response responseMapBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBean<ResponseTaskUserBean> data = ( ResponseBean<ResponseTaskUserBean>)responseMapBean.body();

                        if(data.getResult() == 1){
                            executeUserList = data.getObject().getTagCheckUserList1();

                            if (executeUserList.size() > 0){
                                String[] typeData =new String[executeUserList.size()];
                                for (int i=0;i<executeUserList.size();i++){
                                    typeData[i] = executeUserList.get(i).getUsername();
                                }

                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(TaskEditActivity.this,android.R.layout.simple_list_item_multiple_choice,typeData);
                                spinnerWorkerList.setAdapter(adapter);
                                spinnerWorkerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String currentText = (String)spinnerWorkerList.getSelectedItem();
                                            for (User user: executeUserList){
                                                if(currentText.equals(user.getUsername()) ){
                                                    workerId = user.getId();
                                                    Log.d(TAG, "选中的检修人id为: "+workerId);
                                                    break;
                                                }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }

                                });

                                showPager();
                                new DetailTask().execute(taskId);
                            }else{
                                String[] typeData =new String[]{"当前无选项"};
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(TaskEditActivity.this,android.R.layout.simple_list_item_multiple_choice,typeData);
                                spinnerWorkerList.setAdapter(adapter);
                            }

                        }
                    }
                });
            }

            @Override
            public void fail(final Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TaskEditActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void configRemoteData(List<ImageBean> images){
        for (final ImageBean cur: images) {
            ImageView current = new ImageView(TaskEditActivity.this);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskEditActivity.this,ImageActivity.class);
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
        currentFileName = Common.getInstance().getFilePath(TaskEditActivity.this);
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
                    final String oriPath = Common.getInstance().saveBitmap(TaskEditActivity.this,originalBmp);
                    Log.d(TAG, "onActivityResult: path is" + oriPath);
                    imageLocalPath.add(oriPath);
                    ImageView cur = new ImageView(TaskEditActivity.this);
                    cur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(TaskEditActivity.this,ImageActivity.class);
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
                            final String path = Common.getInstance().saveBitmap(TaskEditActivity.this,bit);
                            Log.d(TAG, "onActivityResult: path is" + path);
                            imageLocalPath.add(path);
                            ImageView choose = new ImageView(TaskEditActivity.this);
                            choose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(TaskEditActivity.this,ImageActivity.class);
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

    class DetailTask extends AsyncTask<Integer,Void,TaskDetailBean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected TaskDetailBean doInBackground(Integer[] ids) {
            try{
                int id = ids[0];
                Log.d(TAG, "doInBackground: called and point id is:"+id);
                Response<ResponseBean<TaskDetailBean>> response = HttpUtils.getInstance().getTaskDetail(id);
                return response.body().getList().get(0);
            }catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(TaskDetailBean bean) {
            super.onPostExecute(bean);

            String curWorkerName = bean.getPutUserName();
            int index = 0;
            for (User item:executeUserList) {
                if(item.getUsername().equals(curWorkerName)){
                    workerId = item.getId();
                    break;
                }
                index++;
            }

            if (index == executeUserList.size()){
                index = 0;
            }
            spinnerWorkerList.setSelection(index);
            remark.setText(bean.getRemark());

            if(bean.getImageList() != null && bean.getImageList().size() > 0){
                imageViews = new ArrayList<>();
                clearImageData();
                List<ImageBean> images = bean.getImageList();
                configRemoteData(images);
                configPager(pager);
                pager.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }else{
                pager.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }

            loading.setVisibility(View.GONE);

        }

    }
}