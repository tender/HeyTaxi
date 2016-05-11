package com.ta.heytaxi.com.ta.heytaxi.message;


import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;

/**
 * Created by IT-0002993 on 16/4/28.
 */
public interface Producer {

    public void send(MQTT mqtt,Message message) throws Exception;

    public String provideClientId();
}
