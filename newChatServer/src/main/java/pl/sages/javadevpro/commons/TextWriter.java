package pl.sages.javadevpro.commons;

import jakarta.enterprise.context.RequestScoped;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Log
public class TextWriter {

    private PrintWriter writer;

    public TextWriter(Socket socket) {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            log.severe("Creating output stream failed: " + e.getMessage());
        }
    }

    public void write(String text) {
        writer.println(text);
    }

}