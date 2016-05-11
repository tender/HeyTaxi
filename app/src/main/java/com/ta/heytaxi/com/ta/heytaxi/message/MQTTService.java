package com.ta.heytaxi.com.ta.heytaxi.message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.util.concurrent.TimeUnit;

public class MQTTService extends Service {
    private static String URL="tcp://127.0.0.1:1883";

    private static final String TOPIC_INPUT = "camel/mqtt/test/input";
    private static final String TOPIC_OUTPUT = "camel/mqtt/test/output";

    private static final String MESSAGE_INPUT = "SquarePants";
    private static final String MESSAGE_OUTPUT = "Hello there " + MESSAGE_INPUT + " :-) ";

    private static final String USER_NAME = "karaf";
    private static final String PASSWORD = "karaf";
    private static final String TAG="MQTTService";

    private Topic outputTopic;
    private MQTT mqtt;
    private BlockingConnection publishConnection ;
    private BlockingConnection subscribeConnection;




    public MQTTService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private MQTT createMQTTTcpConnection(String clientId, boolean clean) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setConnectAttemptsMax(1);
        mqtt.setReconnectAttemptsMax(0);
        if (clientId != null) {
            mqtt.setClientId(clientId);
        }
        mqtt.setCleanSession(clean);
        mqtt.setHost(URL);
        return mqtt;
    }

    private void  createConnection(String clientId,boolean clean){
        outputTopic= new Topic(TOPIC_OUTPUT, QoS.AT_LEAST_ONCE);
        mqtt= new MQTT();
        try {
            mqtt.setConnectAttemptsMax(1);
            mqtt.setReconnectAttemptsMax(0);
            mqtt.setUserName(USER_NAME);
            mqtt.setPassword(PASSWORD);
            if (clientId != null) {
                mqtt.setClientId(clientId);
            }
            mqtt.setCleanSession(clean);
            mqtt.setHost(URL);


            subscribeConnection = mqtt.blockingConnection();
            subscribeConnection.connect();
            subscribeConnection.subscribe(new Topic[]{outputTopic});

        }catch(Exception e){
            Log.e(TAG,e.getMessage());
            if(subscribeConnection != null){
                try {
                    subscribeConnection.disconnect();
                }catch(Exception e1){
                    Log.e(TAG,e1.getMessage());
                }
            }
        }

    }

    private void publicMessage(){
        try{
            publishConnection = mqtt.blockingConnection();
            publishConnection.connect();
            publishConnection.publish(TOPIC_INPUT, MESSAGE_INPUT.getBytes(), QoS.AT_LEAST_ONCE, false);
        }catch(Exception e){
            Log.e(TAG,"Publish Message:"+e.getMessage());
        }
    }

    private void receiveMessage(){

        try {
            while (isReceiveMessage()) {
                Message message = subscribeConnection.receive(10000, TimeUnit.MILLISECONDS);
                Log.i(TAG, "Receive Message" + message);
            }
        }catch(Exception e){
            Log.e(TAG,"Receive Message Eroor:"+e.getMessage());
        }
    }

    private boolean isReceiveMessage(){
        return true;
    }
}
