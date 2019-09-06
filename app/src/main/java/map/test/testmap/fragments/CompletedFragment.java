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

import map.test.testmap.Constants;
import map.test.testmap.R;
import map.test.testmap.model.TaskBean;
import map.test.testmap.utils.TaskAdapter;

import static android.view.Gravity.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedFragment extends Fragment {
    private ArrayList<TaskBean> list;
    private RecyclerView recyclerView;

    public static CompletedFragment newInstance(ArrayList<TaskBean> dataList) {
        CompletedFragment fragment = new CompletedFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList("list",dataList);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle data) {

        if (getArguments() != null) {
            list = getArguments().getParcelableArrayList("list");
        }

        if(list != null && list.size() > 0){

            for (TaskBean item:list
                 ) {
                Log.d("http", "onCreateView: "+item.getStatusStr()+"==========="+item.getStatus());
            }

            View view = inflater.inflate(R.layout.fragment_task_list,null,false);
            recyclerView = view.findViewById(R.id.list);
            TaskAdapter adapter = new TaskAdapter(getActivity(),list,Constants.TYPE_TASK_COMPLETED);
            recyclerView.setLayoutManager (new LinearLayoutManager(getActivity (),LinearLayoutManager.VERTICAL,false));
            recyclerView.setItemAnimator (new DefaultItemAnimator());
            recyclerView.setAdapter (adapter);
            return view;
        }

        TextView textView = new TextView(getActivity());
        textView.setGravity(CENTER);
        textView.setText("当前无数据");
        return textView;
    }
}