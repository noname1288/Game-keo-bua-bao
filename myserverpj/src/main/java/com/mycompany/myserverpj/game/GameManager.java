package com.mycompany.myserverpj.game;

import com.mycompany.myserverpj.data.entity.PlayRoom;
import com.mycompany.myserverpj.game.controller.PlayRoomController;
import com.mycompany.myserverpj.network.ClientHandler;
import com.mycompany.myserverpj.network.ConnectionListener;
import com.mycompany.shared.Message;
import com.mycompany.shared.MessageAction;
import com.mycompany.shared.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameManager {
    private List<ClientHandler> listClient;
    private PlayRoomController controlRoom;
    private final ArrayList<PlayRoom> rooms;
    //kiem tra luong hoat dong
    public GameManager(ConnectionListener connectionListener){
        this.listClient = new CopyOnWriteArrayList<>();
        this.rooms = new ArrayList<>();
        this.controlRoom = new PlayRoomController(this);

    }

    public ArrayList<PlayRoom> getRooms() {
        return rooms;
    }

    public List<ClientHandler> getListClient() {
        return listClient;
    }

    public void setListClient(List<ClientHandler> listClient) {
        this.listClient = listClient;
    }

    // quan lý phòng chơi
    public PlayRoomController getControlRoom() {
        return controlRoom;
    }



    public void updateAllPlayers() {
        // Gọi hàm cập nhật danh sách người chơi cho tất cả các client
        HashMap<String, HashMap<String, String>> data = new HashMap<>();

        int i = 1;
        for (ClientHandler client : listClient) {
            HashMap<String, String> dataThisClient = new HashMap<>();
            dataThisClient.put("playerName", client.getPlayer().getPlayerName());
            dataThisClient.put("status", (client.isStatus() ? "true" : "false"));
            data.put(String.valueOf(i), dataThisClient);
            i++;
        }

        for (ClientHandler client : listClient) {
            client.updatePlayerList(data);
        }
    }

    public void updateClientList(ClientHandler client) {
        listClient.add(client);
        System.out.println(listClient.size());
    }

    public void removeClient(ClientHandler client) {
        listClient.remove(client);
        // Cập nhật lại danh sách người chơi sau khi client rời đi
        updateAllPlayers();
    }

    //gui danh sach phong cho client
    public void updateListRoom() throws IOException {
        HashMap<String, String> data = new HashMap<>();
        Iterator<PlayRoom> iterator = rooms.iterator();
        while (iterator.hasNext()) {
            PlayRoom r = iterator.next();
            String IDRomm = String.valueOf(r.getMaPhong());
            String numberOfPlayer = (r.getPlayer2() != null) ? "2" : "1";
            data.put(IDRomm, numberOfPlayer);
        }

        try  {
            for (ClientHandler cl : this.getListClient()) {
                cl.getObjOut().writeObject(new Message(MessageAction.LIST_ROOM, data));
                cl.getObjOut().flush();
            }
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }



    }


    // kiểm tra tài khoản đăng nhập chưa
    // duyệt danh sách client kiểm tra trùng ID
    public boolean checkCLientOn(Player player) {
        for (ClientHandler client : listClient) {
            if (client.getPlayer().getID().equals(player.getID())) {
                return true;
            }
        }
        return false;
    }

}
