package com.ta.heytaxi.com.ta.heytaxi.message;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class MQTTService extends Service {

    private static String TAG="MQTTService";
    private final IBinder messageBinder = new MessageBinder();

    MessageHelper messageHelper;
    Queue<String> store=new LinkedList<String>();
    Consumer consumer;
    String clientId;
    String topic;



    public MQTTService() {
    }

    // 實作 Binder
    public class MessageBinder extends Binder {
        public MQTTService getService() {
            return MQTTService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messageBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("mylog", "onRebind()");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("mylog", "onUnbind()");
        //handler.removeCallbacks(showTime);
        return super.onUnbind(intent);
    }



    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clientId=intent.getStringExtra("clientId");
        topic=intent.getStringExtra("topic");
        consumer=new ConsumerPahoImpl(clientId,store);
        messageHelper=new MessageHelper(consumer,store);
        try {
            messageHelper.receive(topic);
        }catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getMessages() {
        Log.i(TAG,"getMessage().size:"+store.size());
        //List<String> result=new ArrayList<String>();
        List<String> vos=(List<String>)consumer.getMessages();
        for(String vo:vos) {
            Log.i(TAG,"getMessage() is :"+vo);
        }
        //return result;
    }


    public MessageHelper getMessageHelper() {
        return messageHelper;
    }

    public Queue<String> getStore() {
        return store;
    }
}
