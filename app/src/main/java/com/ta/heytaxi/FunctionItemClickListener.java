package com.ta.heytaxi;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by IT-0002993 on 16/3/22.
 */
public class FunctionItemClickListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(view.getContext(),((FunctionItem)parent.getSelectedItem()).getName(),Toast.LENGTH_SHORT);
    }
}
