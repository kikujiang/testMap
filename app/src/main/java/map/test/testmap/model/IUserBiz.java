package map.test.testmap.model;

import map.test.testmap.Constants;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IUserBiz {

    @GET(Constants.TEST_SPLITER + Constants.TAG_LOGIN)
    Call<User> getLoginInfo();


}
