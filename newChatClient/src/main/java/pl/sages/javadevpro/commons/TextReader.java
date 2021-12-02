package pl.sages.javadevpro.commons;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

@Log
public class TextReader {

    private final Consumer<String> textConsumer;
    private BufferedReader reader;
    private Runnable onClose;

    public TextReader(InputStream inputStream, Consumer<String> textConsumer) {
        this.textConsumer = textConsumer;
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public TextReader(Socket socket, Consumer<String> textConsumer, Runnable onClose) {
        this.textConsumer = textConsumer;
        this.onClose = onClose;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            log.severe("Creating input stream failed: " + e.getMessage());
        }
    }

    public void read() {
        String text;
        try {
            while ((text = reader.readLine()) != null) {
                if (text.startsWith("SERVER:")) {
                    processServerMessage(text);
                } else {
                    textConsumer.accept(text);
                }
            }
        } catch (IOException e) {
            log.severe("Read message failed: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("SERVER: Connection refused - your name is not unique");
        }
        finally {
            if (onClose != null) {
                onClose.run();
            }
        }
    }

    private void processServerMessage(String serverMessage) {
        if (serverMessage.contains("Name accepted.")) {
            System.err.println("SERVER: Connection established - you are connected to general room");
        } else {
            throw new RuntimeException("User name not unique");
        }
    }
}