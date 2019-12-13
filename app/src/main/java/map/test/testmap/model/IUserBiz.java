package map.test.testmap.model;

import java.util.Map;

import map.test.testmap.Constants;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface IUserBiz {

    @FormUrlEncoded
    @POST(Constants.TAG_LOGIN)
    Call<ResponseBean<User>> getLoginInfo(@Field("account") String account,@Field("password") String password,@Field("mac") String mac,@Field("channelId") String channelId);

    @FormUrlEncoded
    @POST(Constants.TAG_USER_PERMISSION)
    Call<ResponseBean<UserPermission>>  getUserPermissionInfo(@Field("id") int userId);

    @FormUrlEncoded
    @POST(Constants.TAG_USER_INFO)
    Call<ResponseBean<User>>  getUserInfo(@Field("id") int userId);

    @FormUrlEncoded
    @POST(Constants.TAG_LOGOUT)
    Call<ResponseBean> logOut(@Field("id") int userId);

    @GET(Constants.TAG_UPDATE)
    Call<ResponseBean> getApkUpdateInfo();

    @FormUrlEncoded
    @POST(Constants.TAG_GET_CHECK_HISTORY)
    Call<ResponseBean<ResponseCheckHistory>> getCheckHistoryInfo(@Field("tag_id") int id);

    @Multipart
    @POST(Constants.TAG_PUT_BACK_TASK)
    Call<ResponseBean> putBackTask(@PartMap Map<String, RequestBody> params);

    @Multipart
    @POST(Constants.TAG_SAVE_CHECK)
    Call<ResponseBean> saveHistoryInfo(@PartMap Map<String, RequestBody> params);

    @Multipart
    @POST(Constants.TAG_EDIT_TAG_CHECK)
    Call<ResponseBean> editTagCheck(@PartMap Map<String, RequestBody> params);

    @Multipart
    @POST(Constants.TAG_SAVE_LINE_CHECK)
    Call<ResponseBean> addLineCheck(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_CHECK_LINE_HISTORY)
    Call<ResponseBean<ResponseCheckHistory>> getLineCheckHistoryInfo(@Field("lineId") int id);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_LINE_HISTORY)
    Call<ResponseBean<ResponseHistory>> getLineHistoryInfo(@Field("id") int id);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_POINT_HISTORY)
    Call<ResponseBean<ResponseHistory>> getPointHistoryInfo(@Field("id") int id);

    @FormUrlEncoded
    @POST(Constants.TAG_SAVE_ASSIST_POINT)
    Call<ResponseBean> saveAssistPoint(@Field("name") String name,@Field("location_long") double location_long,@Field("location_lat") double location_lat,@Field("lineId") int lineId);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_NOTICE)
    Call<ResponseBean<Notice>> getNoticeInfo(@Field("time") long time);

    @GET(Constants.TAG_GET_TASK_INFO)
    Call<ResponseBean<TaskInfo>> getTaskInfo();

    @FormUrlEncoded
    @POST(Constants.TAG_SET_NOTICE)
    Call<ResponseBean> submitNotice(@Field("id") int id);

    @GET(Constants.TAG_GET_TASK_LIST)
    Call<ResponseBean<ResponseTaskBean>> getTaskList();

    @GET(Constants.TAG_GET_NEXT_STEP_LIST)
    Call<ResponseBean<ResponseTaskUserBean>> getNextUserList();

    @FormUrlEncoded
    @POST(Constants.TAG_GET_CHECK_TASK)
    Call<ResponseBean<TaskDetailBean>>
    getTaskDetail(@Field("checkId") int checkId);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_SINGLE_POINT)
    Call<ResponseBean<Point>> getPointDetail(@Field("id") int pointId);

    @FormUrlEncoded
    @POST(Constants.TAG_LOGIN_QRCODE)
    Call<ResponseBean> scanQRCode(@Field("loginSign") String loginSign,@Field("login") String login);

    @FormUrlEncoded
    @POST(Constants.TAG_LOGIN_QRCODE_CONFIRM)
    Call<ResponseBean> confirmQRCode(@Field("loginSign") String loginSign,@Field("login") String login);

    @GET(Constants.TAG_GET_WX_CODE)
    Call<ResponseBean> getWXCode();

    @GET(Constants.TAG_GET_WX_CODE_IMG)
    Call<ResponseBean> getWXCodeImg();
}
