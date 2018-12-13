package map.test.testmap.utils;

import android.util.Log;

import map.test.testmap.Constants;
import map.test.testmap.model.IUserBiz;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtils {
    private static final String TAG = "HttpUtils";
    private static volatile HttpUtils manager = null;
    private HttpUtils client = null;
    private Retrofit retrofit = null;
    private IUserBiz userBiz = null;

    private HttpUtils(){
        retrofit = new Retrofit.Builder().baseUrl(Constants.TEST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userBiz = retrofit.create(IUserBiz.class);
    }

    public static HttpUtils getInstance(){
        if (manager == null){
            synchronized (HttpUtils.class){
                if (manager == null){
                    manager = new HttpUtils();
                }
            }
        }
        return manager;
    }

    public void getLoginInfo(final OnResponseListener listener){
        Call<User> login = userBiz.getLoginInfo();
        login.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                listener.fail(t);
            }
        });

    }
}
