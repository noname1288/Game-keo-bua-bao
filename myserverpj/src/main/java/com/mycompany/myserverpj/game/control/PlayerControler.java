/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.game.control;

import com.mycompany.myserverpj.data.ConnectDB;
import com.mycompany.myserverpj.data.DbService;
import com.mycompany.myserverpj.network.ClientHandler;
import com.mycompany.shared.Message;
import com.mycompany.shared.MessageAction;
import com.mycompany.shared.Player;
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
    private final DbService dbService ;

    public PlayerControler(ObjectOutputStream objOut) {
        this.objOut = objOut;
        dbService = new DbService();
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

    // using for module : reg
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

    public boolean register(Message message) throws SQLException, IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        boolean checkedRegister = dbService.register(
                data.get("username"),
                data.get("password"),
                data.get("email"),
                data.get("playerName")
        );
        return checkedRegister;

    }

    // gửi các thông tin để hiển thị trên màn hình chính

    // gửi về 3 người chơi cao điểm nhất
    public HashMap<String, HashMap<String, String>> getTop3HighestPlayer() {
        // gửi về 3 người chơi cao điểm nhất
        return  dbService.getHighest();
    }

    public HashMap<String, String> getRankPlayer(Message message) {
        HashMap<String, String> info = new HashMap<>();
        try {
            return dbService.getPlayerRankAndScore((String) message.getContent());
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("khong tim thay nguoi choi");
        }
        return null;
    }
    



}
