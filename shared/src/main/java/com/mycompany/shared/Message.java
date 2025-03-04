/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shared;

import java.io.Serializable;

/**
 *
 * @author quang
 */
public class Message implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private MessageAction type;
    private Object content;

    public Message(MessageAction type, Object content) {
        this.type = type;
        this.content = content;
    }

    public MessageAction getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }
}
