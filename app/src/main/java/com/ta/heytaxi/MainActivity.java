package com.ta.heytaxi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<FunctionItem> functionItemsForDriver=new ArrayList<FunctionItem>(10);
    List<FunctionItem> functionItemsForCustomer=new ArrayList<FunctionItem>(10);
    GridView gridView;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        functionItemsForDriver=createFunctionItems(R.array.appFunctionsForDriver);
        functionItemsForCustomer=createFunctionItems(R.array.appFunctionsForCustomer);

        context=getApplicationContext();
        DriverFunctionAdapter adapter=new DriverFunctionAdapter(functionItemsForDriver,context);
        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setAdapter(adapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String value=((FunctionItem)parent.getAdapter().getItem(position)).getName();
//                Toast.makeText(context,value+".xx"+position,Toast.LENGTH_SHORT).show();
//
//            }
//        });
        gridView.setOnItemClickListener(new FunctionItemClickListener(this));


    }



    private List<FunctionItem> createFunctionItems(int functionForUser){
        FunctionItem dto=null;
        String value="";
        int imageResourceId;
        Resources resources=getResources();
        List<FunctionItem> result=new ArrayList<FunctionItem>(10);
        String[] items=getResources().getStringArray(functionForUser);
        for(String item:items) {
            dto=new FunctionItem();
            dto.setDisabled(false);
            imageResourceId=resources.getIdentifier(item, "drawable", this.getPackageName());
            dto.setImageResource(imageResourceId);

            value=resources.getString(resources.getIdentifier(item, "string", this.getPackageName()));
            dto.setName(value);
            result.add(dto);
        }
        return result;
    }
}
