package pl.sages.javadevpro.ftp;

import java.io.*;
import java.net.Socket;


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
            String filedata="";
            File file;
            byte[] data;
            file = new File(filename);
            if(file.isFile())
            {
                fileInputStream = new FileInputStream(file);
                data = new byte[fileInputStream.available()];
                fileInputStream.read(data);
                fileInputStream.close();
                filedata = new String(data);
                dataOutputStream.writeUTF("FILE_SEND_FROM_CLIENT");
                dataOutputStream.writeUTF(dirname);
                dataOutputStream.writeUTF(filename);
                dataOutputStream.writeUTF(filedata);
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