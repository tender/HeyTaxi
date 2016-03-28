package com.ta.heytaxi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IT-0002993 on 16/3/22.
 */
public class DriverFunctionAdapter extends BaseAdapter{
    private List<FunctionItem> items=new ArrayList<FunctionItem>();
    private Context context;
    public DriverFunctionAdapter(List<FunctionItem> items,Context context){
        this.items=items;
        this.context=context;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FunctionItemHolder holder;
        if(v == null){
            v = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, null);
            holder = new FunctionItemHolder();
            holder.image = (ImageView) v.findViewById(R.id.image);
            holder.text = (TextView) v.findViewById(R.id.text);

            v.setTag(holder);
        } else{
            holder = (FunctionItemHolder) v.getTag();
        }
        FunctionItem item=items.get(position);
        //holder.image.setBackgroundColor(Color.BLUE);
        holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.image.setImageResource(item.getImageResource());

        holder.text.setText(item.getName());

        return v;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<FunctionItem> getItems() {
        return items;
    }

    public void setItems(List<FunctionItem> items) {
        this.items = items;
    }
}
