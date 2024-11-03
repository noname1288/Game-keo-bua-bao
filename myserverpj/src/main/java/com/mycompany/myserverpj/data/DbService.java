package com.mycompany.myserverpj.data;

import com.mycompany.shared.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DbService {

    public DbService (){}

    //các hàm DAO làm việc với DB

    //lay doi tuong player
    public Player getPlayer(String username, String password) {
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT * FROM player WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { // con tro reultSet tro den 1 hang hop le = true || nguoc lai = false
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
            e.printStackTrace();
        }
        return null; // Nếu có lỗi xảy ra
    }

    //lay thong tin 3 player cao diem nhat
    public HashMap<String, HashMap<String, String>> getHighest() {
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
        } catch (NullPointerException | SQLException e) {
            // In ra thông tin lỗi
            // xu ly neu khong toi tieu 3 nguoi choi
            e.printStackTrace();
            System.out.println("He thong can toi thieu 3 nguoi choi");

        }
        return null;
    }

    // dang ky
    public boolean register(String username, String password, String email,
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // kiểm tra trung lặp
    public boolean checkDuplicates(String columnsName, String content) {
        try (Connection connection = ConnectDB.getConnection()) {
            String query = "SELECT * FROM player WHERE " + columnsName + " = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, content.trim());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            //todo
        }
        return false;
    }

    public HashMap<String, String> getPlayerRankAndScore(String playerId) {
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
            e.printStackTrace();
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
            //todo
        }
        return false;
    }

}
