/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.client.model;

/**
 *
 * @author quang
 */
public enum ClientState {
    NULL,
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    HV_DUP,
    NO_DUP,
    REGISTER_SUCCESS,
    REGISTER_FAILED,
    THREE_HIGHEST,
    GET_RANK,
    NEW_ROOM,
    LIST_PLAYER,
    INFO_ANOTHER_PLAYER,
    ROOM_FULL
}
