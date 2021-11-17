package pl.sages.javadevpro.ftp;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

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
                String dirname = "";

                byte[] data;
                if (input.equals("FILE_SEND_FROM_CLIENT")) {
                    dirname = dataInputStream.readUTF();
                    filename = dataInputStream.readUTF();

                    File targetDir = new File("files", dirname);
                    if (!targetDir.exists())
                        targetDir.mkdirs();
                    File targetFile = new File(targetDir, filename);
                    fileOutputStream = new FileOutputStream(targetFile, true);
                    FileChannel channel = fileOutputStream.getChannel();
                    FileLock lock = channel.lock();

                    fileOutputStream = new FileOutputStream(targetFile);    //??? due to the append mode above


                    dataOutputStream.writeUTF("FILE_AVAILABLE_ON_SERVER");

                    data = dataInputStream.readAllBytes();

                    channel.write(ByteBuffer.wrap(data));

                    fileOutputStream.close();
                } else if (input.equals("DOWNLOAD_FILE")) {
                    dirname = dataInputStream.readUTF();
                    filename = dataInputStream.readUTF();

                    File targetDir = new File("files", dirname);
                    file = new File(targetDir, filename);

                    if (file.isFile()) {
                        fileInputStream = new FileInputStream(file);

                        FileChannel channel = fileInputStream.getChannel();
                        FileLock lock = channel.lock(0, Long.MAX_VALUE, true);

                        dataOutputStream.writeUTF("FILE_AVAILABLE_ON_SERVER");

                        int size = (int)channel.size();
                        data = new byte[size];
                        channel.read(ByteBuffer.wrap(data));
                        fileInputStream.close();
                        dataOutputStream.write(data);
                        dataOutputStream.close();
                    } else {
                        // NO FILE FOUND
                        data = new byte[0];
                        dataOutputStream.write(data);
                        dataOutputStream.close();
                    }
                } else {
                    System.out.println("Error at Server");
                }
                dataInputStream.close();
            } catch (OverlappingFileLockException e) {
                // File is already locked in this thread or virtual machine
                try {
                    dataOutputStream.writeUTF("FILE_UNAVAILABLE_ON_SERVER");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

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