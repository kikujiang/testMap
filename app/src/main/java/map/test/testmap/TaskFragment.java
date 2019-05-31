package map.test.testmap;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;

import map.test.testmap.fragments.CompletedFragment;
import map.test.testmap.fragments.PublishedFragment;
import map.test.testmap.fragments.ToBeCompletedFragment;
import map.test.testmap.fragments.ToBeVerifyFragment;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseTaskBean;
import map.test.testmap.utils.HttpUtils;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    private static final String TAG = "task";

    private ViewPager pager;
    private TabLayout tab;
    private LinearLayout loadingLayout;
    private Toolbar toolbar;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        tab = view.findViewById(R.id.task_tab);
        pager = view.findViewById(R.id.task_pager);
        loadingLayout = view.findViewById(R.id.loading);
        toolbar = view.findViewById(R.id.task_toolbar);

        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        new MyTask().execute();
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.d(TAG, "onHiddenChanged: called");
            new MyTask().execute();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called!");
        super.onResume();
        new MyTask().execute();
    }

    class MyTask extends AsyncTask<Void,Void,ResponseTaskBean>{

        @Override
        protected void onPreExecute() {
            loadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected ResponseTaskBean doInBackground(Void... voids) {
            try{
                Response<ResponseBean<ResponseTaskBean>> response = HttpUtils.getInstance().getTaskList();
                Log.d(TAG, "doInBackground: called");
                return response.body().getObject();
            }catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseTaskBean result) {
            loadingLayout.setVisibility(View.GONE);
            String[] titles = new String[]{"已发布","待修复","待审核","已完成"};
            Log.d(TAG, "onPostExecute: "+result);
            ResponseTaskBean responseTaskBean = null;
            if(result != null){
                responseTaskBean = result;
            }else {
                responseTaskBean.setTasks1(null);
                responseTaskBean.setTasks2(null);
                responseTaskBean.setTasks3(null);
                responseTaskBean.setTasks4(null);
            }
            pager.setAdapter(null);
            MyAdapter adapter = new MyAdapter(getActivity().getSupportFragmentManager(),responseTaskBean,titles);
            pager.setAdapter(adapter);
            Log.d(TAG, "onPostExecute: "+Constants.currentIndex);

            pager.setCurrentItem(Constants.currentIndex);

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    Log.d(TAG, "onPageSelected: "+i);
                    Constants.currentIndex = i;
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            tab.setupWithViewPager(pager);
        }
    }
}

class MyAdapter extends FragmentStatePagerAdapter {

    private String[] mTitles;

    private PublishedFragment publishedFragment;
    private ToBeCompletedFragment toBeCompletedFragment;
    private ToBeVerifyFragment toBeVerifyFragment;
    private CompletedFragment completedFragment;
    private ResponseTaskBean dataList;

    public MyAdapter(FragmentManager f,ResponseTaskBean taskBean,String[] titles){
        super(f);
        this.dataList = taskBean;
        this.mTitles = titles;
        Log.d("task", "MyAdapter: size1:"+dataList.getTasks1().size()+"----size2:" + dataList.getTasks2().size()+"----size3:"+dataList.getTasks3().size()+"----size4:"+dataList.getTasks4().size());
        publishedFragment = null;
        toBeCompletedFragment = null;
        toBeVerifyFragment = null;
        completedFragment = null;
    }

    private static final String TAG = "task";

    @Override
    public Fragment getItem(int i) {
        Fragment cur = null;

        switch (i){
            case 0:
                if(publishedFragment == null){
                    publishedFragment = PublishedFragment.newInstance(dataList.getTasks1());
                }
                cur = publishedFragment;
                break;
            case 1:
                if(toBeCompletedFragment == null){
                    toBeCompletedFragment = ToBeCompletedFragment.newInstance(dataList.getTasks2());
                }
                cur = toBeCompletedFragment;
                break;
            case 2:
                if(toBeVerifyFragment == null){
                    toBeVerifyFragment = ToBeVerifyFragment.newInstance(dataList.getTasks3());
                }
                cur = toBeVerifyFragment;
                break;
            case 3:
                if(completedFragment == null){
                    completedFragment = CompletedFragment.newInstance(dataList.getTasks4());
                }
                cur = completedFragment;
                break;
        }

        return cur;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}