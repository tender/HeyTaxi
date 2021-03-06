package com.ta.heytaxi;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by IT-0002993 on 16/3/22.
 */
public class FunctionItemClickListener implements AdapterView.OnItemClickListener {
    private static final int START_LOCATION = 0;
    private static final int REGISTER = 2;
    private Activity activity;


    public FunctionItemClickListener(Activity activity){
        this.activity=activity;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String value=((FunctionItem)parent.getAdapter().getItem(position)).getName();
        Toast.makeText(view.getContext(),value+".-->"+position,Toast.LENGTH_SHORT).show();
        switch(position) {
            case 0:
                // 啟動地圖元件用的Intent物件
                Intent intentMap = new Intent(view.getContext().getApplicationContext(), MapsActivity.class);
                // 啟動地圖元件
                activity.startActivityForResult(intentMap, START_LOCATION);
                break;
            case 1:
                Intent intentMapTest = new Intent(view.getContext().getApplicationContext(), MyLocationDemoActivity.class);
                activity.startActivityForResult(intentMapTest, 1);
            case 2:
                // 啟動地圖元件
                Intent intentRegister = new Intent(view.getContext().getApplicationContext(), RegisterActivity.class);
                activity.startActivityForResult(intentRegister,REGISTER);
                // 啟動地圖元件用的Intent物件
            default:
                break;
        }
    }
}
