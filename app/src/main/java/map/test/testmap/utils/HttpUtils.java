package map.test.testmap.utils;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import map.test.testmap.Constants;
import map.test.testmap.model.IUserBiz;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.User;
import map.test.testmap.model.UserPermission;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtils {
    private static final String TAG = "HttpUtils";
    private static volatile HttpUtils manager = null;
    private HttpUtils client = null;
    private Retrofit retrofit = null;
    private IUserBiz userBiz = null;

    private HttpUtils(){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("map","返回数据为 = "+message);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        retrofit = new Retrofit.Builder().baseUrl(Constants.TEST_URL1)
                .client(client)
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

    public void getUserPermission(int userId, final OnResponseListener listener){
        Call<ResponseBean<UserPermission>> permissionInfo = userBiz.getUserPermissionInfo(userId);
        permissionInfo.enqueue(new retrofit2.Callback<ResponseBean<UserPermission>>() {
            @Override
            public void onResponse(Call<ResponseBean<UserPermission>> call, retrofit2.Response<ResponseBean<UserPermission>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<UserPermission>> call, Throwable t) {
                listener.fail(t);
            }
        });

    }

    public void  logOut(int userId, final OnResponseListener listener){
        Call<ResponseBean> permissionInfo = userBiz.logOut(userId);
        permissionInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
            @Override
            public void onResponse(Call<ResponseBean> call, retrofit2.Response<ResponseBean> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    /**
     * 单线程获取登录信息
     * @return
     */
    public Response<ResponseBean<User>> getLoginInfo(String account, String password){
        Call<ResponseBean<User>> login = userBiz.getLoginInfo(account,password);
        Response<ResponseBean<User>> user = null;
        try{
            user = login.execute();
        }catch (IOException e){

        }
        return user;
    }
}
