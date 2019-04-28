package map.test.testmap.mvvm.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import map.test.testmap.mvvm.data.NetworkRepository;
import map.test.testmap.mvvm.data.model.TaskDetailBean;

public class TaskDetailViewModel extends ViewModel {

    private MutableLiveData<TaskDetailBean> taskDetail;
    private NetworkRepository repository;

    public MutableLiveData<TaskDetailBean> getTaskDetail() {
        return taskDetail;
    }
}
