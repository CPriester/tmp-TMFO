package org.geoint.tmfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import static javax.mail.Part.ATTACHMENT;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 *
 */
public class SendEmailTest implements Runnable {

    private final static Logger LOGGER = Logger.getLogger("org.geoint.tmfo.OpenClosedInputStream");

    private final Properties properties = new Properties();
    private final BlockingQueue<String> queue;
    private volatile boolean run = true;
    private Transport t;
    private Thread myThread;
    private Session session;

    public SendEmailTest(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void shutdown() {
        this.run = false;
        LOGGER.info("Stopping 'MailMsgWorker'");
        myThread.interrupt();

    }

    @Override
    public void run() {

        String string = null;
        session = createMailSession();

        while (this.run) {
            try {
                string = queue.take();
                MimeMessage mimeMsg = create(session);

                t = session.getTransport("smtp");
                t.connect();

                 t.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
                mimeMsg = null;
               t.close();
            } catch (MessagingException ex) {
                Logger.getLogger(OpenCloseURLInputStream.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("messagingexception " + ex.getStackTrace());
            } catch (InterruptedException ex) {
                Thread.interrupted();
            }
        }
    }

    public MimeMessage create(Session session) {
        MimeMessage message = new MimeMessage(session);
        Multipart multipart = new MimeMultipart("related");
        try {

            String to = "jdharri@gmail.com";
            String from = "yourmom@gmail.com";
            String host = "localhost";
            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.host", host);
            session = Session.getDefaultInstance(properties);
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject("This is the Subject Line!");
            message.setText("This is actual message");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            File tmp = File.createTempFile("inputStreamTest", "test");
            try (Writer w = new PrintWriter(new FileOutputStream(tmp))) {
                w.write("Steve smells like nasty doo .");
            } catch (Exception e) {
            }

//            for (int i = 0; i < 2; i++) {
                addAttachmentByURL(tmp, attachmentPart, multipart);
//                addAttachmentByFile(tmp, attachmentPart, multipart);
//            }

            message.setContent(multipart);
            message.saveChanges();
        } catch (MessagingException ex) {
            Logger.getLogger(InputStreamCloseTest.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(InputStreamCloseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }

    private void addAttachmentByFile(File tmp, MimeBodyPart attachmentPart, Multipart multipart) throws MessagingException, IOException {
        attachmentPart.attachFile(tmp);
        multipart.addBodyPart(attachmentPart);
    }

    private void addAttachmentByURL(File tmp, MimeBodyPart attachmentPart, Multipart multipart) throws MessagingException, IOException {
        URL urlo = tmp.toURI().toURL();

        
        attachmentPart.setDataHandler(new DataHandler(new SelfClosingURLDataSource(urlo, tmp.getTotalSpace())));
        attachmentPart.setDisposition(ATTACHMENT);
        attachmentPart.setFileName(tmp.getName());
        multipart.addBodyPart(attachmentPart);
    }

    private Session createMailSession() {
        try {

            properties.put("mail.smtp.host", "127.0.0.1");
            properties.put("mail.smtp.auth", "false");
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.socketFactory.port", 25);
            properties.put("mail.smtp.port", 25);
            properties.put("mail.smtp.user", "login-id");
            properties.put("mail.smtp.password", "password");
            session = Session.getInstance(properties, null);
            session.setDebug(false);

        } catch (Exception e) {

            System.out.println("exception" + e.getMessage());
        }
        return Session.getInstance(properties);

    }

}
