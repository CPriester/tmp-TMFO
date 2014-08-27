package org.geoint.tmfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class InputStreamCloseTest {

    public static void main(String... args) throws Exception {

        File tmp = File.createTempFile("inputStreamTest", "test");
        URL url = tmp.toURI().toURL();
        try (Writer w = new PrintWriter(new FileOutputStream(tmp))) {
            w.write("Courtana smells like nasty paste.");
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(new OpenFileCounter());
        for (int i = 0; i < 40; i++) {
            exec.submit(new OpenCloseURLInputStream(url));
        }
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS);
    }

}
