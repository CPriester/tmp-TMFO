package org.geoint.tmfo;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a sub-part of the decorated InputStream, and closes the stream
 * automatically when complete.
 *
 */
public class SubInputStream extends FilterInputStream {

    private long maxLength;

    public SubInputStream(InputStream in, long maxLength) {
        super(in);
        this.maxLength = maxLength;
    }

    @Override
    public int read() throws IOException {
        int result = -1;
        if (maxLength > 0) {
            result = super.read(); //note: because of autoclose, throws exception if EOF has already been returned once
        }
        if (result == -1) {
            super.close(); //autoclose
        }
        return result;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (maxLength == 0) {
            super.close(); //autoclose
            return -1;
        }
        int effRead = (int) Math.min(maxLength, len);
        int result = super.read(b, off, effRead);
        if (result >= 0) {
            maxLength -= result;
        }

        if (result == -1) {
            super.close(); //autoclose
        }

        return result;
    }

    @Override
    public long skip(long n) throws IOException {
        long effRead = Math.min(maxLength, n);
        long result = super.skip(effRead);
        maxLength -= result;
        if (maxLength == 0) {
            super.close();
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        System.out.println("************CLOSED&******");
//        
//            try {
//                throw new RuntimeException("STACKTRACE");
//            } catch (RuntimeException ex) {
//                ex.printStackTrace();
//            }
        this.maxLength = 0;
        super.close();
       
        try {
            super.close();
        } catch (IOException ex) {
            System.out.println("ALREADY CLOSED");
        }
    }
    

           

}
