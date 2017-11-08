package Misc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Logger {
    File file;
    long startTime;
    long finishTime;

    public Logger(String filePath) {
        this.file = new File(filePath);
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop(String name) throws IOException {
        finishTime = System.currentTimeMillis();
        long millis = finishTime - startTime;

        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);

        DataOutputStream dis = new DataOutputStream(new FileOutputStream(file, true));
        dis.writeUTF(name + " took : " + time + System.lineSeparator());

        dis.flush();
        dis.close();

    }
}
