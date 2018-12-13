package map.test.testmap.model;

import map.test.testmap.Constants;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IUserBiz {

    @GET(Constants.TAG_LOGIN)
    Call<User> getLoginInfo();


}
