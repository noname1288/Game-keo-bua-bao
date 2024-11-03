package com.mycompany.myserverpj.network;

import com.mycompany.myserverpj.game.control.PlayRoomController;
import com.mycompany.shared.Message;
import com.mycompany.shared.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * @author quang
 */
public class ConnectionListener {

    private final int port= 8080;
    private ServerSocket server = null;
    private List<ClientHandler> clientHandlerList;
    private PlayRoomController controlRoom;

    public ConnectionListener(){}

        // quan lý phòng chơi
    public PlayRoomController getControlRoom() {
        return controlRoom;
    }

    public List<ClientHandler> getClientHandlerList() {
        return clientHandlerList;
    }

    // khoi dong server
    public void openServer() {
        try {
            server = new ServerSocket(port);
            clientHandlerList = new CopyOnWriteArrayList<>();
            controlRoom = new PlayRoomController(this);

            //start listening
            startListening();

        } catch (IOException e) {
        }
    }

    //server lang nghe
    public void startListening() {
        while (true) {
            try {
                ClientHandler client = new ClientHandler(server.accept(), this);
                System.out.println("1 nguoi muon dang nhap vao game");
            } catch (IOException e) {
                //xu ly exception - todo
                e.printStackTrace();
            }
        }
    }


    //transfer Message to Object
    public Object transferToObject(Message message) {
        Object x = new Object();
        //todo
        return x;
    }

    //co the se dua vao clientHandler
    //todo

    // Gọi hàm cập nhật danh sách người chơi trên main panel cho tất cả các client
    public void updateAllPlayers() {
        HashMap<String, HashMap<String, String>> data = new HashMap<>();

        int i = 1;
        for (ClientHandler client : clientHandlerList) {
            HashMap<String, String> dataThisClient = new HashMap<>();
            dataThisClient.put("playerName", client.getPlayer().getPlayerName());
            dataThisClient.put("status", (client.isStatus() ? "true" : "false"));
            data.put(String.valueOf(i), dataThisClient);
            i++;
        }

        for (ClientHandler client : clientHandlerList) {
            client.updatePlayerList(data);
        }
    }

    public void updateClientList(ClientHandler client) {
        clientHandlerList.add(client);
        System.out.println(clientHandlerList.size());
    }

    public void removeClient(ClientHandler client) {
        clientHandlerList.remove(client);
        // Cập nhật lại danh sách người chơi sau khi client rời đi
        updateAllPlayers();
    }



    // kiểm tra tài khoản đăng nhập chưa
    // duyệt danh sách client kiểm tra trùng ID
    public boolean checkCLientOnl(Player player) {
        for (ClientHandler client : clientHandlerList) {
            if (client.getPlayer().getID().equals(player.getID())) {
                return true;
            }
        }
        return false;
    }

}
