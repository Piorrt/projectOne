package pl.sages.javadevpro.ftp;

import java.io.*;
import java.net.Socket;

public class FTPUser implements Runnable {
    public Socket client = null;
    public DataInputStream dataInputStream = null;
    public DataOutputStream dataOutputStream = null;
    public FileInputStream fileInputStream = null;
    public FileOutputStream fileOutputStream = null;
    public File file = null;

    public FTPUser(Socket c) {
        try {
            client = c;
            dataInputStream = new DataInputStream(c.getInputStream());
            dataOutputStream = new DataOutputStream(c.getOutputStream());

        } catch (Exception e) {
            closeAll(client, dataInputStream, dataOutputStream);
        }
    }

    @Override
    public void run() {
            try {
                String input = dataInputStream.readUTF();
                String filename = "";
                String filedata = "";
                String dirname = "";

                byte[] data;
                if (input.equals("FILE_SEND_FROM_CLIENT")) {
                    dirname = dataInputStream.readUTF();
                    filename = dataInputStream.readUTF();
                    filedata = dataInputStream.readUTF();
                    File targetDir = new File("files", dirname);
                    if (!targetDir.exists())
                        targetDir.mkdirs();
                    File targetFile = new File(targetDir, filename);
                    fileOutputStream = new FileOutputStream(targetFile);

                    fileOutputStream.write(filedata.getBytes());
                    fileOutputStream.close();
                } else if (input.equals("DOWNLOAD_FILE")) {
                    dirname = dataInputStream.readUTF();
                    filename = dataInputStream.readUTF();

                    File targetDir = new File("files", dirname);
                    file = new File(targetDir, filename);

                    if (file.isFile()) {
                        fileInputStream = new FileInputStream(file);
                        data = new byte[fileInputStream.available()];
                        fileInputStream.read(data);
                        filedata = new String(data);
                        fileInputStream.close();
                        dataOutputStream.writeUTF(filedata);
                    } else {
                        dataOutputStream.writeUTF(""); // NO FILE FOUND
                    }
                } else {
                    System.out.println("Error at Server");
                }
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