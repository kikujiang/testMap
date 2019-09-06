package map.test.testmap;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import map.test.testmap.model.Line;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.OkHttpClientManager;
import map.test.testmap.utils.PreferencesUtils;
import okhttp3.Response;

public class LineDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int lineId;
    private Line currentLine;

    public static final int MSG_START = 100000;
    public static final int MSG_END = 100001;
    public static final int MSG_MSG_ERROR = 100002;
    public static final int MSG_MSG_FAILURE = 100003;
    public static final int MSG_GET_SINGLE_LINE_END = 100008;

    private static final String TAG = "line_detail";

    private LinearLayout loadingLayout;
    private TextView failLayout;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START:
                    loadingLayout.setVisibility(View.VISIBLE);
                    failLayout.setVisibility(View.GONE);
                    Log.d(TAG, "handleMessage: msg start");
                    break;
                case MSG_MSG_ERROR:
                    loadingLayout.setVisibility(View.GONE);
                    failLayout.setVisibility(View.VISIBLE);
                    String message = (String) msg.obj;
                    Log.d(TAG, "错误消息为:" + message);
                    Toast.makeText(LineDetailActivity.this,"服务器响应失败，请稍后重试！",Toast.LENGTH_LONG).show();
                    break;
                case MSG_MSG_FAILURE:
                    loadingLayout.setVisibility(View.GONE);
                    failLayout.setVisibility(View.VISIBLE);
                    String failureMsg = (String) msg.obj;
                    Log.d(TAG, "失败消息为:" + failureMsg);
                    Toast.makeText(LineDetailActivity.this,failureMsg,Toast.LENGTH_LONG).show();
                    break;
                case MSG_END:
                    Log.d(TAG, "handleMessage: msg end");
                    loadingLayout.setVisibility(View.GONE);
                    break;
                case MSG_GET_SINGLE_LINE_END:
                    if(currentLine != null){
                        Log.d(TAG, "onPolylineClick: "+currentLine.getName());
                        setLineData();
                    }else {
                        Toast.makeText(LineDetailActivity.this,"当前线路信息异常，请重新刷新！",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private EditText etLineName;
    private EditText etLineType;
    private EditText etLineBelong;
    private EditText etLineRemark;
    private EditText etLineManager;
    private EditText etLineName2;
    private EditText etLineLength;

    private Button btnLineConfirm;
    private Button btnLineState;
    private Button btnLineCreatePoint;
    private Button btnLineCreateAssistPoint;
    private Button btnLineHistory;

    private void initView(){
        loadingLayout = findViewById(R.id.line_detail_loading);
        failLayout = findViewById(R.id.tv_loading_failed);
        toolbar = findViewById(R.id.toolbar_line_detail);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etLineName = findViewById(R.id.et_line_name);
        etLineType = findViewById(R.id.et_line_type);
        etLineBelong = findViewById(R.id.et_line_belong);
        etLineName2 = findViewById(R.id.et_line_name2);
        etLineLength = findViewById(R.id.et_line_length);

        etLineRemark = findViewById(R.id.et_line_remark);
        etLineManager = findViewById(R.id.et_line_manager);

        btnLineConfirm = findViewById(R.id.btn_ok);
        btnLineState = findViewById(R.id.line_state);
        btnLineCreatePoint = findViewById(R.id.line_create_point);
        btnLineCreateAssistPoint = findViewById(R.id.line_create_assist_point);
        btnLineHistory = findViewById(R.id.line_history);

        Common.getInstance().setEditTextFalse(etLineName);
        Common.getInstance().setEditTextFalse(etLineType);
        Common.getInstance().setEditTextFalse(etLineBelong);
        Common.getInstance().setEditTextFalse(etLineName2);
        Common.getInstance().setEditTextFalse(etLineLength);
        Common.getInstance().setEditTextFalse(etLineRemark);
        Common.getInstance().setEditTextFalse(etLineManager);

        btnLineConfirm.setVisibility(View.GONE);
        btnLineState.setVisibility(View.GONE);
        btnLineCreatePoint.setVisibility(View.GONE);
        btnLineCreateAssistPoint.setVisibility(View.GONE);
        btnLineHistory.setVisibility(View.GONE);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lineId = getIntent().getIntExtra("id",-1);
        setContentView(R.layout.activity_line_detail);
        initView();
        new Thread(){
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_START);
                getSingleLine(lineId+"");
                mHandler.sendEmptyMessage(MSG_END);
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void setLineData(){
        etLineName.setText(currentLine.getName());
        etLineType.setText(currentLine.getPosTypeStr());
        etLineRemark.setText(currentLine.getRemark());

        if(currentLine.getManageUserName() != null){
            etLineManager.setText(currentLine.getManageUserName());
        }

        if(currentLine.getStationName() != null){
            etLineBelong.setText(currentLine.getStationName());
        }

        if(currentLine.getStationName() != null){
            etLineBelong.setText(currentLine.getStationName());
        }

        etLineLength.setText(currentLine.getLineLength()+"");

        if(currentLine.getL_name() != null){
            etLineName2.setText(currentLine.getL_name());
        }
    }

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
                        if(currentData.getDesc().equals("登陆已过期,请重新登陆")){
                            Toast.makeText(LineDetailActivity.this,"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                            PreferencesUtils.putString(LineDetailActivity.this,"account",null);
                            PreferencesUtils.putString(LineDetailActivity.this,"password_selector",null);
                            Intent intent = new Intent(LineDetailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
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
}