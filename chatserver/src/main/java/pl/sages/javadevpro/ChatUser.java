package pl.sages.javadevpro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatUser implements Runnable {

    private ChatServer server;
    private ChatRoom room;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;

    public ChatUser(ChatServer server, ChatRoom room, Socket socket) {
        try {
            this.server = server;
            this.room = room;
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = reader.readLine();
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    @Override
    public void run() {
        String messagesFromClient;
        while (socket.isConnected()) {
            try {
                messagesFromClient = reader.readLine();
                if (messagesFromClient.startsWith("/")) {
                    server.commandsHandle(messagesFromClient, this);
                } else {
                    broadcastMessage(messagesFromClient);
                }
            } catch (IOException e) {
                closeAll(socket, reader, writer);
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        room.sendMessageToAllUsers(message, userName);
    }

    public void writeMessage(String message, String fromUser) {
        try {
            writer.write("" + room.getChatRoomName() + ":" + fromUser + "-> " +  message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    public void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if(reader != null) {
                reader.close();
            }
            if(writer != null) {
                writer.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeUser(){
        closeAll(socket, reader, writer);
    }


    public String getUserName() {
        return userName;
    }

    public ChatRoom getRoom() {
        return room;
    }

    public void setRoom(ChatRoom room) {
        this.room = room;
    }

}