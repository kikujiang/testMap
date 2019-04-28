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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
        Button btnNavi;
        Button btnDetail;
        Button btnCheck;
        Button btnEdit;

        public MyHolder(View view){
            super(view);

            tvTitle = view.findViewById(R.id.tv_name);
            btnDetail = view.findViewById(R.id.button_detail);
            btnNavi = view.findViewById(R.id.button_navi);
            btnCheck = view.findViewById(R.id.button_check);
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
        myHolder.tvTitle.setText (item.getName());

        myHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"点击了详情按钮",Toast.LENGTH_LONG).show();
            }
        });

        myHolder.btnNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double curLatitude = item.getTag().getLocation_lat();
                double curLongitude = item.getTag().getLocation_long();
                invokingGD(curLatitude,curLongitude);
            }
        });

        switch (type){
            case Constants.TYPE_TASK_PUBLISH:
                if(item.getStatus() == 3 || item.getStatus() == 4){
                    myHolder.btnEdit.setVisibility(View.VISIBLE);
                }else{
                    myHolder.btnEdit.setVisibility(View.GONE);
                }
                myHolder.btnCheck.setVisibility(View.GONE);
                myHolder.tvTitle.setText (item.getName()+"  "+item.getStatusStr());
                break;
            case Constants.TYPE_TASK_TO_BE_FINISH:
                myHolder.btnCheck.setVisibility(View.VISIBLE);
                myHolder.btnEdit.setVisibility(View.GONE);
                myHolder.btnCheck.setText("修复");
                break;
            case Constants.TYPE_TASK_TO_BE_VERIFY:
                if(item.isPassSelf()){
                    myHolder.btnCheck.setVisibility(View.GONE);
                    myHolder.btnEdit.setVisibility(View.GONE);
                }else{
                    myHolder.btnCheck.setVisibility(View.VISIBLE);
                    myHolder.btnEdit.setVisibility(View.GONE);
                    myHolder.btnCheck.setText("审核");
                }

                break;
            case Constants.TYPE_TASK_COMPLETED:
                myHolder.btnCheck.setVisibility(View.GONE);
                myHolder.btnEdit.setVisibility(View.GONE);
                break;
            default:
        }

        myHolder.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stateIntent = new Intent(context,StateActivity.class);
                stateIntent.putExtra("point_id",item.getTag().getId());
                stateIntent.putExtra("checkId",item.getId());
                if(myHolder.btnCheck.getText().toString().equals("修复")){
                    stateIntent.putExtra("itemFix",true);
                }else if(myHolder.btnCheck.getText().toString().equals("审核")){
                    stateIntent.putExtra("itemQuery",true);
                }

                stateIntent.putExtra("task",true);
                context.startActivity(stateIntent);
            }
        });

        myHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pointDetailIntent = new Intent(context,TaskDetailActivity.class);
                pointDetailIntent.putExtra("checkId",item.getId());
                pointDetailIntent.putExtra("pointId",item.getTag().getId());
                context.startActivity(pointDetailIntent);
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