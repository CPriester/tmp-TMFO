package org.geoint.tmfo;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class InputStreamCloseTest {

    private static final int SMTP_PORT = 9999;
    private static final String SMTP_HOST = "127.0.0.1";
    private static String string;
    private static GreenMail mailServer;
    static ServerSetup gmConfig;
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public static void main(String... args) throws Exception {
//        testStreams();
        sendEmailTest();
    }

    private static void testStreams() throws Exception {
        File tmp = File.createTempFile("inputStreamTest", "test");
        URL url = tmp.toURI().toURL();
        try (Writer w = new PrintWriter(new FileOutputStream(tmp))) {
            w.write("Courtana smells like nasty paste.");
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(new OpenFileCounter());
        for (int i = 0; i < 40; i++) {
            exec.submit(new OpenCloseURLInputStream(url, tmp.length()));
        }
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS);
    }

    public static void sendEmailTest() throws Exception {
//        gmConfig = new ServerSetup( SMTP_PORT , SMTP_HOST, "smtp");
 //       mailServer = new GreenMail(gmConfig);
//        mailServer.start();

        create(queue);

        List<OpenCloseURLInputStream> list = new ArrayList<>();

        int processors = Runtime.getRuntime().availableProcessors() * 2;

        ExecutorService exec = Executors.newFixedThreadPool(processors);
        exec.submit(new OpenFileCounter());
        for (int i = 0; i < processors; i++) {
            exec.submit(new SendEmailTest(queue));
        }
        while (!queue.isEmpty()) {
            Thread.sleep(1);
        }

        for (OpenCloseURLInputStream l : list) {

            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.DAYS);
        }
    }

    public static void create(BlockingQueue<String> queue) {

        try {
            string = "I HATE TOO MANY THREADS";

            for (int p = 0; p < 2000; p++) {
                queue.put(string);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(InputStreamCloseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
