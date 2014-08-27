package org.geoint.tmfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.URLDataSource;

/**
 *
 */
public class OpenCloseURLInputStream implements Runnable {

    private final URL url;

    public OpenCloseURLInputStream(URL url) {
        this.url = url;
    }

    @Override
    public void run() {
        DataHandler dh = new DataHandler(new URLDataSource(url));
        for (int i = 0; i < 10000; i++) {
            openClose(dh);
        }
    }

    private void openClose(DataHandler dh) {
        InputStream in = null;
        try {
            in = dh.getInputStream();
            in.read();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
