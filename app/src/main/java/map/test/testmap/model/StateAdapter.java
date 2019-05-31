package map.test.testmap.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import map.test.testmap.R;

public class StateAdapter extends BaseAdapter {

    ArrayList<State> stateList;
    private LayoutInflater mInflater;
    private Context mContext;

    public StateAdapter(Context context,ArrayList<State> list){
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.stateList = list;
    }

    @Override
    public int getCount() {
        return stateList.size();
    }

    @Override
    public Object getItem(int position) {
        return stateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
             if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.layout, parent, false); //加载布局
                    holder = new ViewHolder();

                    holder.time = convertView.findViewById(R.id.item_time);
                    holder.content = convertView.findViewById(R.id.item_content);
                    holder.user =  convertView.findViewById(R.id.item_user);
                    holder.image = convertView.findViewById(R.id.item_img);

                   convertView.setTag(holder);
             } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
                   holder = (ViewHolder) convertView.getTag();
             }

             State current = stateList.get(position);
             holder.time.setText(current.getCreateTime());
             holder.content.setText(current.getName());
             holder.user.setText(current.getCreateUserName());

             if(current.getStatus() == 2 || current.getStatus() == 4){
                holder.image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mark_repair_1));
                 holder.image.setVisibility(View.VISIBLE);
             }else if(current.getStatus() == 3 || current.getStatus() == 5){
                 holder.image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mark_repair_2));
                 holder.image.setVisibility(View.VISIBLE);
             }else{
                 holder.image.setVisibility(View.GONE);
             }


             return convertView;
    }

    private class ViewHolder {
         TextView user;
         TextView content;
         TextView time;
         ImageView image;
    }
}
