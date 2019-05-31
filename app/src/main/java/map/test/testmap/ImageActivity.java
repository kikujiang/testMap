package map.test.testmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import map.test.testmap.utils.Common;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";
    
    private PhotoView currentView;
    private Button saveBtn;
    private String http;

    private Bitmap curBitmap;
    private Toast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        currentView = findViewById(R.id.photo_view);
        saveBtn = findViewById(R.id.btn_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.create(new ObservableOnSubscribe<File>() {

                    @Override
                    public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                        try {

                            if(curBitmap == null){
                                FutureTarget<Bitmap> futureTarget =
                                        Glide.with(ImageActivity.this)
                                                .asBitmap()
                                                .load(http)
                                                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                                curBitmap = futureTarget.get();
                            }

                            File file = Common.getInstance().saveImage(ImageActivity.this,curBitmap);

                            emitter.onNext(file);
                        }catch (Exception e){

                            emitter.onError(e);
                        }
                        emitter.onComplete();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<File, File>() {
                            @Override
                            public File apply(File file) throws Exception {
                                return null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .delay(5 * 1000,TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<File>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe: ");
                            }

                            @Override
                            public void onNext(File value) {
                                Log.d(TAG, "onNext: ");
                                myToast = Toast.makeText(ImageActivity.this,"图片地址保存在:"+value.getAbsolutePath(),Toast.LENGTH_LONG);
                                myToast.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        myToast.cancel();
                                    }
                                },5*1000);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: "+e.getMessage());
                                myToast = Toast.makeText(ImageActivity.this,"图片保存失败",Toast.LENGTH_LONG);
                                myToast.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        myToast.cancel();
                                    }
                                },5*1000);
                            }

                            @Override
                            public void onComplete() {
                            }
                });
            }
        });
        String path = getIntent().getStringExtra("path");
        http = getIntent().getStringExtra("http");

        if(path == null && http == null){
            finish();
        }

        if(path != null){
            curBitmap = BitmapFactory.decodeFile(path);
            currentView.setImageBitmap(curBitmap);
            currentView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });
        }

        if(http != null){
            Glide.with(this).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)).load(http).into(currentView);
            currentView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });



        }
    }
}
