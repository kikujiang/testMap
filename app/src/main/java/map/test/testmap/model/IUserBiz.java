package map.test.testmap.model;

import java.util.Map;

import map.test.testmap.Constants;
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
    Call<ResponseBean<User>> getLoginInfo(@Field("account") String account,@Field("password") String password,@Field("mac") String mac);

    @FormUrlEncoded
    @POST(Constants.TAG_USER_PERMISSION)
    Call<ResponseBean<UserPermission>>  getUserPermissionInfo(@Field("id") int userId);

    @FormUrlEncoded
    @POST(Constants.TAG_LOGOUT)
    Call<ResponseBean> logOut(@Field("id") int userId);

    @GET(Constants.TAG_UPDATE)
    Call<ResponseBean> getApkUpdateInfo();

    @FormUrlEncoded
    @POST(Constants.TAG_GET_CHECK_HISTORY)
    Call<ResponseBean<ResponseCheckHistory>> getCheckHistoryInfo(@Field("tag_id") int id);

    @Multipart
    @POST(Constants.TAG_SAVE_CHECK)
    Call<ResponseBean> saveHistoryInfo(@PartMap Map<String, RequestBody> params);

    @Multipart
    @POST(Constants.TAG_SAVE_LINE_CHECK)
    Call<ResponseBean> addLineCheck(@PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_CHECK_LINE_HISTORY)
    Call<ResponseBean<ResponseCheckHistory>> getLineCheckHistoryInfo(@Field("lineId") int id);
}
