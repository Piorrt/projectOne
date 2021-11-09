package pl.sages.javadevpro.ftp;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FTPSender implements Runnable{
    public Socket client = null;
    public DataOutputStream dataOutputStream = null;
    public FileInputStream fileInputStream = null;
    public String filename;
    public String dirname;


    public FTPSender(Socket client, String filename, String dirname) {
        try {
            this.client = client;
            this.filename = filename;
            this.dirname = dirname;
            dataOutputStream = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            closeAll(client, dataOutputStream);
        }
    }

    @Override
    public void run()
    {
        try{
            File file;
            byte[] data;
            file = new File(filename);

            Path path = Paths.get(filename);
            filename = path.getFileName().toString();

            if(file.isFile())
            {
                fileInputStream = new FileInputStream(file);
                data = new byte[fileInputStream.available()];
                fileInputStream.read(data);
                fileInputStream.close();

                dataOutputStream.writeUTF("FILE_SEND_FROM_CLIENT");
                dataOutputStream.writeUTF(dirname);
                dataOutputStream.writeUTF(filename);
                dataOutputStream.write(data);
                dataOutputStream.close();
                System.out.println("File Send Successful!");
            }
            else
            {
                System.out.println("File Not Found!");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void closeAll(Socket socket, DataOutputStream dos) {
        try {
            if(dos != null) {
                dos.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}