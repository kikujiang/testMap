package map.test.testmap.mvvm.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import map.test.testmap.mvvm.data.model.TaskDetailBean;
import map.test.testmap.mvvm.data.network.NetworkService;

public class NetworkRepository {

    private static NetworkRepository repository = null;

    private NetworkService service;
    private NetworkRepository(){

    }

    public static NetworkRepository getInstance(){
        if(repository == null){
            synchronized (NetworkRepository.class){
                if(repository == null){
                    repository = new NetworkRepository();
                }
            }
        }

        return repository;
    }

    public LiveData<TaskDetailBean> getTaskDetail(int userId) {
        return service.getTaskDetail(userId);
    }
}
