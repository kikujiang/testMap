package map.test.testmap.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.test.testmap.Constants;
import map.test.testmap.model.IUserBiz;
import map.test.testmap.model.Notice;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseCheckHistory;
import map.test.testmap.model.ResponseHistory;
import map.test.testmap.model.ResponseTaskBean;
import map.test.testmap.model.ResponseTaskUserBean;
import map.test.testmap.model.TaskInfo;
import map.test.testmap.model.User;
import map.test.testmap.model.UserPermission;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
                Log.i("http","交互数据为 = "+message);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
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

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        retrofit = new Retrofit.Builder().baseUrl(Constants.WEB_URL)
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



    public void getUserInfo(int userId, final OnResponseListener listener){
        Call<ResponseBean<User>> userInfo = userBiz.getUserInfo(userId);
        userInfo.enqueue(new retrofit2.Callback<ResponseBean<User>>() {
            @Override
            public void onResponse(Call<ResponseBean<User>> call, retrofit2.Response<ResponseBean<User>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<User>> call, Throwable t) {
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

    public void  scanQRCode(String loginSign,String login, final OnResponseListener listener){
        Log.d(TAG, "confirmQRCode: loginSign:" + loginSign+",login"+login);
        Call<ResponseBean> QRCodeInfo = userBiz.scanQRCode(loginSign,login);
        QRCodeInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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

    public void  confirmQRCode(String loginSign,String login,final OnResponseListener listener){
        Log.d(TAG, "confirmQRCode: loginSign:" + loginSign+",login"+login);
        Call<ResponseBean> confirmQRCodeInfo = userBiz.confirmQRCode(loginSign,login);
        confirmQRCodeInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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
    public Response<ResponseBean<User>> getLoginInfo(String account, String password,String mac,String channelId){
        Log.d(TAG, "getLoginInfo: "+account+","+password+","+mac+","+channelId);
        Call<ResponseBean<User>> login = userBiz.getLoginInfo(account,password,mac,channelId);
        Response<ResponseBean<User>> user = null;
        try{
            user = login.execute();
        }catch (IOException e){

        }
        return user;
    }

    /**
     * 获取更新信息
     * @param listener
     */
    public void checkUpdate(final OnResponseListener listener){
        Call<ResponseBean> updateInfo = userBiz.getApkUpdateInfo();
        updateInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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
     * 获取消息信息
     * @param listener
     */
    public void getNotice(long time,final OnResponseListener listener){
        Call<ResponseBean<Notice>> noticeInfo = userBiz.getNoticeInfo(time);
        noticeInfo.enqueue(new retrofit2.Callback<ResponseBean<Notice>>() {
            @Override
            public void onResponse(Call<ResponseBean<Notice>> call, retrofit2.Response<ResponseBean<Notice>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<Notice>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    public void getCheckHistoryInfo(int id,final OnResponseListener listener){
        Call<ResponseBean<ResponseCheckHistory>> historyInfo = userBiz.getCheckHistoryInfo(id);
        historyInfo.enqueue(new retrofit2.Callback<ResponseBean<ResponseCheckHistory>>() {
            @Override
            public void onResponse(Call<ResponseBean<ResponseCheckHistory>> call, retrofit2.Response<ResponseBean<ResponseCheckHistory>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<ResponseCheckHistory>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    public void getLineCheckHistoryInfo(int id,final OnResponseListener listener){
        Call<ResponseBean<ResponseCheckHistory>> historyInfo = userBiz.getLineCheckHistoryInfo(id);
        historyInfo.enqueue(new retrofit2.Callback<ResponseBean<ResponseCheckHistory>>() {
            @Override
            public void onResponse(Call<ResponseBean<ResponseCheckHistory>> call, retrofit2.Response<ResponseBean<ResponseCheckHistory>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<ResponseCheckHistory>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    /**
     * 设置指定消息为已读
     * @param id 消息id
     * @param listener
     */
    public void submitNotice(int id,final OnResponseListener listener){
        Call<ResponseBean> info = userBiz.submitNotice(id);
        info.enqueue(new retrofit2.Callback<ResponseBean>() {
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

    public void putBackTask(int checkId,String remark,List<String> imagePaths,final OnResponseListener listener){

        Map<String,RequestBody> params = new HashMap<>();

        params.put("checkId",toRequestBody(checkId+""));
        params.put("remark",toRequestBody(remark));

        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            params.put("images\";filename=\""+file.getName(),RequestBody.create(OkHttpClientManager.MEDIA_TYPE_JPG,file));
        }

        Call<ResponseBean> backTaskInfo = userBiz.putBackTask(params);
        backTaskInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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

    public void saveCheckInfo(int id,int checkId,int status,String remark,String userId,List<String> imagePaths,final OnResponseListener listener){

        Map<String,RequestBody> params = new HashMap<>();

        params.put("tag_id",toRequestBody(id+""));
        params.put("checkId",toRequestBody(checkId+""));
        params.put("status",toRequestBody(status+""));
        params.put("putUserIds",toRequestBody(userId));
        params.put("remark",toRequestBody(remark));

        Log.d(TAG, "saveCheckInfo: "+imagePaths.size());

        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            Log.d(TAG, "saveCheckInfo path is: "+imagePaths.get(i)+",and file exists is:"+ file.exists());
            params.put("images\";filename=\""+file.getName(),RequestBody.create(OkHttpClientManager.MEDIA_TYPE_JPG,file));
        }

        Call<ResponseBean> historyInfo = userBiz.saveHistoryInfo(params);
        historyInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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

    public RequestBody toRequestBody(String value){
        return RequestBody.create(MediaType.parse("text/plain"),value);
    }

    public void saveLineTagInfo(int userId,int id,int tagCheckId,String name,String remark,int lineId,int status,String location_lat,String location_long,List<String> imagePaths,final OnResponseListener listener){
        Map<String,RequestBody> params = new HashMap<>();

        params.put("id",toRequestBody(id+""));
        params.put("putUserIds",toRequestBody(userId+""));
        params.put("name",toRequestBody(name));
        params.put("location_lat",toRequestBody(location_lat));
        params.put("location_long",toRequestBody(location_long));
        params.put("type",toRequestBody("101"));
        params.put("lineId",toRequestBody(lineId+""));
        params.put("status",toRequestBody(status+""));
        params.put("tagCheckId",toRequestBody(tagCheckId+""));
        params.put("remark",toRequestBody(remark));

        Log.d(TAG, "saveLineTagInfo: "+imagePaths.size());
        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            params.put("images\";filename=\""+file.getName(),RequestBody.create(OkHttpClientManager.MEDIA_TYPE_JPG,file));
        }

        Call<ResponseBean> lineCheckInfo = userBiz.addLineCheck(params);
        lineCheckInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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

    public void saveAssistPoint(String name, double lat,double lng,int lineId,final OnResponseListener listener){
        Call<ResponseBean> historyInfo = userBiz.saveAssistPoint(name,lng,lat,lineId);
        historyInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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

    public void getPointHistoryInfo(int id,final OnResponseListener listener){
        Call<ResponseBean<ResponseHistory>> historyInfo = userBiz.getPointHistoryInfo(id);
        historyInfo.enqueue(new retrofit2.Callback<ResponseBean<ResponseHistory>>() {
            @Override
            public void onResponse(Call<ResponseBean<ResponseHistory>> call, retrofit2.Response<ResponseBean<ResponseHistory>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<ResponseHistory>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    public void getLineHistoryInfo(int id,final OnResponseListener listener){
        Call<ResponseBean<ResponseHistory>> historyInfo = userBiz.getLineHistoryInfo(id);
        historyInfo.enqueue(new retrofit2.Callback<ResponseBean<ResponseHistory>>() {
            @Override
            public void onResponse(Call<ResponseBean<ResponseHistory>> call, retrofit2.Response<ResponseBean<ResponseHistory>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<ResponseHistory>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    public Response<ResponseBean<ResponseTaskBean>> getTaskList() throws IOException{
        Call<ResponseBean<ResponseTaskBean>> taskList = userBiz.getTaskList();
        return taskList.execute();
    }

    public void getNextUserList(final OnResponseListener listener){
        Call<ResponseBean<ResponseTaskUserBean>> userListInfo = userBiz.getNextUserList();
        userListInfo.enqueue(new retrofit2.Callback<ResponseBean<ResponseTaskUserBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<ResponseTaskUserBean>> call, retrofit2.Response<ResponseBean<ResponseTaskUserBean>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<ResponseTaskUserBean>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    /**
     * 获取任务详细信息
     * @param checkId 标记点id
     */
    public Response<ResponseBean<TaskDetailBean>> getTaskDetail(int checkId) throws IOException{
        Call<ResponseBean<TaskDetailBean>> taskDetailInfo = userBiz.getTaskDetail(checkId);
        return taskDetailInfo.execute();
    }

    /**
     * 获取标记点详细信息
     * @param id 标记点id
     */
    public Response<ResponseBean<Point>> getPointDetail(int id) throws IOException{
        Call<ResponseBean<Point>> pointDetailInfo = userBiz.getPointDetail(id);
        return pointDetailInfo.execute();
    }

    /**
     * 获取任务统计消息
     * @param listener
     */
    public void getTaskInfo(final OnResponseListener listener){
        Call<ResponseBean<TaskInfo>> userListInfo = userBiz.getTaskInfo();
        userListInfo.enqueue(new retrofit2.Callback<ResponseBean<TaskInfo>>() {
            @Override
            public void onResponse(Call<ResponseBean<TaskInfo>> call, retrofit2.Response<ResponseBean<TaskInfo>> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<ResponseBean<TaskInfo>> call, Throwable t) {
                listener.fail(t);
            }
        });
    }

    /**
     * 编辑发布任务接口
     * @param id 需要编辑的任务id
     * @param status    任务的状态
     * @param remark 任务的备注信息
     * @param userId    任务发布的检修人员id
     * @param imagePaths 图片地址
     * @param listener  接口回调
     */
    public void editTagCheck(int id,int status,String remark,int userId,List<String> imagePaths,final OnResponseListener listener){

        Map<String,RequestBody> params = new HashMap<>();

        params.put("id",toRequestBody(id+""));
        params.put("status",toRequestBody(status+""));
        params.put("putUserId",toRequestBody(userId+""));
        params.put("remark",toRequestBody(remark));

        Log.d(TAG, "editTagCheck: "+imagePaths.size());

        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            params.put("images\";filename=\""+file.getName(),RequestBody.create(OkHttpClientManager.MEDIA_TYPE_JPG,file));
        }

        Call<ResponseBean> editTagCheckBean = userBiz.editTagCheck(params);
        editTagCheckBean.enqueue(new retrofit2.Callback<ResponseBean>() {
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
     * 获取微信专用码信息
     * @param listener
     */
    public void getWXCode(final OnResponseListener listener){
        Call<ResponseBean> updateInfo = userBiz.getWXCode();
        updateInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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
     * 获取微信专用码图片地址
     * @param listener
     */
    public void getWXCodeImg(final OnResponseListener listener){
        Call<ResponseBean> updateInfo = userBiz.getWXCodeImg();
        updateInfo.enqueue(new retrofit2.Callback<ResponseBean>() {
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
}
