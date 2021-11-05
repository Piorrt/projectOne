package pl.sages.javadevpro;

import java.io.*;
import java.net.Socket;

public class FTPUser implements Runnable {
    public Socket client = null;
    public DataInputStream dis = null;
    public DataOutputStream dos = null;
    public FileInputStream fis = null;
    public FileOutputStream fos = null;
    public BufferedReader br = null;
    public File file = null;

    public FTPUser(Socket c) {
        try {
            client = c;
            dis = new DataInputStream(c.getInputStream());
            dos = new DataOutputStream(c.getOutputStream());

        } catch (Exception e) {
            closeAll(client, dis, dos);
        }
    }

    @Override
    public void run() {
            try {
                String input = dis.readUTF();
                String filename = "", filedata = "", dirname = "";
                byte[] data;
                if (input.equals("FILE_SEND_FROM_CLIENT")) {
                    dirname = dis.readUTF();
                    filename = dis.readUTF();
                    filedata = dis.readUTF();
                    File targetDir = new File("files", dirname);
                    if (!targetDir.exists())
                        targetDir.mkdirs();
                    File targetFile = new File(targetDir, filename);
                    fos = new FileOutputStream(targetFile);

                    fos.write(filedata.getBytes());
                    fos.close();
                } else if (input.equals("DOWNLOAD_FILE")) {
                    dirname = dis.readUTF();
                    filename = dis.readUTF();

                    File targetDir = new File("files", dirname);
                    file = new File(targetDir, filename);

                    if (file.isFile()) {
                        fis = new FileInputStream(file);
                        data = new byte[fis.available()];
                        fis.read(data);
                        filedata = new String(data);
                        fis.close();
                        dos.writeUTF(filedata);
                    } else {
                        dos.writeUTF(""); // NO FILE FOUND
                    }
                } else {
                    System.out.println("Error at Server");
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