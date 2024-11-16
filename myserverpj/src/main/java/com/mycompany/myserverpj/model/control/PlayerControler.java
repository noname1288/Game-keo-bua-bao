/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.myserverpj.model.control;

import com.mycompany.shared.Message;
import com.mycompany.shared.Player;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author quang
 */
// xu ly tac vu
// login - y tuong: tim doi tuong trong DB de xu ly hien thi sau, neu ko thay
// thi return Login FALSE
public class PlayerControler {

    private final ObjectOutputStream objOut;

    public PlayerControler(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }

    // các hàm xử lý sự kiện client
    //xử lý login
    public Player login(Message message) throws IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        Player player = getPlayer(data.get("username"),
                data.get("password"));
        if (player != null) {
            return player;
        }
        return null;
    }

    public boolean checkDuplicate(Message message) throws IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        Set<String> key = data.keySet();
        String field = null;
        for (String element : key) {
            field = element;
            break;
        }
        if ("USERNAME".equals(field)) {
            if (checkDuplicates("username", data.get(field))) {
                objOut.writeObject(new Message("HV_DUP", null));
            } else {
                objOut.writeObject(new Message("NO_DUP", null));
            }
        } else if ("EMAIL".equals(field)) {
            if (checkDuplicates("email", data.get(field))) {
                objOut.writeObject(new Message("HV_DUP", null));
            } else {
                objOut.writeObject(new Message("NO_DUP", null));
            }
        } else if ("PLAYER_NAME".equals(field)) {
            if (checkDuplicates("playerName", data.get(field))) {
                objOut.writeObject(new Message("HV_DUP", null));
            } else {
                objOut.writeObject(new Message("NO_DUP", null));
            }
        }
        return true;
    }

    public void register(Message message) throws SQLException, IOException {
        HashMap<String, String> data
                = (HashMap<String, String>) message.getContent();
        boolean c = register(
                data.get("username"),
                data.get("password"),
                data.get("email"),
                data.get("playerName")
        );
        if (c) {
            objOut.writeObject(new Message("REGISTER_SUCCESS", null));
        } else {
            objOut.writeObject(new Message("REGISTER_FAILED", null));
        }
    }

    // gửi các thông tin để hiển thị trên màn hình chính
    public boolean getThreeHighest() {
        // gửi về 3 người chơi cao điểm nhất
        try {
            objOut.writeObject(new Message("THREE_HIGHEST", getHighest()));
        } catch (IOException e) {
        }

        return true;
    }

    public void getRankPlayer(Message message) {
        try {
            objOut.writeObject(new Message(
                    "GET_RANK",
                    getPlayerRankAndScore((String) message.getContent()))
            );
        } catch (IOException e) {
        }
    }
    
    public void getRankList() {
        try {
            objOut.writeObject(new Message(
                    "RANK_LIST",
                    getRank()
            ));
        } catch (IOException e) {
        }
    }

    //các hàm DAO làm việc với DB
    //lay doi tuong player
    private Player getPlayer(String username, String password) {
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT * FROM player WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Player player = new Player(
                        resultSet.getString("ID"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("playerName"),
                        resultSet.getString("email"),
                        resultSet.getFloat("score")
                );
                // thêm chô này

                return player;
            } else {
                return null; // Không tìm thấy người chơi
            }
        } catch (Exception e) {
            // In ra thông tin lỗi

        }
        return null; // Nếu có lỗi xảy ra
    }

    //lay thong tin 3 player cao diem nhat
    private HashMap<String, HashMap<String, String>> getHighest() {
        HashMap<String, HashMap<String, String>> rs = new HashMap<>();
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT * FROM player ORDER BY score DESC LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            int rank = 1;
            while (resultSet.next()) {
                HashMap<String, String> playerData = new HashMap<>();
                playerData.put(
                        "playerName",
                        resultSet.getString("playerName")
                );
                playerData.put(
                        "score",
                        String.valueOf(
                                resultSet.getString("score")
                        )
                );
                rs.put(String.valueOf(rank), playerData);
                rank++;
            }
            return rs;
        } catch (Exception e) {
            // In ra thông tin lỗi

        }
        return null;
    }

    // dang ky
    private boolean register(String username, String password, String email,
            String playerName) throws SQLException {
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "INSERT INTO player "
                    + "(username, password, playerName, email) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, playerName);
            statement.setString(4, email);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
        }
        return false;
    }

    // kiểm tra trung lặp
    private boolean checkDuplicates(String columnsName, String content) {
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT * FROM player WHERE " + columnsName + " = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, content.trim());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private HashMap<String, String> getPlayerRankAndScore(String playerId) {
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT rank, score FROM ( "
                    + "SELECT ID, score, RANK() OVER (ORDER BY score DESC) AS rank "
                    + "FROM player) AS ranked "
                    + "WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, playerId);
            ResultSet resultSet = statement.executeQuery();
            HashMap<String, String> data = new HashMap<>();

            if (resultSet.next()) {
                int rank = resultSet.getInt("rank");
                float score = resultSet.getFloat("score");
                data.put("rank", String.valueOf(rank));
                data.put("score", String.valueOf(score));
            }
            return data;
        } catch (Exception e) {
            // In ra thông tin lỗi
        }
        return null; // Trả về null nếu không tìm thấy người chơi
    }

    public boolean updateScore(String ID, float score) {
        try (Connection connection = ConnectDB.getConnection()) {
            String updateQuery = "UPDATE player SET score = ? WHERE ID = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setFloat(1, score);
            updateStatement.setString(2, ID);
            int rowsUpdated = updateStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
        }
        return false;
    }
    
    private HashMap<String, HashMap<String, String>> getRank() {
        HashMap<String, HashMap<String, String>> data = new HashMap<>();
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT * FROM player ORDER BY score DESC";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            int rank = 1;
            while (resultSet.next()) {
                HashMap<String, String> playerData = new HashMap<>();
                playerData.put(
                        "playerName",
                        resultSet.getString("playerName")
                );
                playerData.put(
                        "score",
                        String.valueOf(
                                resultSet.getString("score")
                        )
                );
                data.put(String.valueOf(rank), playerData);
                rank++;
            }
            return data;
        } catch (Exception e) {
            // In ra thông tin lỗi

        }
        return null;
    }

}
