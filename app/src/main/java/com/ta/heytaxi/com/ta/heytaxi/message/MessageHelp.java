package com.ta.heytaxi.com.ta.heytaxi.message;

/**
 * Created by IT-0002993 on 16/4/28.
 */
public class MessageHelp {

    private Producer _producer;
    private Consumer _consumer;

    public MessageHelp(Producer _producer){
        this._producer=_producer;

    }

    public MessageHelp(Consumer _consumer){
        this._consumer=_consumer;
    }

    public MessageHelp(Producer _producer,Consumer _consumer){
        this._producer=_producer;
        this._consumer=_consumer;

    }

    public Consumer get_consumer() {
        return _consumer;
    }

    public void set_consumer(Consumer _consumer) {
        this._consumer = _consumer;
    }

    public Producer get_producer() {
        return _producer;
    }

    public void set_producer(Producer _producer) {
        this._producer = _producer;
    }
}
