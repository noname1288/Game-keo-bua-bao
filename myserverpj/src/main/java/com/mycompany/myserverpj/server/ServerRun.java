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
        intializeServer();
    }

    private static void intializeServer() {
        //ket noi database
        ConnectDB conn = new ConnectDB();

        //khoi dong server
        ConnectionListener connectionListener = new ConnectionListener();
        connectionListener.openServer();

    }
}
