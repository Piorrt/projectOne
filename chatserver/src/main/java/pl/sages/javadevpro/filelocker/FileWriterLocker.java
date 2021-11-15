package pl.sages.javadevpro.filelocker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import static java.lang.Thread.sleep;

public class FileWriterLocker implements Runnable {
    private File file = null; //new File("lock.test");
    private String filename;

    public FileWriterLocker(String filename) {
        this.filename = filename;
        this.file = new File(filename);
    }


    @Override
    public void run() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);

            FileLock lock = fos.getChannel().lock();
            System.out.println("File " + filename + " locked");

            // do some file write operation

            sleep(20_000);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    System.out.println("File " + filename + " unlocked");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}