/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.data.entity;

import com.mycompany.myserverpj.network.ClientHandler;

/**
 *
 * @author quang
 */
public class PlayRoom {

    private int maPhong;
    private ClientHandler player1;
    private ClientHandler player2;
    private boolean statusPlayer1;
    private boolean statusPlayer2;

    public PlayRoom(int maPhong, ClientHandler player1) {
        this.maPhong = maPhong;
        this.player1 = player1;
        statusPlayer1 = false;
        statusPlayer2 = false;
    }

    public boolean isStatusPlayer1() {
        return statusPlayer1;
    }

    public boolean isStatusPlayer2() {
        return statusPlayer2;
    }

    public void setStatusPlayer1(boolean statusPlayer1) {
        this.statusPlayer1 = statusPlayer1;
    }

    public void setStatusPlayer2(boolean statusPlayer2) {
        this.statusPlayer2 = statusPlayer2;
    }

    public int getMaPhong() {
        return maPhong;
    }

    public ClientHandler getPlayer1() {
        return player1;
    }

    public void setPlayer1(ClientHandler player1) {
        this.player1 = player1;
    }

    public ClientHandler getPlayer2() {
        return player2;
    }

    public void setPlayer2(ClientHandler player2) {
        this.player2 = player2;
    }

}
