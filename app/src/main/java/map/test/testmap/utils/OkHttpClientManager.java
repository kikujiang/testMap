package map.test.testmap.utils;

import com.tencent.bugly.crashreport.BuglyLog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import map.test.testmap.model.OnInfoListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpClientManager {

    private static final String TAG = "OkHttpClientManager";
    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static volatile OkHttpClientManager manager = null;
    private OkHttpClient client = null;

    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            //打印retrofit日志
            BuglyLog.i(TAG,"交互数据为 = "+message);
        }
    });

    private OkHttpClientManager(){
        client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .removeHeader("User-Agent")//移除旧的
                                .addHeader("User-Agent", Common.getInstance().getUserAgent())//添加真正的头部
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }

    public static OkHttpClientManager getInstance(){
        if (manager == null){
            synchronized (OkHttpClientManager.class){
                if (manager == null){
                    manager = new OkHttpClientManager();
                }
            }
        }
        return manager;
    }

    public void post(String url, Map<String,String> data, final OnInfoListener listener){
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String,String> item: data.entrySet()) {
            builder.add(item.getKey(),item.getValue());
        }
        RequestBody requestBody = builder.build();
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url(url).post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = client.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.fail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.success(response);
            }
        });
    }

    public void sendFileToServer(String url, List<String> pathList, Map<String,String> data, final OnInfoListener listener){

        if(pathList == null || pathList.size() < 1){
            post(url,data,listener);
            return;
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String,String> item: data.entrySet()) {
            builder.addFormDataPart(item.getKey(),item.getValue());
        }

        for (String path: pathList) {
            File file = new File(path);
            builder.addFormDataPart("images", file.getName(),
                    RequestBody.create(MEDIA_TYPE_JPG, file));
        }

        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = client.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.fail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.success(response);
            }
        });
    }
}
