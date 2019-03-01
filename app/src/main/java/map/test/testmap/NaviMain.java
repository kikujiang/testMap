package map.test.testmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.HttpUtils;

public class NaviMain extends AppCompatActivity {

    private static final String TAG = "NaviMain";
    private BottomNavigationView navigation;
    
    private int userId = -1;
    private Fragment mainFragment,taskFragment,messageFragment,personFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideAllFragment(transaction);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                        transaction.show(mainFragment);
                    break;
                case R.id.navigation_dashboard:
                    if(taskFragment == null){
                        taskFragment = TaskFragment.newInstance();
                        transaction.add(R.id.layout,taskFragment);
                    }else{
                        transaction.show(taskFragment);
                    }
                    break;
                case R.id.navigation_notifications:
                    if(messageFragment == null){
                        messageFragment = new MessageFragment();
                        transaction.add(R.id.layout,messageFragment);
                    }else{
                        transaction.show(messageFragment);
                    }
                    break;
                case R.id.navigation_person:
                    if(personFragment == null){
                        personFragment = SettingsFragment.newInstance(userId);
                        transaction.add(R.id.layout,personFragment);
                    }else{
                        transaction.show(personFragment);
                    }
                    break;
            }
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_main);

        if(Constants.isLogin){
            userId = Constants.userId;
        }else{
            userId = getIntent().getIntExtra("userId",0);
        }
        Log.d(TAG, "user id is:" + userId);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mainFragment == null){
            mainFragment =MainFragment.newInstance(userId);
            transaction.add(R.id.layout,mainFragment);
        }else{
            transaction.show(mainFragment);
        }
        transaction.commit();
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
            Toast.makeText(NaviMain.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    public void hideAllFragment(FragmentTransaction transaction){
        if(mainFragment != null){
            transaction.hide(mainFragment);
        }

        if(taskFragment != null){
            transaction.hide(taskFragment);
        }

        if(messageFragment != null){
            transaction.hide(messageFragment);
        }

        if(personFragment != null){
            transaction.hide(personFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!" + Constants.isExistAlready);
        if(!Constants.isExistAlready){
            logOut(userId);
        }
    }

    private void logOut(int userId){
        Log.d(TAG, "=========================logOut called!=========================");
        HttpUtils.getInstance().logOut(userId, new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {

                if(responseMapBean == null){
                    return;
                }

                ResponseBean current = (ResponseBean)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    Log.d(TAG, "fail: "+current);
                }

                if(current.getResult() == Constants.RESULT_OK){
                    Log.d(TAG, "退出成功");
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail message is:" + e.getMessage());
            }
        });
    }

}