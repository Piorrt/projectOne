package pl.sages.javadevpro;

import java.io.*;
import java.net.Socket;

public class FTPDownloader implements Runnable {
    public Socket client = null;
    public DataInputStream dis = null;
    public DataOutputStream dos = null;
    public FileOutputStream fos = null;
    public String filename;
    public String dirname;

    public FTPDownloader(Socket client, String filename, String dirname) {
        try {
            this.client = client;
            this.filename = filename;
            this.dirname = dirname;
            dis = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            closeAll(client, dis, dos);
        }
    }

    @Override
    public void run() {
        try {
            String filedata = "";
            dos.writeUTF("DOWNLOAD_FILE");
            dos.writeUTF(dirname);
            dos.writeUTF(filename);
            filedata = dis.readUTF();
            if (filedata.equals("")) {
                System.out.println("No Such File");
            } else {
                fos = new FileOutputStream(filename);
                fos.write(filedata.getBytes());
                fos.close();
                System.out.println("File Download Successful!");
            }
        } catch (Exception e) {

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