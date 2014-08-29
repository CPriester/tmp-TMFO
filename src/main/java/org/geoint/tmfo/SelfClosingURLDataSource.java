package org.geoint.tmfo;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.activation.URLDataSource;

/**
 * Ensures that one, and only one,{@link InputStream} is available for the URL,
 * and closes it when the underlying resource is exhausted.
 *
 */
public class SelfClosingURLDataSource extends URLDataSource implements Closeable, AutoCloseable {

    private final long length;
    private InputStream streamInstance;
    private final AtomicInteger n = new AtomicInteger();
    private final Object streamLock = new Object();

    public SelfClosingURLDataSource(URL url, long length) {
        super(url);
        this.length = length;
    }

    @Override
    public void close() throws IOException {
        System.out.println("***********CLOSE*****************");
        getInputStream().close();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        synchronized (streamLock) {
            System.out.println("*******getInputStream");
            n.incrementAndGet();
            if (streamInstance == null) {
                streamInstance = new SubInputStream(super.getURL().openStream(), length);
            } else {
               
            }
        }
        return streamInstance;
    }
}
