package map.test.testmap.mvvm.data.network;

import android.util.Log;

import map.test.testmap.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceCreator {

    private HttpLoggingInterceptor loggingInterceptor = null;

    private OkHttpClient client = null;

    private Retrofit retrofit = null;

    public ServiceCreator(){
        init();
    }

    private void init(){
        loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("http","交互数据为 = "+message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        retrofit = new Retrofit.Builder().baseUrl(Constants.WEB_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <T extends Object>Object create(Class<T> clazz){
        return retrofit.create(clazz);
    }
}
