package map.test.testmap;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import map.test.testmap.model.LoadMoreOnScrollListener;
import map.test.testmap.model.LoadMoreWrapper;
import map.test.testmap.model.Notice;
import map.test.testmap.model.NoticeAdapter;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.DataBaseUtils;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.PreferencesUtils;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private static final String TAG = "message";
    
    private RecyclerView list;
    private TextView emptyView;
    private SwipeRefreshLayout refreshLayout;
    private NoticeAdapter adapter;

    private List<Notice> noticeUnreadList;
    private List<Notice> cacheList;

    private Toolbar toolbar;
    private boolean needSubmit;

    private int count = 10;
    private int index = 0;
    private List<Notice> currentList;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        init(view);
        return view;
    }

    private void init(View v){
        list = v.findViewById(R.id.notice_list);
        emptyView = v.findViewById(R.id.notice_empty);

        toolbar = v.findViewById(R.id.settings_toolbar);
        refreshLayout = v.findViewById(R.id.refresh_layout);
        
        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        noticeUnreadList = new ArrayList<>();

        refreshLayout.setRefreshing(false);

        //设置下拉时圆圈的颜色（可以由多种颜色拼成）
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);

        //设置下拉时圆圈的背景颜色（这里设置成白色）
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        //设置下拉刷新时的操作
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //具体操作
                long time = PreferencesUtils.getLong(getActivity(),String.valueOf(Constants.userId)+"123456",0L);
                Log.d(TAG, "onRefresh: called! time is:"+ time);
                getNotice(time);
            }
        });

        initData();
    }

    private void initData(){
        cacheList = DataBaseUtils.getInstance().findAllUserMessage();
        currentList = new ArrayList<>();

        if(Constants.noticeCount > 0){
            Constants.noticeCount = 0;
            long time = PreferencesUtils.getLong(getActivity(),String.valueOf(Constants.userId)+"123456",0L);
            getNotice(time);
        }else{
            if(null == cacheList || cacheList.isEmpty()){
                long time = PreferencesUtils.getLong(getActivity(),String.valueOf(Constants.userId)+"123456",0L);
                getNotice(time);
            }else{

                for (Notice item:
                     cacheList) {
                    if (item.getReadStatus() == 2){
                        needSubmit = true;
                        noticeUnreadList.add(item);
                    }
                }

                emptyView.setVisibility(View.GONE);
                initList();
            }
        }
    }

    private void initList(){
        index = 0;
        currentList.clear();
        if(cacheList.size() > count){
            for (int i=0;i<count;i++){
                index = i;
                currentList.add(cacheList.get(i));
            }
        }else{
            index = cacheList.size();
            currentList =  cacheList;
        }
        adapter = new NoticeAdapter(getActivity(),currentList);
        LoadMoreWrapper loadMoreWrapper = new LoadMoreWrapper(adapter);
        list.setLayoutManager (new LinearLayoutManager(getActivity (),LinearLayoutManager.VERTICAL,false));
        list.setItemAnimator (new DefaultItemAnimator());
        list.setAdapter (loadMoreWrapper);


        list.addOnScrollListener(new LoadMoreOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: haha:"+cacheList.size()+",index is:" + index);
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);

                int totalCount = cacheList.size();

                if (totalCount > index-1) {
                    if((totalCount - index) > count){
                        for (int i=0;i<count;i++){
                            index = index+1;
                            Log.d(TAG, "onLoadMore2: index is:" + index);
                            currentList.add(cacheList.get(index));
                        }
                        loadMoreWrapper.notifyDataSetChanged();
                    }else{
                        for (int i=0;i<totalCount - index-1;i++){
                            index = index+1;
                            Log.d(TAG, "onLoadMore3: index is:" + index);
                            currentList.add(cacheList.get(index));
                        }
                        loadMoreWrapper.notifyDataSetChanged();
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                    }
                } else {
                    // 显示加载到底的提示
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                }
            }
        });
    }


    private void getNotice(long time){
        Log.d(TAG, "getNotice: called!");
        HttpUtils.getInstance().getNotice(time,new OnResponseListener() {
            @Override
            public void success(retrofit2.Response responseMapBean) {
                refreshLayout.setRefreshing(false);
                if(responseMapBean == null){
                    Log.d(TAG, "服务器返回对象为空");
                    showEmpty();
                    return;
                }

                ResponseBean<Notice> current = (ResponseBean<Notice>)responseMapBean.body();
                if(current.getResult() == Constants.RESULT_FAIL){
                    if(current.getDesc().equals("登陆已过期,请重新登陆")){
                        Toast.makeText(getActivity(),"登陆已过期,请重新登陆",Toast.LENGTH_LONG).show();
                        PreferencesUtils.putString(getActivity(),"account",null);
                        PreferencesUtils.putString(getActivity(),"password_selector",null);
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                    showEmpty();
                }

                if(current.getResult() == Constants.RESULT_OK){

                    long currentTime = current.getTime();
                    PreferencesUtils.putLong(getActivity(),String.valueOf(Constants.userId)+"123456",currentTime);
                    final List<Notice> noticeList = current.getList();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: ");
                            if(noticeList.size() > 0){
                                Log.d(TAG, "run: 1");
                                noticeUnreadList.clear();
                                for (Notice item:noticeList) {
                                    int id = item.getId();

                                    if(Constants.messageId < id){
                                        Constants.messageId = id;
                                    }

                                    if(item.getReadStatus() == 2){
                                        needSubmit = true;
                                        noticeUnreadList.add(item);
                                    }

                                    if(cacheList != null && cacheList.size() > 0){
                                        boolean isExist = false;
                                        for (Notice item1:cacheList){
                                            if(item1.getId() == id){
                                                isExist = true;
                                                DataBaseUtils.getInstance().updateMessage(item);
                                                break;
                                            }
                                        }

                                        if (!isExist){
                                            DataBaseUtils.getInstance().insertMessage(item);
                                        }

                                    }else{
                                        DataBaseUtils.getInstance().insertMessage(item);
                                    }
                                }

                                cacheList.clear();

                                try {
                                    Thread.sleep(1*1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                cacheList = DataBaseUtils.getInstance().findAllUserMessage();

                                emptyView.setVisibility(View.GONE);

                                initList();

                                PreferencesUtils.putInt(getActivity(),String.valueOf(Constants.userId),Constants.messageId);

                            }else{

                                if (cacheList != null && cacheList.size() > 0){
                                    Log.d(TAG, "run: 2");
                                    cacheList = DataBaseUtils.getInstance().findAllUserMessage();
                                    emptyView.setVisibility(View.GONE);
                                    initList();

                                    PreferencesUtils.putInt(getActivity(),String.valueOf(Constants.userId),Constants.messageId);
                                }else {
                                    Log.d(TAG, "run: 3");
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });

                }
            }

            @Override
            public void fail(Throwable e) {
                refreshLayout.setRefreshing(false);
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
        Log.d(TAG, "onHiddenChanged: called");
        if(!hidden){

            if(Constants.noticeCount > 0){
                Constants.noticeCount = 0;
                getNotice(0L);
            }else if(noticeUnreadList.size() > 0){
                list.setAdapter(null);
                long time = PreferencesUtils.getLong(getActivity(),String.valueOf(Constants.userId)+"123456",0L);
                getNotice(0L);
            }

        }else{
            if (needSubmit && noticeUnreadList.size() > 0){
                needSubmit = false;
                for (Notice item:
                     noticeUnreadList) {
                    Log.d(TAG, "开始设置消息为已读状态,消息id为:" + item.getId());
                    HttpUtils.getInstance().submitNotice(item.getId(), new OnResponseListener() {
                        @Override
                        public void success(Response responseMapBean) {
                            Log.d(TAG, "success: "+responseMapBean.body());
                        }

                        @Override
                        public void fail(Throwable e) {
                            Log.d(TAG, "fail: "+e.getMessage());
                        }
                    });
                }
            }
        }
    }
}