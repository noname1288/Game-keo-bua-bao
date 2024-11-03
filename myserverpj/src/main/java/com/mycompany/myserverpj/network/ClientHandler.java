/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.network;

import com.mycompany.myserverpj.formatter.MessageDispatcher;
import com.mycompany.myserverpj.game.control.PlayerControler;
import com.mycompany.shared.Message;
import com.mycompany.shared.MessageAction;
import com.mycompany.shared.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mycompany.shared.MessageAction.LOGIN_FAILED;
import static com.mycompany.shared.MessageAction.LOGIN_SUCCESS;

/**
 * @author quang
 */
public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ConnectionListener connectionListener;
    private final MessageDispatcher messageDispatcher = null;
    private PlayerControler playerControler;
    private Player player;
    private boolean status;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private int choice;  // tạo biến lưu lựa chọn ở đây cho đơn giản

    public ClientHandler(Socket clientSocket, ConnectionListener connectionListener) {
        this.clientSocket = clientSocket;
        this.connectionListener = connectionListener;
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

    public PlayerControler getPlayerControler() {
        return playerControler;
    }

    @Override
    public void run() {
        try {
            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(clientSocket.getInputStream());
            playerControler = new PlayerControler(objOut);
            while (true) {
                // Chờ thông điệp từ client
                Message message = (Message) objIn.readObject();
                System.out.println(message.getType() + " " + (message.getContent() != null ? message.getContent().toString() : "Content is null"));
                // Xử lý yêu cầu đăng nhập
                switch (message.getType()) {
                    case LOGIN:
                        player = playerControler.login(message);
                        if (player != null) {
                            if (connectionListener.checkCLientOnl(player)) {
                                objOut.writeObject(new Message(LOGIN_FAILED, null));
                            } else {
                                status = true;
                                objOut.writeObject(new Message(LOGIN_SUCCESS, player));
                                connectionListener.updateClientList(this);
                            }
                        } else {
                            objOut.writeObject(new Message(LOGIN_FAILED, null));
                        }
                        objOut.flush();
                        break;
                    case CHECK_DUP:
                        playerControler.checkDuplicate(message);
                        break;
                    case REGISTER:
                        boolean checked = playerControler.register(message);
                        if (checked) {
                            objOut.writeObject(new Message(MessageAction.REGISTER_SUCCESS, null));
                        } else {
                            objOut.writeObject(new Message(MessageAction.REGISTER_FAILED, null));
                        }
                        objOut.flush();
                        break;
                    case THREE_HIGHEST:
                        HashMap<String, HashMap<String, String>> top3player = playerControler.getTop3HighestPlayer();
                        objOut.writeObject(new Message(MessageAction.THREE_HIGHEST, top3player));
                        break;
                    case GET_RANK:
                        HashMap<String, String> rankInfo = playerControler.getRankPlayer(message);
                        objOut.writeObject(new Message(MessageAction.GET_RANK, rankInfo));
                        objOut.flush();
                        break;
                    case NEW_ROOM:
                        HashMap<String, String> maPhong = connectionListener.getControlRoom().reqNewRoom(this);
                        objOut.writeObject(new Message(MessageAction.NEW_ROOM, maPhong));
                        objOut.flush();
                        break;
                    case SEND_INVITE:
                        connectionListener.getControlRoom().reqSendInvite(message, player.getPlayerName());
                        break;
                    case ACCEPT:
                        connectionListener.getControlRoom().respAcceptInvite(message, this);
                        break;
                    case LIST_PLAYER:
                        connectionListener.updateAllPlayers();
                        break;
                    case CHOICE:
                        choice = (int) message.getContent();
                        break;
                    case READY:
                        connectionListener.getControlRoom().setReadyStatus(this);
                        break;
                    case PLAY:
                        break;
                    case OUT_ROOM:
                        connectionListener.getControlRoom().OutRoom(this);
                        connectionListener.updateAllPlayers();
                        break;
                    case DISCONNECT:
                        closeConnection();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    // cập nhật danh sách người chơi
    public void updatePlayerList(HashMap<String, HashMap<String, String>> data) {
        try {
            objOut.writeObject(new Message(MessageAction.LIST_PLAYER, data));
            objOut.flush();
        } catch (IOException e) {
        }
    }


    // đóng kết nối
    private void closeConnection() {
        try {
            try (clientSocket) {
                connectionListener.removeClient(this);
            }
            objOut.close();
            objIn.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
