package map.test.testmap.mvvm.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.bugly.crashreport.CrashReport;

import map.test.testmap.BaseActivity;
import map.test.testmap.R;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.HttpUtils;
import retrofit2.Response;

public class WXCodeActivity extends BaseActivity {

    private static final String TAG = "WXCodeActivity";

    private Toolbar toolbar;
    private TextView tvCode;
    private ImageView imageCode;

    private static final String prefix = "企业微信专用码：";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wxcode);

        toolbar = findViewById(R.id.code_toolbar);
        tvCode = findViewById(R.id.tv_code);
        imageCode = findViewById(R.id.image_code);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        HttpUtils.getInstance().getWXCode(new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                ResponseBean data = (ResponseBean) responseMapBean.body();

                if (data.getResult() == 1) {
                    String code = data.getDesc();
                    String content = prefix+code;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCode.setText(content);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCode.setText(prefix+"获取失败");
                        }
                    });
                }
            }

            @Override
            public void fail(Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCode.setText(prefix+"获取失败");
                    }
                });
            }
        });

        HttpUtils.getInstance().getWXCodeImg(new OnResponseListener() {
            @Override
            public void success(Response responseMapBean) {
                ResponseBean data = (ResponseBean) responseMapBean.body();
                Log.d(TAG, "image src data is : ");

                if (data.getResult() == 1) {
                    String imageSrc = data.getDesc();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WXCodeActivity.this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)).load(imageSrc).into(imageCode);
                        }
                    });
                }
            }

            @Override
            public void fail(Throwable e) {
                Log.e(TAG, "image src error data is : "+ e.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
//                finish();
                CrashReport.testJavaCrash();
                return true;
        }
        return true;
    }
}
