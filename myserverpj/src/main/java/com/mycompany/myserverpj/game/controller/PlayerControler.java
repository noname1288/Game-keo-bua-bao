/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.game.controller;

import com.mycompany.myserverpj.data.ConnectDB;
import com.mycompany.myserverpj.data.DBService;
import com.mycompany.shared.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author quang
 */
// xu ly tac vu
// login - y tuong: tim doi tuong trong DB de xu ly hien thi sau, neu ko thay
// thi return Login FALSE
public class PlayerControler {

    private final ObjectOutputStream objOut;
    private final DBService dbService;

    public PlayerControler(ObjectOutputStream objOut) {
        this.objOut = objOut;
        dbService = new DBService();
    }

    // các hàm xử lý sự kiện client
    //xử lý login
    public Player login(Message message) throws IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        Player player = dbService.getPlayer(data.get("username"),
                data.get("password"));
        if (player != null) {
            return player;
        }
        return null;
    }

    public boolean checkDuplicate(Message message) throws IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        Set<String> key = data.keySet();
        String field = null;
        for (String element : key) {
            field = element;
            break;
        }
        if ("USERNAME".equals(field)) {
            if (dbService.checkDuplicates("username", data.get(field))) {
                objOut.writeObject(new Message(MessageAction.HV_DUP, null));
            } else {
                objOut.writeObject(new Message(MessageAction.NO_DUP, null));
            }
        } else if ("EMAIL".equals(field)) {
            if (dbService.checkDuplicates("email", data.get(field))) {
                objOut.writeObject(new Message(MessageAction.HV_DUP, null));
            } else {
                objOut.writeObject(new Message(MessageAction.NO_DUP, null));
            }
        } else if ("PLAYER_NAME".equals(field)) {
            if (dbService.checkDuplicates("playerName", data.get(field))) {
                objOut.writeObject(new Message(MessageAction.HV_DUP, null));
            } else {
                objOut.writeObject(new Message(MessageAction.NO_DUP, null));
            }
        }
        return true;
    }

    public void register(Message message) throws SQLException, IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        boolean c = dbService.register(
                data.get("username"),
                data.get("password"),
                data.get("email"),
                data.get("playerName")
        );
        if (c) {
            objOut.writeObject(new Message(MessageAction.REGISTER_SUCCESS, null));
        } else {
            objOut.writeObject(new Message(MessageAction.REGISTER_FAILED, null));
        }
    }

    // gửi các thông tin để hiển thị trên màn hình chính
    public boolean getThreeHighest() {
        // gửi về 3 người chơi cao điểm nhất
        try {
            objOut.writeObject(new Message(MessageAction.THREE_HIGHEST, dbService.getHighest()));
        } catch (IOException e) {
        }

        return true;
    }

    public void getRankPlayer(Message message) {
        try {
            objOut.writeObject(new Message(
                    MessageAction.GET_RANK,
                    dbService.getPlayerRankAndScore((String) message.getContent()))
            );
        } catch (IOException e) {
        }
    }
    
    public void getRankList() {
        try {
            objOut.writeObject(new Message(
                    MessageAction.RANK_LIST,
                    dbService.getRank()
            ));
        } catch (IOException e) {
        }
    }



}
