package map.test.testmap.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ToBeVerifyFragment extends Fragment {

    private ArrayList<TaskBean> list;
    private RecyclerView recyclerView;

    public static ToBeVerifyFragment newInstance(ArrayList<TaskBean> dataList) {
        ToBeVerifyFragment fragment = new ToBeVerifyFragment();
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
            View view = inflater.inflate(R.layout.fragment_task_list,null,false);
            recyclerView = view.findViewById(R.id.list);
            TaskAdapter adapter = new TaskAdapter(getActivity(),list,Constants.TYPE_TASK_TO_BE_VERIFY);
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