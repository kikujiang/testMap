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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import map.test.testmap.ImageActivity;
import map.test.testmap.R;
import map.test.testmap.databinding.FragmentPointDetailBinding;
import map.test.testmap.model.Image;
import map.test.testmap.model.Point;
import map.test.testmap.model.ResponseBean;
import map.test.testmap.utils.HttpUtils;
import map.test.testmap.utils.MyViewPagerAdapter;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PointDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointDetailFragment extends Fragment {


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_point_detail, container, false);
        View view = binding.getRoot();
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

            if(bean.getImages() != null && bean.getImages().size() > 0){
                imageViews = new ArrayList<>();
                List<Image> images = bean.getImages();
                configRemoteData(images);
                configPager(binding.pager);
            }else{
                binding.image.setVisibility(View.GONE);
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
