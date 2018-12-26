package map.test.testmap.model;

import map.test.testmap.Constants;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IUserBiz {

    @FormUrlEncoded
    @POST(Constants.TEST_SPLITTER + Constants.TAG_LOGIN)
    Call<ResponseBean<User>> getLoginInfo(@Field("account") String account,@Field("password") String password);

    @FormUrlEncoded
    @POST(Constants.TEST_SPLITTER + Constants.TAG_USER_PERMISSION)
    Call<ResponseBean<UserPermission>>  getUserPermissionInfo(@Field("id") int userId);

    @FormUrlEncoded
    @POST(Constants.TEST_SPLITTER + Constants.TAG_LOGOUT)
    Call<ResponseBean> logOut(@Field("id") int userId);
}
