package com.mycompany.myserverpj.formatter;

import com.mycompany.shared.Message;
import com.mycompany.shared.MessageAction;

import java.io.ObjectOutputStream;

public class MessageDispatcher {
    //att
    private final ObjectOutputStream oos ;

    //constructor
    MessageDispatcher(ObjectOutputStream oos){
        this.oos = oos ;
    }

    //public method

    //transfer Object to Message
    public Message dispatch(MessageAction ma, Object x){
        return new Message(ma, x);
    }

}
