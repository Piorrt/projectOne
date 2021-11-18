package pl.sages.javadevpro.ftp;

import pl.sages.javadevpro.utils.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPServer implements Server {
    ServerSocket serverSocket;
    Socket socket = null;

    public FTPServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            while(!serverSocket.isClosed()) {
                socket = serverSocket.accept();
                FTPUser ftpUser = new FTPUser(socket);
                Thread thread = new Thread(ftpUser);
                thread.start();
            }
        }
        catch(Exception e) {
            closeServerSocket();
        }
    }
}

