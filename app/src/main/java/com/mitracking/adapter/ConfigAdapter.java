package com.mitracking.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mitracking.R;
import com.mitracking.objs.ConfigObj;
import java.util.ArrayList;

public class ConfigAdapter extends BaseAdapter {

    private LayoutInflater inflater=null;
    private ArrayList<ConfigObj> array;
    private Fragment fragment;

    public ConfigAdapter(Fragment fragment, ArrayList<ConfigObj> array){
        this.fragment = fragment;
        inflater = (LayoutInflater)fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.cnfg_row, parent, false);

        TextView tag = ViewHolder.get(convertView, R.id.tag);
        tag.setText(array.get(position).tag+" : ");

        TextView value = ViewHolder.get(convertView, R.id.value);
        value.setText(array.get(position).value);

        return convertView;
    }

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    public void updateAdapter(ArrayList<ConfigObj> array){
        this.array = array;
        notifyDataSetChanged();
    }
}
