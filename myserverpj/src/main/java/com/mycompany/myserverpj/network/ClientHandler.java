/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.network;

import com.mycompany.myserverpj.game.GameManager;
import com.mycompany.shared.Message;
import com.mycompany.shared.Player;
import com.mycompany.myserverpj.game.controller.PlayerControler;
import com.mycompany.shared.MessageAction;
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
public class ClientHandler extends Thread {

    private final Socket clientSocket;
    private final GameManager gameManager;
    private PlayerControler control;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private Player player;
    private boolean status;

    // tạo biến lưu lựa chọn ở đây cho đơn giản
    private int choice;

    public ClientHandler(Socket clientSocket, GameManager gameManager) {
        this.clientSocket = clientSocket;
        this.gameManager = gameManager;
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
                System.out.println(message.getType() +" " + (message.getContent() != null ? message.getContent().toString() : "Content is null"));
                // Xử lý yêu cầu đăng nhập
                switch (message.getType()) {
                    case LOGIN:
                        player = control.login(message);
                        if (player != null) {
                            if (gameManager.checkCLientOn(player)) {
                                objOut.writeObject(new Message(MessageAction.LOGIN_FAILED , null));
                            } else {
                                status = true;
                                objOut.writeObject(new Message(MessageAction.LOGIN_SUCCESS, player));
                                gameManager.updateClientList(this);
                            }
                        } else {
                            objOut.writeObject(new Message(MessageAction.LOGIN_FAILED, null));
                        }
                        objOut.flush();
                        break;
                    case CHECK_DUP:
                        control.checkDuplicate(message);
                        break;
                    case REGISTER:
                        control.register(message);
                        break;
                    case THREE_HIGHEST:
                        control.getThreeHighest();
                        break;
                    case GET_RANK:
                        control.getRankPlayer(message);
                        break;
                    case DISCONNECT:
                        closeConnection();
                        break;
                    case NEW_ROOM:
                        gameManager.getControlRoom().reqNewRoom(this);
                        break;
                    case SEND_INVITE:
                        gameManager.getControlRoom().reqSendInvite(message, player.getPlayerName());
                        break;
                    case ACCEPT:
                        gameManager.getControlRoom().respAcceptInvite(message, this);
                        break;
                    case LIST_PLAYER:
                        gameManager.updateAllPlayers();
                        break;
                    case LIST_ROOM:
                        gameManager.updateListRoom();
                        break;
                    case CHOICE:
                        choice = (int) message.getContent();
                        break;
                    case READY:
                        gameManager.getControlRoom().setReadyStatus(this);
                        break;
                    case PLAY:
                        break;
                    case OUT_ROOM:
                        gameManager.getControlRoom().OutRoom(this);
                        gameManager.updateAllPlayers();
                        break;
                    case ENTER_ROOM:
                        gameManager.getControlRoom().reqEnterRoom(message, this);
                        break;
                    case RANK_LIST:
                        control.getRankList();
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
                gameManager.removeClient(this);
            }
            objOut.close();
            objIn.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
