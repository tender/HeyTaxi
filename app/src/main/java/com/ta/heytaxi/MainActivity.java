package com.ta.heytaxi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ta.heytaxi.com.ta.heytaxi.message.Consumer;
import com.ta.heytaxi.com.ta.heytaxi.message.MQTTService;
import com.ta.heytaxi.com.ta.heytaxi.message.MessageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private final static String TAG="MainActivity";
    List<FunctionItem> functionItemsForDriver=new ArrayList<FunctionItem>(10);
    List<FunctionItem> functionItemsForCustomer=new ArrayList<FunctionItem>(10);
    GridView gridView;
    Context context;

//    private ServiceConnection serviceConnection;
//    private MQTTService messageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        functionItemsForDriver = createFunctionItems(R.array.appFunctionsForDriver);
        functionItemsForCustomer = createFunctionItems(R.array.appFunctionsForCustomer);

        context = getApplicationContext();
        DriverFunctionAdapter adapter = new DriverFunctionAdapter(functionItemsForDriver, context);
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

        try {
            startMessageService();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startMessageService()throws Exception{
       Intent msi=new Intent(this,MQTTService.class);
        msi.putExtra("clientId","consumer_user");
        msi.putExtra("topic","HeyTaxi Message Server");
        startService(msi);


        // 實作 ServiceConnection
//        serviceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                messageService = ((MQTTService.MessageBinder)service).getService();
//                Toast.makeText(context, messageService+"", Toast.LENGTH_SHORT).show();
//                Log.i("mylog", "onServiceConnected".toString());
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                messageService = null;
//                Log.i("mylog", "onServiceDisconnected".toString());
//            }
//        };
//        bindService(msi, serviceConnection,
//                Context.BIND_AUTO_CREATE);

        Log.i(TAG,"Start Message Service ");
//        try {
//            Thread.sleep(10000);
//            Log.i(TAG,"DDDDDDDDD");
//            if(messageService!=null){
//                MessageHelper helper=messageService.getMessageHelper();
//                Queue<String> value=messageService.getStore();
//                Consumer consumer=helper.getConsumer();
//                List<String> vos= (List<String>) consumer.getMessages();
//                for(String vo:vos){
//                    Log.i(TAG,"MainActivity --->getMessage:"+vo);
//                }
//            }else{
//                Log.i(TAG,"MESSAGE SERVICE IS NULL");
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
