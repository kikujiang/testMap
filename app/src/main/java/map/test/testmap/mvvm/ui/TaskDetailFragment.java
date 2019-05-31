package map.test.testmap.mvvm.ui;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import map.test.testmap.R;
import map.test.testmap.databinding.FragmentTaskDetailBinding;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import map.test.testmap.mvvm.util.TaskDetailAdapter;
import map.test.testmap.utils.HttpUtils;
import retrofit2.Response;

public class TaskDetailFragment extends Fragment {

    private static final String TAG = "task";

    private FragmentTaskDetailBinding binding;
    private int checkId;

    public static TaskDetailFragment newInstance(int checkId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle data = new Bundle();
        data.putInt("checkId",checkId);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            checkId = getArguments().getInt("checkId");
            Log.d("task", "TaskDetailFragment onCreateView check id: " + checkId);
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_task_detail, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new DetailTask().execute(checkId);
    }

    class DetailTask extends AsyncTask<Integer,Void,List<TaskDetailBean>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<TaskDetailBean> doInBackground(Integer[] ids) {
            try{
                int id = ids[0];
                Log.d(TAG, "doInBackground: called and check id is:"+id);
                Response<ResponseBean<TaskDetailBean>> response = HttpUtils.getInstance().getTaskDetail(id);
                return response.body().getList();
            }catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<TaskDetailBean> list) {

            super.onPostExecute(list);
            TaskDetailAdapter adapter = new TaskDetailAdapter(getActivity(),list);
            binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.list.setAdapter(adapter);
            binding.list.setItemAnimator (new DefaultItemAnimator());
            DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            Drawable drawable = getResources().getDrawable(R.drawable.divider);
            decoration.setDrawable(drawable);
            binding.list.addItemDecoration (decoration);
        }

    }
}
