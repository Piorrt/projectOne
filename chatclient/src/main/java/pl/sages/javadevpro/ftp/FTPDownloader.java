package pl.sages.javadevpro.ftp;

import java.io.*;
import java.net.Socket;

public class FTPDownloader implements Runnable {
    public Socket client = null;
    public DataInputStream dataInputStream = null;
    public DataOutputStream dataOutputStream = null;
    public FileOutputStream fileOutputStream = null;
    public String filename;
    public String dirname;

    public FTPDownloader(Socket client, String filename, String dirname) {
        try {
            this.client = client;
            this.filename = filename;
            this.dirname = dirname;
            dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            closeAll(client, dataInputStream, dataOutputStream);
        }
    }

    @Override
    public void run() {
        try {
            byte[] data;
            dataOutputStream.writeUTF("DOWNLOAD_FILE");
            dataOutputStream.writeUTF(dirname);
            dataOutputStream.writeUTF(filename);
            data = dataInputStream.readAllBytes();
            if (data.length == 0) {
                System.out.println("No Such File");
            } else {
                fileOutputStream = new FileOutputStream(filename);
                fileOutputStream.write(data);
                fileOutputStream.close();
                System.out.println("File Download Successful!");
            }
            dataOutputStream.close();
            dataInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeAll(Socket socket, DataInputStream dis, DataOutputStream dos) {
        try {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}