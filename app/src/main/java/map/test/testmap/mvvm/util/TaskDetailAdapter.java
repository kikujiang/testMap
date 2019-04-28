package map.test.testmap.mvvm.util;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import map.test.testmap.ImageActivity;
import map.test.testmap.R;
import map.test.testmap.databinding.ItemTaskDetailBinding;
import map.test.testmap.mvvm.data.model.ImageBean;
import map.test.testmap.mvvm.data.model.TaskDetailBean;
import map.test.testmap.utils.MyViewPagerAdapter;

public class TaskDetailAdapter extends RecyclerView.Adapter<TaskDetailAdapter.TaskDetailHolder> {

    private static final String TAG = "TaskDetailAdapter";

    private Context mContext;
    private List<TaskDetailBean> list;

    public TaskDetailAdapter(Context context, List<TaskDetailBean> dataList){
        this.mContext = context;
        this.list = dataList;
    }

    @NonNull
    @Override
    public TaskDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemTaskDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this.mContext), R.layout.item_task_detail, viewGroup, false);
        return new TaskDetailHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull TaskDetailHolder viewHolder, int i) {
        ItemTaskDetailBinding binding = DataBindingUtil.getBinding(viewHolder.itemView);

        TaskDetailBean bean = list.get(i);

        binding.setDetail(bean);

        if(bean.isOvertime()){
            binding.overtime.setText("是");
        }else{
            binding.overtime.setText("否");
        }

        if(bean.getImageList() != null && bean.getImageList().size() > 0){
            List<ImageView> imageViews = new ArrayList<>();
            List<ImageBean> images = bean.getImageList();
            Log.d(TAG, "onBindViewHolder: "+images.size());
            configRemoteData(images,imageViews);
            configPager(binding.pager,imageViews);
        }else{
            binding.image.setVisibility(View.GONE);
        }

        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    final class TaskDetailHolder extends RecyclerView.ViewHolder
    {
        public TaskDetailHolder(View itemView)
        {
            super(itemView);
        }
    }

    private int pagerWidth;

    private void configPager(ViewPager pager,List<ImageView> imageViews){
        pagerWidth = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        ViewGroup.LayoutParams lp = pager.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp.width = pagerWidth;
        }
        pager.setLayoutParams(lp);
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(imageViews);
        pager.setAdapter(adapter);
    }

    private void configRemoteData(List<ImageBean> images,List<ImageView> imageViews){
        for (final ImageBean cur: images) {
            ImageView current = new ImageView(mContext);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,ImageActivity.class);
                    intent.putExtra("http",cur.getPath());
                    mContext.startActivity(intent);
                }
            });
            Glide.with(mContext).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)).load(cur.getPath()).into(current);
            imageViews.add(current);
        }
    }
}
