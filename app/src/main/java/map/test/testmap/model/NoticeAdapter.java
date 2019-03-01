package map.test.testmap.model;

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

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyHolder> {

    Context context;
    private List<Notice> list;

    public NoticeAdapter(Context context, List<Notice> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.notice_item,parent,false);
        MyHolder holder = new MyHolder (view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Notice item = list.get (position);
        holder.tvTitle.setText (item.getNotice_title());
        String content = item.getNotice_content();
        content.replace("//<///br//>","\n");
        Log.d("map", "content is:"+content);
        holder.tvContent.setText (content);
        holder.tvTime.setText (item.getNotice_time());
    }

    @Override
    public int getItemCount() {
        return list.size ();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        public MyHolder(View itemView) {
            super (itemView);
            tvTitle = itemView.findViewById (R.id.title);
            tvContent = itemView.findViewById (R.id.content);
            tvTime = itemView.findViewById (R.id.time);
        }
    }
}
