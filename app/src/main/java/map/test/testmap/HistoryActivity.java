package map.test.testmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import map.test.testmap.model.HistoryAdapter;
import map.test.testmap.model.Notice;
import map.test.testmap.model.NoticeAdapter;
import map.test.testmap.model.OnResponseListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.model.ResponseHistory;
import map.test.testmap.utils.HttpUtils;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "history";
    
    private Toolbar toolbar;
    private LinearLayout loadingLayout;
    private TextView emptyTv;
    private ListView historyList;
    private int historyId;
    private int historyType;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyId = getIntent().getIntExtra("history_id",-1);
        historyType = getIntent().getIntExtra("history_type",-1);
        init();
    }

    private void init(){
        toolbar = findViewById(R.id.history_toolbar);
        loadingLayout = findViewById(R.id.history_loading);
        historyList = findViewById(R.id.list_history);
        emptyTv = findViewById(R.id.history_empty);
        initToolBar();
        initList();
    }

    private void initToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initList(){
        switch (historyType){
            case Constants.TYPE_HISTORY_POINT:
                HttpUtils.getInstance().getPointHistoryInfo(historyId, new OnResponseListener() {
                    @Override
                    public void success(Response responseMapBean) {
                        if(responseMapBean == null){
                            Log.d(TAG, "服务器返回对象为空");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingLayout.setVisibility(View.GONE);
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            });
                            return;
                        }

                        ResponseBean<ResponseHistory> current = (ResponseBean<ResponseHistory>)responseMapBean.body();
                        if(current.getResult() == Constants.RESULT_FAIL){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingLayout.setVisibility(View.GONE);
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            });

                        }

                        if(current.getResult() == Constants.RESULT_OK){
                            final List<ResponseHistory> historyData = current.getList();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(historyData.size() > 0){
                                        loadingLayout.setVisibility(View.GONE);
                                        emptyTv.setVisibility(View.GONE);
                                        if(adapter == null){
                                            adapter = new HistoryAdapter(HistoryActivity.this,historyData);
                                            historyList.setAdapter(adapter);
                                        }else {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }else{
                                        loadingLayout.setVisibility(View.GONE);
                                        emptyTv.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void fail(Throwable e) {
                        loadingLayout.setVisibility(View.GONE);
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case Constants.TYPE_HISTORY_LINE:
                HttpUtils.getInstance().getLineHistoryInfo(historyId, new OnResponseListener() {
                    @Override
                    public void success(Response responseMapBean) {
                        if(responseMapBean == null){
                            Log.d(TAG, "服务器返回对象为空");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingLayout.setVisibility(View.GONE);
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            });
                            return;
                        }

                        ResponseBean<ResponseHistory> current = (ResponseBean<ResponseHistory>)responseMapBean.body();
                        if(current.getResult() == Constants.RESULT_FAIL){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingLayout.setVisibility(View.GONE);
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                            });

                        }

                        if(current.getResult() == Constants.RESULT_OK){
                            final List<ResponseHistory> historyData = current.getList();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(historyData.size() > 0){
                                        loadingLayout.setVisibility(View.GONE);
                                        emptyTv.setVisibility(View.GONE);
                                        if(adapter == null){
                                            adapter = new HistoryAdapter(HistoryActivity.this,historyData);
                                            historyList.setAdapter(adapter);
                                        }else {
                                            adapter.notifyDataSetChanged();
                                        }
                                    }else{
                                        loadingLayout.setVisibility(View.GONE);
                                        emptyTv.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void fail(Throwable e) {
                        loadingLayout.setVisibility(View.GONE);
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }
}