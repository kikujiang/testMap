package map.test.testmap.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import map.test.testmap.model.OnInfoListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpClientManager {

    private static final String TAG = "OkHttpClientManager";
    private static volatile OkHttpClientManager manager = null;
    private OkHttpClient client = null;

    private OkHttpClientManager(){
        client = new OkHttpClient();
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
}
