package com.mycompany.client.control;

import com.mycompany.client.model.ClientState;
import com.mycompany.client.view.MainPanel;
import com.mycompany.shared.Message;
import com.mycompany.shared.MessageAction;
import com.mycompany.shared.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocket {

    private Socket clientSocket;

    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    private final String serverHost = "127.0.0.1";
    private final int serverPort = 8080;

    private Player player;
    private MainPanel mainPanel;
    private volatile ClientState state;
    private HashMap<?, ?> data;
    // Hàng đợi để lưu trữ các thông điệp từ server
    private final BlockingQueue<Message> messageQueue;

    //GETTER SETTER
    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public HashMap<?, ?> getData() {
        return data;
    }

    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public Player getPlayer() {
        return player;
    }

    public ClientSocket() {
        messageQueue = new LinkedBlockingQueue<>();
    }

    public Socket requestConnection() throws IOException {
        try {
            clientSocket = new Socket(serverHost, serverPort);
            this.objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objIn = new ObjectInputStream(clientSocket.getInputStream());

            // Khởi động thread để nhận dữ liệu từ server
            new Thread(new ListenFromServer()).start();

            // Khởi động thread để xử lý thông điệp trong hàng đợi
            new Thread(new MessageHandler()).start();
        } catch (IOException e) {
            return null;
        }
        return clientSocket;
    }

    // inner class để nghe thông tin từ server gửi về
    class ListenFromServer implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    // Nhận thông điệp từ server
                    Message message = (Message) objIn.readObject();
                    System.out.println(message.getType() + " " + (message.getContent() != null ? message.getContent().toString() : "Content is null"));
                    // Đưa thông điệp vào hàng đợi
                    messageQueue.put(message);
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
            }
        }
    }

    // inner class để xử lý thông điệp trong hàng đợi
    class MessageHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    // Lấy thông điệp từ hàng đợi
                    Message message = messageQueue.take();
                    // Xử lý thông điệp
                    handleMessage(message);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // các message nhận thụ động từ server
    private void handleMessage(Message message) throws InterruptedException {
        if (null != message.getType()) {
            switch (message.getType()) {
                case LOGIN_SUCCESS:
                    state = ClientState.LOGIN_SUCCESS;
                    player = (Player) message.getContent();
                    break;
                case LOGIN_FAILED:
                    state = ClientState.LOGIN_FAILED;
                    break;
                case LIST_PLAYER:
                    data = (HashMap<String, HashMap<String, String>>) message.getContent();
                    state = ClientState.LIST_PLAYER;
                    if (mainPanel != null) {
                        mainPanel.setListPlayer((HashMap<String, HashMap<String, String>>) data);
                    }
                    break;
                case HV_DUP:
                    state = ClientState.HV_DUP;
                    break;
                case NO_DUP:
                    state = ClientState.NO_DUP;
                    break;
                case THREE_HIGHEST:
                    state = ClientState.THREE_HIGHEST;
                    data = (HashMap<String, HashMap<String, String>>) message.getContent();
                    break;
                case GET_RANK:
                    state = ClientState.GET_RANK;
                    data = (HashMap<String, String>) message.getContent();
                    break;
                case NEW_ROOM:
                    state = ClientState.NEW_ROOM;
                    data = (HashMap<String, String>) message.getContent();
                    break;
                case INVITE:
                    data = (HashMap<String, String>) message.getContent();
                    String inviterPlayer = (String) data.get("inviter");
                    String maPhong = (String) data.get("maPhong");
                    mainPanel.showInviteDialog(inviterPlayer, maPhong);
                    break;
                case ACCEPTER:
                    mainPanel.setPlayRoomAnotherPlayer((Player) message.getContent());
                    mainPanel.showPlayRoom();
                    break;
                case INFO_ANOTHER_PLAYER:
                    state = ClientState.INFO_ANOTHER_PLAYER;
                    mainPanel.setPlayRoomAnotherPlayer((Player) message.getContent());
                    mainPanel.showPlayRoom();
                    break;
                case ROOM_FULL:
                    state = ClientState.ROOM_FULL;
                    break;
                case READY:
                    mainPanel.getPlayRoom().hidePlayButtonAndChangeLabel();
                    break;
                case PLAY:
                    mainPanel.getPlayRoom().hidePlayButtonAndChangeLabel();
                    break;
                case COUNTDOWN:
                    mainPanel.getPlayRoom().chanceTime(String.valueOf(message.getContent()));
                    break;
                case TIME_UP:
                    try {
                        objOut.writeObject(new Message(MessageAction.CHOICE, mainPanel.getPlayRoom().getChoice()));
                    } catch (IOException e) {
                    }
                    break;
                case RESULT:
                    processingResults((String) message.getContent());
                    break;
                default:
                    break;
            }
        }
    }

    // gửi thông điệp login
    public void Login(String username, String password) throws IOException, ClassNotFoundException {
        HashMap<String, String> dataSend = new HashMap<>();
        dataSend.put("username", username);
        dataSend.put("password", password);
        try {
            objOut.writeObject(new Message(MessageAction.LOGIN, dataSend));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    // kiểm tra trùng lặp username, email, playerName
    public void checkDuplicates(String type, String content) {
        HashMap<String, String> dataSend = new HashMap<>();
        dataSend.put((type), content);
        try {
            objOut.writeObject(new Message(MessageAction.CHECK_DUP, dataSend));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void register(String username, String password, String email, String playerName) {
        HashMap<String, String> dataSend = new HashMap<>();
        dataSend.put("username", username);
        dataSend.put("email", email);
        dataSend.put("password", password);
        dataSend.put("playerName", playerName);
        try {
            objOut.writeObject(new Message(MessageAction.REGISTER, dataSend));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void getThreeHighest() {
        try {
            objOut.writeObject(new Message(MessageAction.THREE_HIGHEST, null));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void getListPlayers() {
        try {
            objOut.writeObject(new Message(MessageAction.LIST_PLAYER, null));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void getRank(String ID) {
        try {
            objOut.writeObject(new Message(MessageAction.GET_RANK, ID));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void newRoom() throws IOException {
        try {
            objOut.writeObject(new Message(MessageAction.NEW_ROOM, player));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void sendInvite(String playerName) {
        try {
            objOut.writeObject(new Message(MessageAction.SEND_INVITE, playerName));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void acceptInvite(String inviter) {
        try {
            objOut.writeObject(new Message(MessageAction.ACCEPT, inviter));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void disconnection() throws IOException {
        objOut.writeObject(new Message(MessageAction.DISCONNECT, null));
        objOut.flush();
    }

    public void ready(String maPhong) {
        try {
            objOut.writeObject(new Message(MessageAction.READY, maPhong));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    public void noready(String maPhong) {
        try {
            objOut.writeObject(new Message(MessageAction.NO_READY, maPhong));
            objOut.flush();
        } catch (IOException e) {
        }
    }

    private void processingResults(String result) {
        System.out.println(result);
        switch (result) {
            case "HOA":
                // cộng điểm cho mình
                player.updateScore(0.5f);
                // cập nhật hiển thị score của mình trong PlayRoom
                mainPanel.getPlayRoom().setThisPlayerInfo();
                // cập nhật hiển thị score của người chơi khác trong PlayRoom
                mainPanel.getPlayRoom().updateAnotherPlayerScore(0.5f);
                //thay đổi thông báo tháng thua trong room
                mainPanel.getPlayRoom().chanceTime("HÒA");
                // reset lại phòng để bắt dầu vòng chơi mới
                mainPanel.getPlayRoom().resetRoom();
                break;
            case "THANG":
                // cộng điểm cho mình
                player.updateScore(1f);
                //thay đổi thông báo tháng thua trong room
                mainPanel.getPlayRoom().chanceTime("THẮNG");
                // cập nhật hiển thị score của mình trong PlayRoom
                mainPanel.getPlayRoom().setThisPlayerInfo();
                // reset lại phòng để bắt dầu vòng chơi mới
                mainPanel.getPlayRoom().resetRoom();
                break;
            case "THUA":
                // cập nhật hiển thị score của người chơi khác trong PlayRoom
                mainPanel.getPlayRoom().updateAnotherPlayerScore(1f);
                //thay đổi thông báo tháng thua trong room
                mainPanel.getPlayRoom().chanceTime("THUA");
                // reset lại phòng để bắt dầu vòng chơi mới
                mainPanel.getPlayRoom().resetRoom();
                break;
            default:
                throw new AssertionError();
        }
    }

    //thông báo rời phòng
    public void outRoom() {
        try {
            objOut.writeObject(new Message(MessageAction.OUT_ROOM, null));
        } catch (IOException e) {
        }
    }
}
