/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shared;

import java.io.Serializable;

/**
 *
 * @author quang
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 123L;
    
    private String ID;
    private String username;
    private String password;
    private String playerName;
    private String email;
    private float score;
    private int rank;

    public Player(String ID, String username, String password, String playerName, String email, float score) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.playerName = playerName;
        this.email = email;
        this.score = score;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
    
    public float updateScore(float score) {
        this.score += score;
        return this.score;
        // goi database
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
