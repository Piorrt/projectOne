package pl.sages.javadevpro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHolder implements Runnable {

    public static ArrayList<ClientHolder> clientHolders = new ArrayList<>();

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUserName;

    public ClientHolder(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = reader.readLine();
            clientHolders.add(this);
            broadcastMessage("SERVER: " + clientUserName + " has connected to the chat.");
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
                broadcastMessage(messagesFromClient);
            } catch (IOException e) {
                closeAll(socket, reader, writer);
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHolder client: clientHolders) {
            try {
                if (!client.clientUserName.equals(this.clientUserName)) {
                    client.writer.write(message);
                    client.writer.newLine();
                    client.writer.flush();
                }
            } catch (IOException e) {
                closeAll(socket, reader, writer);
            }
        }
    }

    public void removeClient() {
        clientHolders.remove(this);
        broadcastMessage("SERVER: " + clientUserName + " has left the chat.");
    }

    public void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        removeClient();
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

}