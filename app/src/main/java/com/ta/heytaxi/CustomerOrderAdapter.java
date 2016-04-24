package com.ta.heytaxi;

import android.content.Context;
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
public class CustomerOrderAdapter extends BaseAdapter{
    private List<CustomerOrder> items=new ArrayList<CustomerOrder>();
    private Context context;
    public CustomerOrderAdapter(List<CustomerOrder> items, Context context){
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
        CustomerOrderHolder holder;
        if(v == null){
            v = LayoutInflater.from(getContext()).inflate(R.layout.order_item, null);
            holder = new CustomerOrderHolder();
            holder.image = (ImageView) v.findViewById(R.id.image);
            holder.text = (TextView) v.findViewById(R.id.text);

            v.setTag(holder);
        } else{
            holder = (CustomerOrderHolder) v.getTag();
        }
        CustomerOrder item=items.get(position);
        //holder.image.setBackgroundColor(Color.BLUE);
        holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.image.setImageResource(item.getImageResource());

        holder.text.setText(item.getOrderNO());

        return v;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<CustomerOrder> getItems() {
        return items;
    }

    public void setItems(List<CustomerOrder> items) {
        this.items = items;
    }
}
