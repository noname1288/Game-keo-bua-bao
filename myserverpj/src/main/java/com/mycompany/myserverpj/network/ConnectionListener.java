package com.mycompany.myserverpj.network;

import com.mycompany.myserverpj.data.ConnectDB;
import com.mycompany.myserverpj.game.GameManager;
import com.mycompany.myserverpj.game.controller.PlayRoomController;
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
public class ConnectionListener {

    private ServerSocket server = null;
    private ConnectDB con = null;

    final private int port = 8080;
    private GameManager gameManager;

    public ConnectionListener(){}

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void openServer() {
        try {
            server = new ServerSocket(port);
            gameManager = new GameManager(this );

            while (true) {
                listenning();
            }
        } catch (IOException e) {
        }
    }

    public void listenning() throws IOException {
        try {
            ClientThread client = new ClientThread(server.accept(), this.gameManager);
            System.out.println("1 nguoi da dang nhap");
        } catch (IOException e) {
        }
    }

}
