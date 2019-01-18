package map.test.testmap.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import map.test.testmap.R;

public class LineStateAdapter extends BaseAdapter {

    ArrayList<LineState> stateList;
    private LayoutInflater mInflater;

    public LineStateAdapter(Context context, ArrayList<LineState> list){
        mInflater = LayoutInflater.from(context);
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

                    holder.text = (TextView) convertView.findViewById(R.id.text);

                   convertView.setTag(holder);
             } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
                   holder = (ViewHolder) convertView.getTag();
             }

             LineState current = stateList.get(position);
             holder.text.setText(current.getCreateTime()+" "+current.getCreateUserName()+" "+current.getPointName());

             return convertView;
    }

    private class ViewHolder {
         TextView text;
    }
}
