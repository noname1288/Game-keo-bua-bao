/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.game.controller;

import com.mycompany.myserverpj.data.DBService;
import com.mycompany.myserverpj.game.GameManager;
import com.mycompany.myserverpj.game.playing.PlayingThread;
import com.mycompany.myserverpj.network.ClientThread;
import com.mycompany.myserverpj.data.entity.PlayRoom;
import com.mycompany.myserverpj.network.ConnectionListener;
import com.mycompany.shared.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author quang
 */
public class PlayRoomController {

    private final ArrayList<PlayRoom> rooms;
    private ExecutorService executorService;
    private final DBService dbService;
    private final GameManager gameManager;
    private final PlayingThread playingThread = null;

    // chấp nhận yêu cầu khởi tạo phòng  
    // xử lý chấp nhận lời mời
    public PlayRoomController(GameManager gameManager) {
        rooms = gameManager.getRooms();
        executorService = Executors.newFixedThreadPool(10);
        dbService = new DBService();
        this.gameManager = gameManager;
    }



    //
    public void reqNewRoom(ClientThread player) throws IOException {
        // player yeu cau la dang ranh
        int maPhong = 1;
        Collections.sort(rooms, (PlayRoom p1, PlayRoom p2) -> Integer.compare(p1.getMaPhong(), p2.getMaPhong()));
        for (PlayRoom r : rooms) {
            if (r.getMaPhong() > maPhong) {
                break;
            } else if (r.getMaPhong() == maPhong) {
                maPhong++;
            }
        }
        PlayRoom room = new PlayRoom(maPhong, player);
        rooms.add(room);
        createRoom(maPhong, player);
        //gui thong tin
        gameManager.updateListRoom();
    }
    
    private void createRoom(int maPhong, ClientThread client) {
        try {
            client.getObjOut().writeObject(new Message(MessageAction.NEW_ROOM, maPhong));
        } catch (Exception e) {
        }
    }

    public void reqSendInvite(Message message, String playerInvite) throws IOException {
        //playerInvite la người gửi lời mời
        //xuất hiện ở đây để gửi đến người nhận là ai đã mời họ

        //lấy người nhận từ thông điệp người mời gửi đến
        String playerReceive = (String) message.getContent();

        //tìm phòng người mời đang ở để lấy mã phòng
        int maPhong = 0;
        for (PlayRoom room : rooms) {
            if (room.getPlayer1().getPlayer().getPlayerName().equals(playerInvite)) {
                maPhong = room.getMaPhong();
                break;
            }
        }

        // tìm ClientThread của người nhận
        for (ClientThread thread : gameManager.getListClient()) {
            if (thread.getPlayer().getPlayerName().equals(playerReceive)) {
                // gửi lời mời đến người nhận
                // data gồm số phòng và tên người gửi
                HashMap<String, String> data = new HashMap<>();
                data.put("maPhong", String.valueOf(maPhong));
                data.put("inviter", playerInvite);
                thread.getObjOut().writeObject(new Message(MessageAction.INVITE, data));
                thread.getObjOut().flush();
                break;
            }
        }
    }

    public void respAcceptInvite(Message message, ClientThread client) throws IOException {
        String inviter = (String) message.getContent();

        for (PlayRoom room : rooms) {
            if (room.getPlayer1().getPlayer().getPlayerName().equals(inviter)) {
                if (room.getPlayer2() == null) {
                    room.setPlayer2(client);
                    room.getPlayer1().setStatus(false);
                    client.setStatus(false);
                    // gửi thông tin người chơi 2 về cho người chơi 1 để hiển thị
                    room.getPlayer1().getObjOut().writeObject(new Message(MessageAction.INFO_ANOTHER_PLAYER, client.getPlayer()));
                    room.getPlayer1().getObjOut().flush();
                    // gửi thông tin về cho player 2 để vào phòng
                    createRoom(room.getMaPhong(), client);
                    client.getObjOut().writeObject(
                            new Message(MessageAction.INFO_ANOTHER_PLAYER, 
                                    room.getPlayer1().getPlayer()
                            ));
                    client.getObjOut().flush();
                    gameManager.updateAllPlayers();
                    gameManager.updateListRoom();
                } else {
                    client.getObjOut().writeObject(
                            new Message(MessageAction.ROOM_FULL, null));
                    client.getObjOut().flush();
                }
                break;
            }
        }
    }
    
    public void reqEnterRoom(Message message, ClientThread client) throws IOException {
        int IDRoom = (int) message.getContent();

        for (PlayRoom room : rooms) {
            if (room.getMaPhong() == IDRoom) {
                if (room.getPlayer2() == null) {
                    room.setPlayer2(client);
                    room.getPlayer1().setStatus(false);
                    client.setStatus(false);
                    // gửi thông tin người chơi 2 về cho người chơi 1 để hiển thị
                    room.getPlayer1().getObjOut().writeObject(new Message(MessageAction.INFO_ANOTHER_PLAYER, client.getPlayer()));
                    room.getPlayer1().getObjOut().flush();
                    // gửi thông tin về cho player 2 để vào phòng
                    createRoom(IDRoom, client);
                    client.getObjOut().writeObject(
                            new Message(MessageAction.INFO_ANOTHER_PLAYER, room.getPlayer1().getPlayer()));
                    client.getObjOut().flush();

                    gameManager.updateAllPlayers();
                    gameManager.updateListRoom();
                } else {
                    client.getObjOut().writeObject(
                            new Message(MessageAction.ROOM_FULL, null));
                    client.getObjOut().flush();
                }
                break;
            }
        }
    }

    public void setReadyStatus(ClientThread client) throws IOException {
        for (PlayRoom room : rooms) {
            if (room.getPlayer1().equals(client)) {
                room.setStatusPlayer1(true);
                if (room.isStatusPlayer1() && room.isStatusPlayer2()) {
                    room.getPlayer1().getObjOut().writeObject(new Message(MessageAction.PLAY, null));
                    room.getPlayer1().getObjOut().flush();
                    room.getPlayer2().getObjOut().writeObject(new Message(MessageAction.PLAY, null));
                    room.getPlayer2().getObjOut().flush();
                    executorService.submit(() ->new PlayingThread(room, 10));
                    break;
                }
            } else if (room.getPlayer2().equals(client)) {
                room.setStatusPlayer2(true);
                if (room.isStatusPlayer1() && room.isStatusPlayer2()) {
                    room.getPlayer1().getObjOut().writeObject(new Message(MessageAction.PLAY, null));
                    room.getPlayer1().getObjOut().flush();
                    room.getPlayer2().getObjOut().writeObject(new Message(MessageAction.PLAY, null));
                    room.getPlayer2().getObjOut().flush();
                    executorService.submit(new PlayingThread(room, 10));
                    break;
                }
            }
        }
    }

    //xử lý yêu cầu rời phòng chơi
    public void OutRoom(ClientThread client) throws IOException {
        Iterator<PlayRoom> iterator = rooms.iterator();
        while (iterator.hasNext()) {
            PlayRoom room = iterator.next();
            if (room.getPlayer1().equals(client)) {
                // cả 2 người chơi chuyển thành trạn thái đang rảnh
                client.setStatus(true);
                if (room.getPlayer2() != null) {
                    room.getPlayer2().setStatus(true);
                }
                /* Chuyển người chơi 2 thành người chơi 1 vì 1 số logic chỉ kiểm
                tra người chơi 1 có tồn tại hay không
                 */
                room.setPlayer1(room.getPlayer2());
                room.setPlayer2(null);
                if (room.getPlayer1() == null) {
                    iterator.remove(); // Xóa phòng an toàn khỏi danh sách
                }
            } else if (room.getPlayer2() != null && room.getPlayer2().equals(client)) {
                room.setPlayer2(null);
                client.setStatus(true);
                room.getPlayer1().setStatus(true);
            }
        }
        //gui thong tin
        gameManager.updateListRoom();
    }





//    public void shutdown() {
//        executorService.shutdown();
//    }
}
