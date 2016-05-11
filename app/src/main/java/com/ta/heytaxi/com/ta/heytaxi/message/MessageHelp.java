package com.ta.heytaxi.com.ta.heytaxi.message;

import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Topic;

/**
 * Created by IT-0002993 on 16/4/28.
 */
public class MessageHelp {

    private static final String URL="tcp://127.0.0.1:1883";
    private static final int CONSUMER_PRODUCER=0;
    private static final int ONLY_CONSUMER=1;
    private static final int ONLY_PRODUCER=2;

    private Producer _producer;
    private Consumer _consumer;
    private int serviceType;


    public MessageHelp(Producer _producer){
        this._producer=_producer;
        setServiceType(ONLY_PRODUCER);
    }

    public MessageHelp(Consumer _consumer){

        this._consumer=_consumer;
        setServiceType(ONLY_CONSUMER);
    }

    public MessageHelp(Producer _producer,Consumer _consumer){
        this._producer=_producer;
        this._consumer=_consumer;
        setServiceType(CONSUMER_PRODUCER);
    }

    private String getServiceClientId(){
        if(ONLY_PRODUCER==getServiceType()){
            return getProducer().provideClientId();
        }else{
            return getConsumer().provideClientId();
        }
    }

    public void receive(String topic) throws Exception{
        MQTT mqtt=createConnection(true);
        getConsumer().receive(mqtt,topic);
    }


    public MQTT createConnection(boolean clean) throws Exception{
        String clientId=getServiceClientId();
        return createConnection(clientId,clean);
    }

    private MQTT createConnection(String clientId,boolean clean) throws Exception{
        MQTT mqtt=new MQTT();
        mqtt.setConnectAttemptsMax(1);
        mqtt.setReconnectAttemptsMax(0);
        if(clientId != null){
            mqtt.setClientId(clientId);

        }
        mqtt.setCleanSession(clean);
        mqtt.setHost(URL);
        return mqtt;

    }

    public Consumer getConsumer() {
        return _consumer;
    }

    public void setConsumer(Consumer _consumer) {
        this._consumer = _consumer;
    }

    public Producer getProducer() {
        return _producer;
    }

    public void setProducer(Producer _producer) {
        this._producer = _producer;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
}
