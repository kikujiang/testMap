package map.test.testmap.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;

import map.test.testmap.Constants;
import map.test.testmap.R;
import map.test.testmap.StateActivity;
import map.test.testmap.TaskEditActivity;
import map.test.testmap.model.TaskBean;
import map.test.testmap.mvvm.ui.TaskDetailActivity;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyHolder> {

    private ArrayList<TaskBean> dataList;
    private Context context;
    private int type;
    public TaskAdapter(Context context,ArrayList<TaskBean> data,int type){
        Log.d("task", "TaskAdapter: list size is:"+data.size());
        this.dataList = data;
        this.context = context;
        this.type = type;
    }

    final class MyHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvState;
        TextView tvTime;
        ImageView imgState;
        LinearLayout btnNavi;
        LinearLayout btnCheck;
        TextView tvCheck;
        ImageView imgCheck;
        LinearLayout btnEdit;

        public MyHolder(View view){
            super(view);

            tvTitle = view.findViewById(R.id.tv_name);
            tvTime = view.findViewById(R.id.tv_time);
            tvState = view.findViewById(R.id.tv_state);
            imgState = view.findViewById(R.id.tv_state_img);
            btnNavi = view.findViewById(R.id.button_navi);
            btnCheck = view.findViewById(R.id.button_check);
            imgCheck = view.findViewById(R.id.img_check);
            tvCheck = view.findViewById(R.id.tv_check);
            btnEdit = view.findViewById(R.id.button_edit);
        }
    }

    public void setOnItemClickListener(TaskAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 自定义监听回调，RecyclerView 的 单击和长按事件
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from (viewGroup.getContext ()).inflate (R.layout.fragment_task_item,viewGroup,false);
        MyHolder holder = new MyHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        final TaskBean item = dataList.get (i);

//        if(item.getName().equals("刚刚")){
//            Log.d("task", "刚刚 data is: "+item.getStatus());
//        }

        myHolder.tvTitle.setText (item.getName());

        String taskTime = Common.getInstance().getDateFromLong(item.getTaskTime());
        myHolder.tvTime.setText(taskTime);

        myHolder.btnNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double curLatitude = item.getTag().getLocation_lat();
                double curLongitude = item.getTag().getLocation_long();
                invokingGD(curLatitude,curLongitude);
            }
        });

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pointDetailIntent = new Intent(context,TaskDetailActivity.class);
                pointDetailIntent.putExtra("checkId",item.getId());
                pointDetailIntent.putExtra("pointId",item.getTag().getId());
                context.startActivity(pointDetailIntent);

            }
        });

        switch (type){
            case Constants.TYPE_TASK_PUBLISH:
                Log.d("http", "status: "+item.getCheckStatus());
                if(item.getCheckStatus() == 3 || item.getCheckStatus() == 0){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_2));
                    myHolder.imgState.setVisibility(View.VISIBLE);
                    myHolder.btnEdit.setVisibility(View.VISIBLE);
                }else if(item.getCheckStatus() == 4){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_1));
                    myHolder.imgState.setVisibility(View.VISIBLE);
                    myHolder.btnEdit.setVisibility(View.VISIBLE);
                }else{
                    myHolder.btnEdit.setVisibility(View.GONE);
                    myHolder.imgState.setVisibility(View.GONE);
                }
                myHolder.btnCheck.setVisibility(View.GONE);
                myHolder.tvState.setText(item.getStatusStr());

                break;
            case Constants.TYPE_TASK_TO_BE_FINISH:
                if(item.getCheckStatus() == 3 || item.getCheckStatus() == 0){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_2));
                }else if(item.getCheckStatus() == 4){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_1));
                }
                myHolder.btnCheck.setVisibility(View.VISIBLE);
                myHolder.btnEdit.setVisibility(View.GONE);
                myHolder.tvCheck.setText("修复");
                myHolder.tvState.setText(item.getStatusStr());
                myHolder.imgCheck.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon20));
                break;
            case Constants.TYPE_TASK_TO_BE_VERIFY:
                if(item.getCheckStatus() == 3 || item.getCheckStatus() == 0){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_2));
                }else if(item.getCheckStatus() == 4){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_1));
                }

                if(item.isPassSelf()){
                    myHolder.btnCheck.setVisibility(View.GONE);
                    myHolder.btnEdit.setVisibility(View.GONE);
                }else{
                    myHolder.btnCheck.setVisibility(View.VISIBLE);
                    myHolder.btnEdit.setVisibility(View.GONE);
                    myHolder.tvCheck.setText("审核");
                    myHolder.imgCheck.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon21));
                }
                myHolder.tvState.setText(item.getStatusStr());
                break;
            case Constants.TYPE_TASK_COMPLETED:

                if(item.getCheckStatus() == 3 || item.getCheckStatus() == 0){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_2));
                }else if(item.getCheckStatus() == 4){
                    myHolder.imgState.setImageDrawable(context.getResources().getDrawable(R.mipmap.mark_repair_1));
                }

                myHolder.btnCheck.setVisibility(View.GONE);
                myHolder.btnEdit.setVisibility(View.GONE);
                myHolder.tvState.setText(item.getStatusStr());
                break;
            default:
        }

        myHolder.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stateIntent = new Intent(context,StateActivity.class);
                stateIntent.putExtra("point_id",item.getTag().getId());
                stateIntent.putExtra("checkId",item.getId());
                if(myHolder.tvCheck.getText().toString().equals("修复")){
                    stateIntent.putExtra("itemFix",true);
                }else if(myHolder.tvCheck.getText().toString().equals("审核")){
                    stateIntent.putExtra("itemQuery",true);
                }

                stateIntent.putExtra("task",true);
                context.startActivity(stateIntent);
            }
        });

        myHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stateIntent = new Intent(context,TaskEditActivity.class);
                stateIntent.putExtra("checkId",item.getId());
                stateIntent.putExtra("item_id",item.getPublishItemId());
                context.startActivity(stateIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.dataList.size();
    }

    public void invokingGD(double lat, double lon){

        String params = "androidamap://navi?sourceApplication=amap&lat="+lat+"&lon="+lon+ "&dev=0";
        //  com.autonavi.minimap这是高德地图的包名
        Intent intent = new Intent("android.intent.action.VIEW",android.net.Uri.parse(params));
        intent.setPackage("com.autonavi.minimap");

        if(isInstallByread("com.autonavi.minimap")){
            context.startActivity(intent);
        }else{
            Toast.makeText(context, "没有安装高德地图客户端", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

}