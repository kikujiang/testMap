package map.test.testmap.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import map.test.testmap.R;
import map.test.testmap.utils.Common;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<ResponseHistory> historyData;

    public HistoryAdapter(Context c,List<ResponseHistory> data){
            this.context = c;
            this.historyData = data;
    }

    @Override
    public int getCount() {
        return historyData.size();
    }

    @Override
    public Object getItem(int position) {
        return historyData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout, parent, false); //加载布局
            holder = new HistoryAdapter.ViewHolder();
            holder.time = convertView.findViewById(R.id.item_time);
            holder.content = convertView.findViewById(R.id.item_content);
            holder.user =  convertView.findViewById(R.id.item_user);
            holder.image = convertView.findViewById(R.id.item_img);

            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (HistoryAdapter.ViewHolder) convertView.getTag();
        }

        ResponseHistory current = historyData.get(position);
        holder.time.setText(current.getOperate_time());

        String content = Common.getInstance().stripHtml(current.getOperate_content());
        holder.content.setText(content);
        holder.user.setText(current.getOperate_userName());
        holder.image.setVisibility(View.GONE);

//        holder.text.setText(current.getOperate_content());

        return convertView;
    }

    private class ViewHolder {
        TextView user;
         TextView content;
         TextView time;
         ImageView image;
    }
}