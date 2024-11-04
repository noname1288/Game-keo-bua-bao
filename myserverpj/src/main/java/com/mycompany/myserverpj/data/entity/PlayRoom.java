/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.data.entity;

import com.mycompany.myserverpj.network.ClientThread;

/**
 *
 * @author quang
 */
public class PlayRoom {

    private int maPhong;
    private ClientThread player1;
    private ClientThread player2;
    private boolean statusPlayer1;
    private boolean statusPlayer2;

    public PlayRoom(int maPhong, ClientThread player1) {
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

    public ClientThread getPlayer1() {
        return player1;
    }

    public void setPlayer1(ClientThread player1) {
        this.player1 = player1;
    }

    public ClientThread getPlayer2() {
        return player2;
    }

    public void setPlayer2(ClientThread player2) {
        this.player2 = player2;
    }

}
