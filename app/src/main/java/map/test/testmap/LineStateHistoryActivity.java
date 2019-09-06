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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import map.test.testmap.model.Image;
import map.test.testmap.model.Line;
import map.test.testmap.model.LineState;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseCheckHistory;
import map.test.testmap.model.ResponseTaskUserBean;
import map.test.testmap.model.State;
import map.test.testmap.model.User;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import map.test.testmap.utils.OkHttpClientManager;
import map.test.testmap.utils.PreferencesUtils;
import retrofit2.Response;

public class LineStateHistoryActivity extends AppCompatActivity implements LineStateListFragment.OnTouchListener {

    private static final String TAG = "LineHistory";

    private TextView checkName;
    private TextView checkTime;
    private TextView checkState;
    private TextView checkRemark;
    private TextView checkLat;
    private TextView checkLong;
    private TextView checkStart;
    private TextView checkEnd;
    private TextView checkDeviceName;

    private LineState currentState;

    private Line currentLine = null;

    private ViewPager pager = null;
    private TextView tvEmpty = null;
    private List<ImageView> imageViews = null;
    private int pagerWidth;

    private List<String> imageLocalPath = null;
    private List<String> imageRemotePath = null;

    private Toolbar toolbar;
    private LinearLayout loadingLayout;

    private int lineId;

    private int pointId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_state_history);
        lineId = getIntent().getIntExtra("line_id",-1);

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
        checkStart = findViewById(R.id.check_start);
        checkEnd = findViewById(R.id.check_end);
        checkDeviceName = findViewById(R.id.sp_name);
        loadingLayout = findViewById(R.id.loading);
        initData();

    }

    private void initData(){

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.line_state,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("lineId",lineId);
                intent.putExtra("pointId",pointId);
                setResult(RESULT_OK,intent);
                finish();
                return true;
        }
        return true;
    }


    private void getSingleLine(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_SINGLE_LINE;
        Log.d(TAG, "请求获取某条线接口" + url);
        final Map<String,String> data = new HashMap<>();
        data.put("id",lineId+"");
        Log.d(TAG, "getSingleLine data is:" + data.toString());
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(okhttp3.Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();

                    Type type = new TypeToken<ResponseBean<Line>>(){}.getType();
                    final ResponseBean<Line> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        Log.d(TAG, "fail:" + currentData.getDesc());
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){
                        currentLine = currentData.getObject();
                        Log.d(TAG, "收到消息为："+ result);
                    }
                }catch (Exception e){
                    Log.d(TAG, "fail:" + e.getMessage());
                }
            }

            @Override
            public void fail(Exception e) {
                Log.d(TAG, "fail:" + e.getMessage());
            }
        });
    }

    private void getDataFromServer(){
        showLoading(true);
        getSingleLine();
        HttpUtils.getInstance().getLineCheckHistoryInfo(lineId, new OnResponseListener() {
            @Override
            public void success(final Response responseMapBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBean<ResponseCheckHistory> data = ( ResponseBean<ResponseCheckHistory>)responseMapBean.body();

                        if(data.getResult() == 2){
                            if(data.getDesc().equals("登陆已过期,请重新登陆")){
                                Toast.makeText(LineStateHistoryActivity.this,"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                                PreferencesUtils.putString(LineStateHistoryActivity.this,"account",null);
                                PreferencesUtils.putString(LineStateHistoryActivity.this,"password_selector",null);
                                Intent intent = new Intent(LineStateHistoryActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
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

    private void showListFragment(){
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_list,LineStateListFragment.newInstance(stateList)).commit();
    }

    private ArrayList<LineState> stateList;


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

        if(currentLine != null){
            checkStart.setText(currentLine.getTag_begin_name()+"");
            checkEnd.setText(currentLine.getTag_end_name()+"");
            checkDeviceName.setText(currentLine.getName());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyUp: ");
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

}