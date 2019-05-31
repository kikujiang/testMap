package map.test.testmap.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import map.test.testmap.Constants;
import map.test.testmap.R;
import map.test.testmap.model.TaskBean;
import map.test.testmap.utils.TaskAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishedFragment extends Fragment {

    private static final String TAG = "task";

    private ArrayList<TaskBean> list;
    private RecyclerView recyclerView;

    public static PublishedFragment newInstance(ArrayList<TaskBean> dataList) {
        PublishedFragment fragment = new PublishedFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList("list",dataList);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "PublishedFragment onHiddenChanged: ");
        super.onHiddenChanged(hidden);

        if (hidden){
            Log.d(TAG, "PublishedFragment onHiddenChanged: "+hidden);
        }else{
            Log.d(TAG, "PublishedFragment onHiddenChanged: "+hidden);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "PublishedFragment onResume: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle data) {

        if (getArguments() != null) {
            list = getArguments().getParcelableArrayList("list");
            Log.d("task", "PublishedFragment onCreateView: "+list.size());
        }

        if(list != null && list.size() > 0){
            View view = inflater.inflate(R.layout.fragment_task_list,null,false);
            recyclerView = view.findViewById(R.id.list);
            TaskAdapter adapter = new TaskAdapter(getActivity(),list,Constants.TYPE_TASK_PUBLISH);
            recyclerView.setLayoutManager (new LinearLayoutManager(getActivity (),LinearLayoutManager.VERTICAL,false));
            recyclerView.setItemAnimator (new DefaultItemAnimator());
            recyclerView.setAdapter (adapter);
            return view;
        }

        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setText("当前无数据");
        return textView;
    }
}