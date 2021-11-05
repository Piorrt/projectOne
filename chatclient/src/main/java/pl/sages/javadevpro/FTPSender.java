package pl.sages.javadevpro;

import java.io.*;
import java.net.Socket;


public class FTPSender implements Runnable{
    public Socket client = null;
    public DataOutputStream dos = null;
    public FileInputStream fis = null;
    public String filename;
    public String dirname;


    public FTPSender(Socket client, String filename, String dirname) {
        try {
            this.client = client;
            this.filename = filename;
            this.dirname = dirname;
            dos = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            closeAll(client, dos);
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
                fis = new FileInputStream(file);
                data = new byte[fis.available()];
                fis.read(data);
                fis.close();
                filedata = new String(data);
                dos.writeUTF("FILE_SEND_FROM_CLIENT");
                dos.writeUTF(dirname);
                dos.writeUTF(filename);
                dos.writeUTF(filedata);
                System.out.println("File Send Successful!");
            }
            else
            {
                System.out.println("File Not Found!");
            }
        }
        catch(Exception e)
        {

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