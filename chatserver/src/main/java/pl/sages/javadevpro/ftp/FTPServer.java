package pl.sages.javadevpro.ftp;

import java.io.*;
import java.net.*;

public class FTPServer implements Runnable {
    ServerSocket serverSocket = null;
    Socket socket = null;

    public FTPServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

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

        try{
            while(!serverSocket.isClosed())
            {
                socket = serverSocket.accept();
                FTPUser ftpUser = new FTPUser(socket);
                Thread thread = new Thread(ftpUser);
                thread.start();
            }
        }
        catch(Exception e)
        {
            closeServerSocket();
        }
    }
}

