package map.test.testmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.crashreport.CrashReport;

import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.User;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.PreferencesUtils;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * A login screen that offers login via email/password_selector.
 */
public class
LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    /**
     * Id to identity REQUEST_LOCATION permission request.
     */
    private static final int REQUEST_LOCATION = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mPassWordCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.isLogin = false;

        CrashReport.initCrashReport(getApplicationContext(), "171b14d1df", false);

        String account = PreferencesUtils.getString(LoginActivity.this,"account",null);
        String password = PreferencesUtils.getString(LoginActivity.this,"password_selector",null);
        if(account != null && password != null){
            Log.d(TAG, "onCreate: 1");
            initWithApiKey();
            loginAuto();
            return;
        }
        Log.d(TAG, "onCreate: 2");

        init();
    }

    private void init(){
        initWithApiKey();
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPassWordCheck = findViewById(R.id.password_check);
        mPassWordCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        mPassWordCheck.setChecked(false);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        initView();
        checkUpdate();
    }


    private void initWithApiKey() {
//        PushManager.startWork(getApplicationContext(),
//                PushConstants.LOGIN_TYPE_API_KEY,
//                Utils.getMetaValue(LoginActivity.this, "api_key"));
        initWithTencent();
    }

    private void loginAuto(){
        String account = PreferencesUtils.getString(LoginActivity.this,"account",null);
        String password = PreferencesUtils.getString(LoginActivity.this,"password_selector",null);


        if(account != null && password != null){
            mAuthTask = new UserLoginTask(account, password,1);
            mAuthTask.execute((Void) null);
        }
    }

    private void initWithTencent(){
        XGPushConfig.enableDebug(this, true);
        XGPushConfig.getToken(this);
        /*
            注册信鸽服务的接口
            如果仅仅需要发推送消息调用这段代码即可
        */
        XGPushManager.registerPush(getApplicationContext(),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        Log.w(com.tencent.android.tpush.common.Constants.LogTag, "+++ register push sucess. token:" + data + "flag" + flag);
                        if(data != null){
                            Constants.channelId = data.toString();
                            Log.d(TAG, "channel id is:" + Constants.channelId);
                        }
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.w(com.tencent.android.tpush.common.Constants.LogTag,
                                "+++ register push fail. token:" + data
                                        + ", errCode:" + errCode + ",msg:"
                                        + msg);
                    }
                });
//
//        // 获取token
//        XGPushConfig.getToken(this);
    }

    private void checkUpdate(){
        HttpUtils.getInstance().checkUpdate(new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                if(responseMapBean == null){
                    Log.d(TAG, "服务器返回对象为空");
                    return;
                }

                ResponseBean current = (ResponseBean)responseMapBean.body();

                if(current == null){
                    return;
                }

                if(current.getResult() == Constants.RESULT_FAIL){
                    Log.d(TAG, "fail message is:" + current.getDesc());
                }

                if(current.getResult() == Constants.RESULT_OK){
                    int serverVersion = current.getVersionCode() == null?current.getId():Integer.parseInt(current.getVersionCode());
                    int currentVersion = Common.getInstance().getVersionCode(LoginActivity.this);

                    if(currentVersion < serverVersion){
                        String downloadPath = current.getAppPath() == null?current.getDesc():current.getAppPath();
                        download(downloadPath);
                    }
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail: "+e.getMessage());
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
                        Common.getInstance().downloadApk(LoginActivity.this,url,"下载中","电力地理系统");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private void initView(){
        String account = PreferencesUtils.getString(LoginActivity.this,"account",null);
        String password = PreferencesUtils.getString(LoginActivity.this,"password_selector",null);

        if(account != null){
            mEmailView.setText(account);
        }

        if(password != null){
            mPasswordView.setText(password);
        }
    }

    private void populateAutoComplete() {
        if (!requestLocation()) {
            return;
        }
    }

    private boolean requestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "====================onDestroy: ");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        Log.d(TAG, "attemptLogin: password " + password);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password_selector, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password,0);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Response<ResponseBean<User>>> {

        private final String mAccount;
        private final String mPassword;
        private int flag = 0;

        UserLoginTask(String account, String password,int f) {
            mAccount = account;
            mPassword = password;
            this.flag = f;

            Log.d(TAG, "UserLoginTask: acc:"+mAccount+",pwd:"+mPassword);
        }

        @Override
        protected Response<ResponseBean<User>> doInBackground(Void... params) {
            String mac = Common.getInstance().getMacAddress();
            Log.d(TAG, "doInBackground: mac is:" + mac);

            if(null == mac || "".equals(mac)){
                mac = "00:00:00:00:00:00";
            }
            return HttpUtils.getInstance().getLoginInfo(mAccount,mPassword,mac,Constants.channelId);
        }

        @Override
        protected void onPostExecute(final Response<ResponseBean<User>> response) {
            Log.d(TAG, "onPostExecute: ");
            mAuthTask = null;
            if(flag == 0){
                showProgress(false);
            }

            if(response == null){
                String errMsg = "服务器请求超时！";
                Log.e(TAG, "onPostExecute: "+ errMsg);
                Toast.makeText(LoginActivity.this,errMsg,Toast.LENGTH_LONG).show();

                if(flag == 1){
                    init();
                }
                return;
            }

            ResponseBean<User> currentBean = response.body();

            if(currentBean == null){
                String errMsg = "服务器异常，请稍后再试！";
                Log.e(TAG, "onPostExecute: "+ errMsg);
                Toast.makeText(LoginActivity.this,errMsg,Toast.LENGTH_LONG).show();

                if(flag == 1){
                    init();
                }

                return;
            }

            if(currentBean.getResult() == Constants.RESULT_FAIL){
                Log.e(TAG, "onPostExecute: "+ currentBean.getDesc());
                Toast.makeText(LoginActivity.this,currentBean.getDesc(),Toast.LENGTH_LONG).show();

                if(flag == 1){
                    init();
                }
                return;
            }

            if(currentBean.getResult() == Constants.RESULT_OK){
                Log.i(TAG, "onPostExecute: success");
                Constants.isLogin = true;
                Constants.userId = currentBean.getObject().getId();
                Constants.userName = currentBean.getObject().getUsername();
                Constants.loginTag = currentBean.getObject().getLoginTag();
                PreferencesUtils.putString(LoginActivity.this,"account",mAccount);
                PreferencesUtils.putString(LoginActivity.this,"password_selector",mPassword);
                Intent intent = new Intent(LoginActivity.this,NaviMain.class);
                intent.putExtra("userId",currentBean.getObject().getId());
                startActivity(intent);
            }
            finish();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            if(flag == 0){
                showProgress(false);
            }
        }
    }
}