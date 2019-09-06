package map.test.testmap;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.TaskInfo;
import map.test.testmap.model.User;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.PreferencesUtils;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "settings";
    
    private Button updateLayout;
    private LinearLayout infoLayout;
    private Button exitLayout;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView tvVersion;

    private TextView tvTotal1;
    private TextView tvTotal2;
    private TextView tvNew1;
    private TextView tvNew2;

    private int userId = -1;

    private Toolbar toolbar;

    private static final String ARG_PARAM1 = "userId";

    public static SettingsFragment newInstance(int userId) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = Integer.MIN_VALUE;
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        return view;
    }

    private void init(View view){

        updateLayout = view.findViewById(R.id.settings_update);
        infoLayout = view.findViewById(R.id.settings_info);
        exitLayout = view.findViewById(R.id.settings_exit);
        tvName = view.findViewById(R.id.tv_settings_name);
        tvPhone = view.findViewById(R.id.tv_settings_phone);
        tvEmail = view.findViewById(R.id.tv_settings_mail);
        tvVersion = view.findViewById(R.id.tv_settings_version);
        tvTotal1 = view.findViewById(R.id.settings_total);
        tvTotal2 = view.findViewById(R.id.settings_total2);
        tvNew1 = view.findViewById(R.id.settings_new);
        tvNew2 = view.findViewById(R.id.settings_new2);
        toolbar = view.findViewById(R.id.settings_toolbar);

        tvVersion.setText(Common.getInstance().getVersionName(getActivity()));
        updateLayout.setOnClickListener(this);
//        infoLayout.setOnClickListener(this);
        exitLayout.setOnClickListener(this);
        if(userId == Integer.MIN_VALUE){
            return;
        }

        toolbar.setTitle("");

        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        getUserInfo();
        getTaskInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            getTaskInfo();
        }

    }

    private void getTaskInfo(){
        HttpUtils.getInstance().getTaskInfo(new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                if(responseMapBean == null){
                    Log.d(TAG, "responseMapBean is null!" );
                    return;
                }

                ResponseBean<TaskInfo> taskInfo = (ResponseBean<TaskInfo>)responseMapBean.body();
                if(taskInfo.getResult() == Constants.RESULT_FAIL){
                    if(taskInfo.getDesc().equals("登陆已过期,请重新登陆")){
                        Toast.makeText(getActivity(),"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                        PreferencesUtils.putString(getActivity(),"account",null);
                        PreferencesUtils.putString(getActivity(),"password_selector",null);
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                    Log.d(TAG, "fali: "+ taskInfo);

                }

                if(taskInfo.getResult() == Constants.RESULT_OK){
                    final TaskInfo curTaskInfo = taskInfo.getObject();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTotal1.setText(curTaskInfo.getCheckSum()+"条");
                            tvTotal2.setText(curTaskInfo.getPublishSum()+"条");
                            tvNew1.setText(curTaskInfo.getCheckAdd()+"条");
                            tvNew2.setText(curTaskInfo.getPublishAdd()+"条");
                        }
                    });

                }
            }

            @Override
            public void fail(Throwable e) {
            }
        });
    }

    /**
     * 获取用户权限的信息
     */
    private void getUserInfo(){
        HttpUtils.getInstance().getUserInfo(userId, new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {

                if(responseMapBean == null){
                    Log.d(TAG, "responseMapBean is null!" );
                    return;
                }

                final ResponseBean<User> current = (ResponseBean<User>)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    Log.d(TAG, "success: "+ current);

                    if(current.getDesc().equals("登陆已过期,请重新登陆")){
                        Toast.makeText(getActivity(),"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                        PreferencesUtils.putString(getActivity(),"account",null);
                        PreferencesUtils.putString(getActivity(),"password_selector",null);
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                }

                if(current.getResult() == Constants.RESULT_OK){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvName.setText(current.getObject().getUsername());
                            tvPhone.setText(current.getObject().getPhone());
                            tvEmail.setText(current.getObject().getEmail());
                        }
                    });

                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail message is:" + e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settings_update:
                checkUpdate();
                break;
            case R.id.settings_info:
                enterVersionActivity();
                break;
            case R.id.settings_exit:
                logOut(userId);
                break;
        }
    }

    private void enterVersionActivity(){
        Intent versionIntent = new Intent(getActivity(),VersionActivity.class);
        startActivity(versionIntent);
    }

    /**
     * 退出app
     */
    private void logOut(int userId){
        Log.d(TAG, "=========================logOut called!=========================");
        HttpUtils.getInstance().logOut(userId, new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {

                if(responseMapBean == null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"访问服务器超时，请稍后重试！",Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }

                ResponseBean current = (ResponseBean)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){

                    if(current.getDesc().equals("登陆已过期,请重新登陆")){
                        Toast.makeText(getActivity(),"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                        PreferencesUtils.putString(getActivity(),"account",null);
                        PreferencesUtils.putString(getActivity(),"password_selector",null);
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }

                    Log.d(TAG, "fail: "+current);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"退出失败，请检查网络连接！",Toast.LENGTH_LONG).show();
                        }
                    });

                }

                if(current.getResult() == Constants.RESULT_OK){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Constants.isExistAlready = true;
                                Toast.makeText(getActivity(),"退出成功",Toast.LENGTH_LONG).show();
                                Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
                                PreferencesUtils.putString(getActivity(),"account",null);
                                PreferencesUtils.putString(getActivity(),"password_selector",null);
                                startActivity(loginIntent);
                                getActivity().finish();
                            }
                        });
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail message is:" + e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"退出失败，请检查网络连接！",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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

                    if(current.getDesc().equals("登陆已过期,请重新登陆")){
                        Toast.makeText(getActivity(),"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                        PreferencesUtils.putString(getActivity(),"account",null);
                        PreferencesUtils.putString(getActivity(),"password_selector",null);
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }

                    Toast.makeText(getActivity(),current.getDesc(),Toast.LENGTH_LONG).show();
                }

                if(current.getResult() == Constants.RESULT_OK){

                    int serverVersion = current.getVersionCode() == null?current.getId():Integer.parseInt(current.getVersionCode());
                    int currentVersion = Common.getInstance().getVersionCode(getActivity());

                    if(currentVersion < serverVersion){
                        String downloadPath = current.getAppPath() == null?current.getDesc():current.getAppPath();
                        download(downloadPath);
                    }else{
                        Toast.makeText(getActivity(),"当前是最新版本",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail: "+e.getMessage());
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void download(final String url) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("版本更新")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Common.getInstance().downloadApk(getActivity(),url,"下载中","电力地理系统");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }
}
