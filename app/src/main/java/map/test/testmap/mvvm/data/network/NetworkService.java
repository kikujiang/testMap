package map.test.testmap.mvvm.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import map.test.testmap.mvvm.data.network.api.ServerService;
import retrofit2.Call;

public class NetworkService {

    private static NetworkService instance = null;
    private ServerService service = null;

    private NetworkService(){
        ServiceCreator creator = new ServiceCreator();
        service = (ServerService) creator.create(ServerService.class);
    }

    public static NetworkService getInstance(){
        if(instance == null){
            synchronized (NetworkService.class){
                if(instance == null){
                    instance = new NetworkService();
                }
            }
        }

        return instance;
    }

    /**
     * 获取任务详细信息
     * @param pointId 标记点id
     */
    public LiveData<TaskDetailBean> getTaskDetail(int pointId){
        final MutableLiveData<TaskDetailBean> detail = new MutableLiveData<>();
        Call<ResponseBean<TaskDetailBean>> taskDetailInfo = service.getTaskDetail(pointId);
        taskDetailInfo.enqueue(new retrofit2.Callback<ResponseBean<TaskDetailBean>>() {
            @Override
            public void onResponse(Call<ResponseBean<TaskDetailBean>> call, retrofit2.Response<ResponseBean<TaskDetailBean>> response) {
                detail.setValue(response.body().getList().get(0));
            }

            @Override
            public void onFailure(Call<ResponseBean<TaskDetailBean>> call, Throwable t) {
            }
        });

        return detail;
    }
}
