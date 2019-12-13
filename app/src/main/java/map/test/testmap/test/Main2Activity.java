package map.test.testmap.test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import org.reactivestreams.Subscription;

import map.test.testmap.BaseActivity;
import map.test.testmap.R;

public class Main2Activity extends BaseActivity {

    private Button btn;
    private ImageView image;
    private static final String TAG = "rxjava";
    private Bitmap curBitmap;
    private Subscription mSubscription; // 用于保存Subscription对象
    String http = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559121777020&di=2dfe253a111b4194a5c014f6c469cc2b&imgtype=0&src=http%3A%2F%2Fk.zol-img.com.cn%2Fsjbbs%2F7692%2Fa7691515_s.jpg";

    private Toast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CrashReport.initCrashReport(getApplicationContext(), "171b14d1df", false);
        setContentView(R.layout.activity_main2);
        btn = findViewById(R.id.button);
        image = findViewById(R.id.image);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CrashReport.testJavaCrash();
            }
        });

    }

    private Bitmap getBitmapFromHttp() throws Exception{
        FutureTarget<Bitmap> futureTarget =
                Glide.with(Main2Activity.this)
                        .asBitmap()
                        .load(http)
                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        return futureTarget.get();
    }
}
