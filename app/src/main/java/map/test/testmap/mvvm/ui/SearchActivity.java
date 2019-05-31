package map.test.testmap.mvvm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.test.testmap.Constants;
import map.test.testmap.PointDetailActivity;
import map.test.testmap.R;
import map.test.testmap.model.ConstantType;
import map.test.testmap.model.DataType;
import map.test.testmap.model.Line;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.OkHttpClientManager;
import map.test.testmap.utils.SearchAdapter;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private int searchType;

    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private RecyclerView contentList;

    private List<Line> lineList;
    private List<String> dataList;
    private String[] dataAll;
    private List<ConstantType> typeList;

    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchType = getIntent().getIntExtra("type",0);
        if(searchType == 0){
            finish();
            return;
        }

        setContentView(R.layout.activity_search);

        init();
    }

    private void init(){
        loadingLayout = findViewById(R.id.search_loading);
        contentList = findViewById(R.id.search_list);
        initToolBar();
        initSearchView();
        getDataFromServer();
    }

    private void getDataFromServer(){
        switch (searchType){
            case PointDetailActivity
                    .TYPE_BDS:
                getConstantType();
                break;
            case PointDetailActivity
                    .TYPE_LINE:
                getALLLine();
                break;
        }
    }

    private void initToolBar(){
        toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView.setMenuItem(searchItem);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    private void getConstantType(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_CONSTANT_TYPE;
        Log.d(TAG, "请求获取变电所列表接口" + url);
        final Map<String,String> data = new HashMap<>();
        data.put("constantType","1");

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    Log.d(TAG, "success: "+result);
                    Type type = new TypeToken<ResponseBean<ConstantType>>(){}.getType();
                    final ResponseBean<ConstantType> currentData = new Gson().fromJson(result,type);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(currentData.getResult() == Constants.RESULT_FAIL){
                                Toast.makeText(SearchActivity.this,currentData.getDesc(),Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            if (currentData.getResult() == Constants.RESULT_OK){
                                typeList = currentData.getList();

                                if (typeList.size() > 0){
                                    loadingLayout.setVisibility(View.GONE);
                                    if(dataList != null){
                                        dataList.clear();
                                    }else {
                                        dataList = new ArrayList<>();
                                    }
                                    dataAll = new String[typeList.size()];
                                    int i = 0;
                                    for (ConstantType item:
                                            typeList) {
                                        dataList.add(item.getName());
                                        dataAll[i] = item.getName();
                                        i++;
                                    }

                                    SearchAdapter adapter = new SearchAdapter(SearchActivity.this,dataList);
                                    contentList.setLayoutManager (new LinearLayoutManager(SearchActivity.this,LinearLayoutManager.VERTICAL,false));
                                    contentList.setItemAnimator (new DefaultItemAnimator());
                                    contentList.setAdapter (adapter);
                                    contentList.addItemDecoration (new DividerItemDecoration(SearchActivity.this,DividerItemDecoration.VERTICAL));

                                    adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            String cur = dataList.get(position);

                                            for (ConstantType item:
                                                    typeList) {
                                                if(item.getName().equals(cur)){
                                                    Intent backData = new Intent();
                                                    backData.putExtra("type",searchType);
                                                    backData.putExtra("id",item.getValue());
                                                    backData.putExtra("str",item.getName());
                                                    setResult(SearchActivity.RESULT_OK,backData);
                                                    finish();
                                                    return;
                                                }
                                            }
                                        }
                                    });

                                    searchView.setSuggestions(dataAll);

                                }
                            }
                        }
                    });

                }catch (Exception e){
                    dealException(e);
                }
            }

            @Override
            public void fail(Exception e) {
                dealException(e);
            }
        });
    }

    private void getALLLine(){
        final String url = Constants.WEB_URL + Constants.TAG_GET_ALL_LINE;
        Log.d(TAG, "请求获取所有线接口" + url);
        final Map<String,String> data = new HashMap<>();

        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();
                    final Type type = new TypeToken<ResponseBean<Line>>(){}.getType();

                    final ResponseBean<Line> currentData = new Gson().fromJson(result,type);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(currentData.getResult() == Constants.RESULT_FAIL){
                                Toast.makeText(SearchActivity.this,currentData.getDesc(),Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            if (currentData.getResult() == Constants.RESULT_OK){
                                lineList = currentData.getList();

                                if (lineList.size() > 0){
                                    loadingLayout.setVisibility(View.GONE);
                                    if(dataList != null){
                                        dataList.clear();
                                    }else {
                                        dataList = new ArrayList<>();
                                    }
                                    dataAll = new String[lineList.size()];
                                    int i = 0;
                                    for (Line item:
                                         lineList) {
                                        dataList.add(item.getName());
                                        dataAll[i] = item.getName();
                                        i++;
                                    }

                                    SearchAdapter adapter = new SearchAdapter(SearchActivity.this,dataList);
                                    contentList.setLayoutManager (new LinearLayoutManager(SearchActivity.this,LinearLayoutManager.VERTICAL,false));
                                    contentList.setItemAnimator (new DefaultItemAnimator());
                                    contentList.setAdapter (adapter);
                                    contentList.addItemDecoration (new DividerItemDecoration(SearchActivity.this,DividerItemDecoration.VERTICAL));

                                    adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            String cur = dataList.get(position);

                                            for (Line item:
                                                 lineList) {
                                                if(item.getName().equals(cur)){
                                                    Intent backData = new Intent();
                                                    backData.putExtra("type",searchType);
                                                    backData.putExtra("id",item.getId());
                                                    backData.putExtra("str",item.getName());
                                                    setResult(SearchActivity.RESULT_OK,backData);
                                                    finish();
                                                    return;
                                                }
                                            }
                                        }
                                    });

                                    searchView.setSuggestions(dataAll);

                                }
                            }
                        }
                    });


                }catch (Exception e){

                    dealException(e);
                }
                Log.d(TAG,"success: "+responseMapBean.body().toString());
            }

            @Override
            public void fail(Exception e) {
                dealException(e);
            }
        });
    }

    private void dealException(Exception e){
        Log.d(TAG, "dealException: "+e.getMessage());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SearchActivity.this,"服务器未响应，请稍后重试！",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private String currentSearch = "";

    private void initSearchView(){
        searchView = findViewById(R.id.search);
        searchView.setHint("请输入查询内容");

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: "+query);
                currentSearch = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                Log.d(TAG, "onSearchViewShown: ");
                currentSearch = "";
            }

            @Override
            public void onSearchViewClosed() {
                Log.d(TAG, "onSearchViewClosed: "+currentSearch);
                if(!"".equals(currentSearch)){

                    switch (searchType){
                        case PointDetailActivity.TYPE_BDS:
                            for (ConstantType item:
                                    typeList) {
                                if(item.getName().equals(currentSearch)){
                                    Log.d(TAG, "onSearchViewClosed: 1"+item.getName());
                                    Intent backData = new Intent();
                                    backData.putExtra("type",searchType);
                                    backData.putExtra("id",item.getValue());
                                    backData.putExtra("str",item.getName());
                                    setResult(SearchActivity.RESULT_OK,backData);
                                    finish();
                                    return;
                                }
                            }
                            break;
                        case PointDetailActivity.TYPE_LINE:
                            for (Line item:
                                    lineList) {
                                if(item.getName().equals(currentSearch)){
                                    Log.d(TAG, "onSearchViewClosed: 1"+item.getId());
                                    Intent backData = new Intent();
                                    backData.putExtra("type",searchType);
                                    backData.putExtra("id",item.getId());
                                    backData.putExtra("str",item.getName());
                                    setResult(SearchActivity.RESULT_OK,backData);
                                    finish();
                                    return;
                                }
                            }
                            break;
                    }


                }
            }
        });
    }
}
