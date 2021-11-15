package pl.sages.javadevpro.filelocker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import static java.lang.Thread.sleep;

public class FileReaderLocker implements Runnable {
    private File file = null; //new File("lock.test");
    private String filename;

    public FileReaderLocker(String filename) {
        this.filename = filename;
        this.file = new File(filename);
    }


    @Override
    public void run() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            FileLock lock = fis.getChannel().lock(0, Long.MAX_VALUE, true);
            System.out.println("File " + filename + " locked");

            // do some file read operation

            sleep(20_000);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    System.out.println("File " + filename + " unlocked");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}