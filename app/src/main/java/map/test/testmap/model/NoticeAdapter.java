package map.test.testmap.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import map.test.testmap.R;
import map.test.testmap.utils.Common;

public class NoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Notice> list;

    public NoticeAdapter(Context context, List<Notice> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext ()).inflate(R.layout.notice_item, parent, false);
        MyHolder myViewHolder = new MyHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyHolder cur = (MyHolder) holder;
        Notice item = list.get (position);
        cur.tvTitle.setText (item.getNotice_title());
        String contentOri = item.getNotice_content();
        Log.d("content", contentOri);
        String content = Common.getInstance().stripHtml(contentOri);
        Log.d("content", content);
        cur.tvContent.setText (content);
        cur.tvTime.setText (item.getNotice_time());

        if(item.getReadStatus() == 0 || item.getReadStatus() == 1){
            cur.status.setBackground(null);
        }else {
            cur.status.setBackground(context.getResources().getDrawable(R.drawable.point_blue));
        }

    }

    @Override
    public int getItemCount() {
        return list.size ();
    }


    class MyHolder extends ViewHolder {

        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        View status;
        public MyHolder(View itemView) {
            super (itemView);
            tvTitle = itemView.findViewById (R.id.title);
            tvContent = itemView.findViewById (R.id.content);
            tvTime = itemView.findViewById (R.id.time);
            status = itemView.findViewById (R.id.status);
        }
    }
}
