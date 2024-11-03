/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author quang
 */
public class ConnectDB {
    private static HikariDataSource dataSource;
    
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/laptrinhmang");
            config.setUsername("root");
            config.setPassword("");
            config.setMaximumPoolSize(10); //limit 10 connections
            config.setMinimumIdle(2); //at least 2 connections open in the pool
            config.setConnectionTimeout(30000); //set timeout for connection request to 30s

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            // neu khong ket noi duoc voi db
            e.printStackTrace();
        }

    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized due to a previous error.");
        }
        return dataSource.getConnection();
    }
}
