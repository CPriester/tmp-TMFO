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
    private final long length;

    public OpenCloseURLInputStream(URL url, long length) {
        this.url = url;
        this.length = length;
    }

    @Override
    public void run() {
        System.out.println("DataHandler test on url: " + url.toString());
        DataHandler dh = new DataHandler(new SelfClosingURLDataSource(url, length));
  //      DataHandler dh = new DataHandler(new URLDataSource(url));
        for (int i = 0; i < 10000; i++) {
            openClose(dh);
        }
    }

    private void openClose(DataHandler dh) {
//        InputStream in = null;
        try {
//            in = dh.getInputStream();
            String mime = dh.getContentType();
            System.out.println("ContentType: "+mime);
//           Object obj= dh.getContent();
//            System.out.println(obj.getClass().getName());
////            in.read();
//            if (InputStream.class.isAssignableFrom(obj.getClass())) {
//                ((InputStream)obj).read();
//            }
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
//            try {
//                in.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }
    }

}
