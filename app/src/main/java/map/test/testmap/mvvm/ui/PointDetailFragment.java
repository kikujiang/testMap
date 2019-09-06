package map.test.testmap.mvvm.ui;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.qrcode.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.test.testmap.Constants;
import map.test.testmap.ImageActivity;
import map.test.testmap.R;
import map.test.testmap.databinding.FragmentPointDetailBinding;
import map.test.testmap.model.Image;
import map.test.testmap.model.Line;
import map.test.testmap.model.OnInfoListener;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.Common;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import map.test.testmap.utils.OkHttpClientManager;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PointDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointDetailFragment extends Fragment {


    private EditText etLineName;
    private EditText etLineType;
    private EditText etLineBelong;
    private EditText etLineRemark;
    private EditText etLineName2;
    private EditText etLineLength;
    private TextView tvStart;
    private TextView tvEnd;
    private TextView tvDetailTitle;
    private LinearLayout bottomLayout;

    public PointDetailFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "task";

    private FragmentPointDetailBinding binding;
    private int pointId;

    public static PointDetailFragment newInstance(int checkId) {
        PointDetailFragment fragment = new PointDetailFragment();
        Bundle data = new Bundle();
        data.putInt("pointId",checkId);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pointId = getArguments().getInt("pointId");
            Log.d("task", "PointDetailFragment onCreateView point id: " + pointId);
        }
    }

    private void getSingleLine(String lineId){
        final String url = Constants.WEB_URL + Constants.TAG_GET_SINGLE_LINE;
        Log.d(TAG, "请求获取某条线接口" + url);
        final Map<String,String> data = new HashMap<>();
        data.put("id",lineId);
        Log.d(TAG, "getSingleLine data is:" + data.toString());
        OkHttpClientManager.getInstance().post(url, data, new OnInfoListener() {
            @Override
            public void success(okhttp3.Response responseMapBean) {
                try{
                    String result = responseMapBean.body().string();

                    Type type = new TypeToken<ResponseBean<Line>>(){}.getType();
                    final ResponseBean<Line> currentData = new Gson().fromJson(result,type);

                    if(currentData.getResult() == Constants.RESULT_FAIL){
                        Log.d(TAG, "fail:" + currentData.getDesc());
                    }

                    if(currentData.getResult() == Constants.RESULT_OK){

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Line currentLine = currentData.getObject();
                                etLineName.setText(currentLine.getName());
                                etLineType.setText(currentLine.getPosTypeStr());
                                etLineRemark.setText(currentLine.getRemark());

                                if(currentLine.getStationName() != null){
                                    etLineBelong.setText(currentLine.getStationName());
                                }

                                if(currentLine.getStationName() != null){
                                    etLineBelong.setText(currentLine.getStationName());
                                }

                                etLineLength.setText(currentLine.getLineLength()+"");

                                if(currentLine.getL_name() != null){
                                    etLineName2.setText(currentLine.getL_name());
                                }

                                tvStart.setText(currentLine.getTag_begin_name()+"");
                                tvEnd.setText(currentLine.getTag_end_name()+"");
                            }
                        });




                        Log.d(TAG, "收到消息为："+ result);
                    }
                }catch (Exception e){
                    Log.d(TAG, "fail:" + e.getMessage());
                }
            }

            @Override
            public void fail(Exception e) {
                Log.d(TAG, "fail:" + e.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_point_detail, container, false);

        View view = binding.getRoot();
        bottomLayout = view.findViewById(R.id.line_bottom);
        etLineName = view.findViewById(R.id.et_line_name);
        etLineType = view.findViewById(R.id.et_line_type);
        etLineBelong = view.findViewById(R.id.et_line_belong);
        etLineName2 = view.findViewById(R.id.et_line_name2);
        etLineLength = view.findViewById(R.id.et_line_length);
        etLineRemark = view.findViewById(R.id.et_line_remark);
        tvStart = view.findViewById(R.id.start);
        tvEnd = view.findViewById(R.id.end);
        tvDetailTitle = view.findViewById(R.id.detail_title_prefix);
        Common.getInstance().setEditTextFalse(etLineName);
        Common.getInstance().setEditTextFalse(etLineType);
        Common.getInstance().setEditTextFalse(etLineBelong);
        Common.getInstance().setEditTextFalse(etLineName2);
        Common.getInstance().setEditTextFalse(etLineLength);
        Common.getInstance().setEditTextFalse(etLineRemark);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new DetailTask().execute(pointId);
    }

    class DetailTask extends AsyncTask<Integer,Void,Point> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Point doInBackground(Integer[] ids) {
            try{
                int id = ids[0];
                Log.d(TAG, "doInBackground: called and point id is:"+id);
                Response<ResponseBean<Point>> response = HttpUtils.getInstance().getPointDetail(id);
                return response.body().getObject();
            }catch (IOException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Point bean) {
            super.onPostExecute(bean);
            binding.setPoint(bean);

            if(bean.getMtypeStr() != null){
                binding.deviceType.setText(bean.getMtypeStr());
            }else{
                binding.deviceType.setText("暂无");
            }

            binding.latitude.setText(bean.getLocation_lat()+"");
            binding.longitude.setText(bean.getLocation_long()+"");

            if(bean.getType() == 101 || bean.getType() == 102){
                binding.detailTitle.setText("线路详情");
                tvDetailTitle.setText("故  障  点");
                binding.detailLine.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.GONE);
                binding.layoutPointMiddle.setVisibility(View.GONE);
                binding.layoutPointBottom.setVisibility(View.GONE);

                getSingleLine(bean.getLineId()+"");
            }else{
                binding.detailTitle.setText("标记点详情");
                tvDetailTitle.setText("名        称");
                binding.detailLine.setVisibility(View.GONE);
                if(bean.getImages() != null && bean.getImages().size() > 0){
                    imageViews = new ArrayList<>();
                    List<Image> images = bean.getImages();
                    configRemoteData(images);
                    configPager(binding.pager);
                }else{
                    binding.image.setVisibility(View.GONE);
                }
            }

        }

        private MyViewPagerAdapter adapter = null;
        private List<ImageView> imageViews = null;
        private int pagerWidth;

        private void configPager(ViewPager pager){

            if(adapter == null){
                pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels);
                ViewGroup.LayoutParams lp = pager.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    lp.width = pagerWidth;
                }
                pager.setLayoutParams(lp);
                adapter = new MyViewPagerAdapter(imageViews);
                pager.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }

        private void configRemoteData(List<Image> images){
            for (final Image cur: images) {
                ImageView current = new ImageView(getActivity());
                current.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),ImageActivity.class);
                        intent.putExtra("http",cur.getPath());
                        startActivity(intent);
                    }
                });
                Glide.with(getActivity()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)).load(cur.getPath()).into(current);
                imageViews.add(current);
            }
            Log.d(TAG, "configRemoteData: "+ imageViews.size());
        }
    }
}
