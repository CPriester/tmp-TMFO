package org.geoint.tmfo;

import com.sun.management.UnixOperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 *
 */
public class OpenFileCounter implements Runnable {

    private long max = 0;

    @Override
    public void run() {
        while (true) {
            try {
                OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
                if (os instanceof UnixOperatingSystemMXBean) {
                    long numFD = ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
                    max = Math.max(max, numFD);
                    System.out.println(
                            "nofile: "
                            + numFD
                            + "/"
                            + ((UnixOperatingSystemMXBean) os).getMaxFileDescriptorCount()
                            + "; peak: " + max);
                }
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

}
