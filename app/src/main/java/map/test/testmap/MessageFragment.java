package map.test.testmap;


import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import map.test.testmap.model.Notice;
import map.test.testmap.model.NoticeAdapter;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import q.rorbin.badgeview.QBadgeView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private static final String TAG = "message";
    
    private RecyclerView list;
    private TextView emptyView;
    private NoticeAdapter adapter;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        init(view);
        return view;
    }

    private void init(View v){
        list = v.findViewById(R.id.notice_list);
        emptyView = v.findViewById(R.id.notice_empty);
        getNotice();
    }

    private void getNotice(){
        HttpUtils.getInstance().getNotice(new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {
                if(responseMapBean == null){
                    Log.d(TAG, "服务器返回对象为空");
                    showEmpty();
                    return;
                }

                ResponseBean<Notice> current = (ResponseBean<Notice>)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    showEmpty();
                }

                if(current.getResult() == Constants.RESULT_OK){
                    final List<Notice> noticeList = current.getList();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: ");
                            if(noticeList.size() > 0){
                                emptyView.setVisibility(View.GONE);
                                adapter = new NoticeAdapter(getActivity(),noticeList);
                                list.setLayoutManager (new LinearLayoutManager(getActivity (),LinearLayoutManager.VERTICAL,false));
                                list.setItemAnimator (new DefaultItemAnimator());
                                list.setAdapter (adapter);
                                list.addItemDecoration (new DividerItemDecoration(getActivity (),DividerItemDecoration.VERTICAL));
                            }else{
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
            }

            @Override
            public void fail(Throwable e) {
                Log.d(TAG, "fail: "+e.getMessage());
                showEmpty();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void showEmpty(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.d(TAG, "onHiddenChanged: called");
            list.setAdapter(null);
            getNotice();
        }
    }


}
