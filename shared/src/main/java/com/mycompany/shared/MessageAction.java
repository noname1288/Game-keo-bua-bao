/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shared;

/**
 *
 * @author Admin
 */
public enum MessageAction {
    LOGIN,//(1, "Yeu cau dang nhap"),
    LOGIN_SUCCESS,// (2, "Dang nhap thanh cong"),
    LOGIN_FAILED,//(3, "Dang nhap that bai"),
    
    CHECK_DUP,//(4, "Kiem tra trung lap"),
    HV_DUP, //todo
    NO_DUP, //todo
    
    REGISTER,//(5, "Yeu cau dang ki"),
    REGISTER_SUCCESS, //todo
    REGISTER_FAILED, //todo
    
    THREE_HIGHEST,//(6, "Tra ve 3 nguoi cao diem nhat"),
    
    LIST_PLAYER,//(12, "danh sach nguoi dang online"),
    
    GET_RANK,//(7, "Get rank"),
    
       
    INFO_ANOTHER_PLAYER,//(16, "thong bao roi phong"),
    
    NEW_ROOM,//(9, "yeu cau tao phong"),
    SEND_INVITE,//(10, "nguoi dung gui loi moi"),
    ACCEPT,//(11, "nguoi dung dong y"),
    ACCEPTER,//(15, "thong bao chap nhan loi moi"),
    CHOICE,//(13, "lua chon: keo/bua/bao"),
    OUT_ROOM,//(14, "thong bao roi phong"),
    
    TIME_UP, //todo
    RESULT,//todo
    
    INVITE, // todo
    COUNTDOWN,//todo
    
    ROOM_FULL,//(17, "thong bao roi phong"),
    READY,//(18, "Khi ca 2 nguoi dung da san sang"),
    NO_READY,//todo
    PLAY,//(19, "1 trong 2 người chơi đã chọn button PLAY ");
    ENTER_ROOM,
    LIST_ROOM,
    RANK_LIST,
    CANCEL_ROOM,
    
    
    DISCONNECT;//(8, "nguoi dung dong ket noi"),
    
    
    
    
    
    private MessageAction(){
    }
}
