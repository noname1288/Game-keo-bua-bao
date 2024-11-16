/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.model;

import com.mycompany.shared.Message;
import com.mycompany.shared.Player;
import com.mycompany.myserverpj.model.control.PlayerControler;
import com.mycompany.myserverpj.server.Server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author quang
 */
public class ClientThread extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private PlayerControler control;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private Player player;
    private boolean status;

    // tạo biến lưu lựa chọn ở đây cho đơn giản
    private int choice;

    public ClientThread(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.status = true;
        // khởi tạo trước để làm FLAG 
        choice = -1;
        start();
    }

    public ObjectOutputStream getObjOut() {
        return objOut;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public PlayerControler getControl() {
        return control;
    }

    @Override
    public void run() {
        try {
            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(clientSocket.getInputStream());
            control = new PlayerControler(objOut);
            while (true) {
                // Chờ thông điệp từ client
                Message message = (Message) objIn.readObject();
                System.out.println(message.getType() + " " + (message.getContent() != null ? message.getContent().toString() : "Content is null"));
                // Xử lý yêu cầu đăng nhập
                switch (message.getType()) {
                    case "LOGIN":
                        player = control.login(message);
                        if (player != null) {
                            if (server.checkCLientOn(player)) {
                                objOut.writeObject(new Message("LOGIN_FAILED", null));
                            } else {
                                status = true;
                                objOut.writeObject(new Message("LOGIN_SUCCESS", player));
                                server.updateClientList(this);
                            }
                        } else {
                            objOut.writeObject(new Message("LOGIN_FAILED", null));
                        }
                        objOut.flush();
                        break;
                    case "CHECK_DUP":
                        control.checkDuplicate(message);
                        break;
                    case "REGISTER":
                        control.register(message);
                        break;
                    case "THREE_HIGHEST":
                        control.getThreeHighest();
                        break;
                    case "GET_RANK":
                        control.getRankPlayer(message);
                        break;
                    case "DISCONNEC":
                        closeConnection();
                        break;
                    case "NEW_ROOM":
                        server.getControlRoom().reqNewRoom(this);
                        break;
                    case "SEND_INVITE":
                        server.getControlRoom().reqSendInvite(message, player.getPlayerName());
                        break;
                    case "ACCEPT":
                        server.getControlRoom().respAcceptInvite(message, this);
                        break;
                    case "LIST_PLAYER":
                        server.updateAllPlayers();
                        break;
                    case "LIST_ROOM":
                        server.getControlRoom().updateListRoom();
                        break;
                    case "CHOICE":
                        choice = (int) message.getContent();
                        break;
                    case "READY":
                        server.getControlRoom().setReadyStatus(this);
                        break;
                    case "PLAY":
                        break;
                    case "OUT_ROOM":
                        server.getControlRoom().OutRoom(this);
                        server.updateAllPlayers();
                        break;
                    case "ENTER_ROOM":
                        server.getControlRoom().reqEnterRoom(message, this);
                        break;
                    case "RANK_LIST":
                        control.getRankList();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        } catch (SQLException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // cập nhật danh sách người chơi
    public void updatePlayerList(HashMap<String, HashMap<String, String>> data) {
        try {
            objOut.writeObject(new Message("LIST_PLAYER", data));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    // đóng kết nối
    private void closeConnection() {
        try {
            try (clientSocket) {
                server.removeClient(this);
            }
            objOut.close();
            objIn.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
