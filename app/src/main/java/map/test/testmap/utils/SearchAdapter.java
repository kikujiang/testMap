package map.test.testmap.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import map.test.testmap.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyHolder> {

    private List<String> dataList;
    private Context context;
    public SearchAdapter(Context context,List<String> data){
            Log.d("task", "SearchAdapter: list size is:"+data.size());
            this.dataList = data;
            this.context = context;
            }

    final class MyHolder extends RecyclerView.ViewHolder {

        TextView tvContent;

        public MyHolder(View view){
            super(view);

            tvContent = view.findViewById(R.id.content);
        }
}

    public void setOnItemClickListener(SearchAdapter.OnItemClickListener onItemClickListener){
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
        View view = LayoutInflater.from (viewGroup.getContext ()).inflate (R.layout.fragment_item,viewGroup,false);
        MyHolder holder = new MyHolder (view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
        String item = dataList.get (i);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,i);
            }
        });
        myHolder.tvContent.setText (item);
    }

    @Override
    public int getItemCount() {
        return this.dataList.size();
    }
}