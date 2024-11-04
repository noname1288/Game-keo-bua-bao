/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.server;

import com.mycompany.myserverpj.data.ConnectDB;
import com.mycompany.myserverpj.network.ConnectionListener;

/**
 *
 * @author quang
 */
public class ServerRun {
    public static void main(String[] args) {
        initializeServer();
    }

    private static void initializeServer() {
        //ket noi database
        ConnectDB con = new ConnectDB();

        //khoi dong server
        ConnectionListener connectionListener = new ConnectionListener();
        connectionListener.openServer();
    }
}
