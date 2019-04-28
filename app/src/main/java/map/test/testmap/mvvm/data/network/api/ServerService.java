package map.test.testmap.mvvm.data.network.api;

import map.test.testmap.Constants;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 服务器交互接口
 */
public interface ServerService {

    @FormUrlEncoded
    @POST(Constants.TAG_GET_CHECK_TASK)
    Call<ResponseBean<TaskDetailBean>> getTaskDetail(@Field("checkId") int checkId);

    @FormUrlEncoded
    @POST(Constants.TAG_GET_SINGLE_POINT)
    Call<ResponseBean<Point>> getPointDetail(@Field("id") int pointId);
}
