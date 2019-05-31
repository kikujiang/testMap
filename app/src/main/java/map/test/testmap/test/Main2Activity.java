package map.test.testmap.test;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.tencent.bugly.crashreport.CrashReport;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import map.test.testmap.R;
import map.test.testmap.utils.Common;
import uk.co.senab.photoview.log.LoggerDefault;

public class Main2Activity extends AppCompatActivity {

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
