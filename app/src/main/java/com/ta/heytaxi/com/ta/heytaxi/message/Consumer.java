package com.ta.heytaxi.com.ta.heytaxi.message;

import javax.jms.JMSException;

/**
 * Created by IT-0002993 on 16/4/28.
 */
public interface Consumer {

    public void receive() throws JMSException;
}
