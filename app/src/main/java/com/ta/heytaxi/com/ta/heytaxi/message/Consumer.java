package com.ta.heytaxi.com.ta.heytaxi.message;


import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;

import java.util.List;

/**
 * Created by IT-0002993 on 16/4/28.
 */
public interface Consumer {

    public void receive(MQTT mqtt,String topic) throws Exception;

    public List<Message> getMessages();

    public String provideClientId();
}
