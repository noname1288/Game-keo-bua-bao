package com.mycompany.myserverpj.server;

import com.mycompany.myserverpj.model.ClientThread;
import com.mycompany.myserverpj.model.control.ConnectDB;
import com.mycompany.myserverpj.model.control.PlayRoomControl;
import com.mycompany.shared.Player;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author quang
 */
public class Server {

    private ServerSocket server = null;
    private ConnectDB con = null;
    private List<ClientThread> listClient;
    private PlayRoomControl controlRoom;
    final private int port = 8080;

    public void openServer() {
        try {
            server = new ServerSocket(port);
            listClient = new CopyOnWriteArrayList<>();
            controlRoom = new PlayRoomControl(this);
            con = new ConnectDB();
            while (true) {
                listenning();
            }
        } catch (IOException e) {
        }
    }

    public void listenning() throws IOException {
        try {
            ClientThread client = new ClientThread(server.accept(), this);
            System.out.println("1 nguoi da dang nhap");
        } catch (IOException e) {
        }
    }

    public void updateAllPlayers() {
        // Gọi hàm cập nhật danh sách người chơi cho tất cả các client
        HashMap<String, HashMap<String, String>> data = new HashMap<>();

        int i = 1;
        for (ClientThread client : listClient) {
            HashMap<String, String> dataThisClient = new HashMap<>();
            dataThisClient.put("playerName", client.getPlayer().getPlayerName());
            dataThisClient.put("status", (client.isStatus() ? "true" : "false"));
            data.put(String.valueOf(i), dataThisClient);
            i++;
        }
        
        for (ClientThread client : listClient) {
            client.updatePlayerList(data);
        }
    }

    public void updateClientList(ClientThread client) {
        listClient.add(client);
        System.out.println(listClient.size());
    }

    public void removeClient(ClientThread client) {
        listClient.remove(client);
        // Cập nhật lại danh sách người chơi sau khi client rời đi
        updateAllPlayers();
    }

    // quan lý phòng chơi
    public PlayRoomControl getControlRoom() {
        return controlRoom;
    }

    public List<ClientThread> getListClient() {
        return listClient;
    }
    // kiểm tra tài khoản đăng nhập chưa
    // duyệt danh sách client kiểm tra trùng ID
    public boolean checkCLientOn(Player player) {
        for (ClientThread client : listClient) {
            if (client.getPlayer().getID().equals(player.getID())) {
                return true;
            }
        }
        return false;
    }

}
