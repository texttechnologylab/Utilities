package org.texttechnologylab.utilities.helper.utils.mail;

import org.apache.commons.io.FileUtils;
import org.texttechnologylab.utilities.helper.TempFileHandler;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gabrami on 08.08.19.
 */
public class TTMail extends MimeMessage {

    MailConnection ml = null;

    Multipart multi = new MimeMultipart();

    public TTMail(MailConnection ml, String sSubject) throws MessagingException {
        this(ml.getSession());
        this.ml=ml;
        this.setFrom(ml.getAdress());
        this.setSubject(sSubject);
    }

    public TTMail(MailConnection ml) throws MessagingException {
        this(ml.getSession());
        this.ml=ml;
        super.setFrom(ml.getAdress());
    }

    public TTMail(MailConnection ml, Message m) throws MessagingException {
        this(ml.getSession());
        this.ml=ml;
        super.setFrom(ml.getAdress());

    }

    public TTMail(Session session) {
        super(session);
    }

    public TTMail(Session session, InputStream is) throws MessagingException {
        super(session, is);
    }

    public TTMail(MimeMessage source) throws MessagingException {
        super(source);
    }

    protected TTMail(Folder folder, int msgnum) {
        super(folder, msgnum);
    }

    protected TTMail(Folder folder, InputStream is, int msgnum) throws MessagingException {
        super(folder, is, msgnum);
    }

    protected TTMail(Folder folder, InternetHeaders headers, byte[] content, int msgnum) throws MessagingException {
        super(folder, headers, content, msgnum);
    }

    public void setSubject(String sSubject) throws MessagingException {
        super.setSubject(sSubject);
    }

    public void setContentHTML(String sValue) throws MessagingException {

        BodyPart htmlContent = new MimeBodyPart();
        htmlContent.setContent(sValue, "text/html; charset=utf-8");
        this.multi.addBodyPart(htmlContent);

    }

    public void sendMessage() throws MessagingException {
        this.setContent(multi);
        this.ml.sendMessage(this);
        //this.ml.closeFolder(true);
    }

    public void addAttachment(File pFile, String sName) throws MessagingException {
        BodyPart att = new MimeBodyPart();
        att.setDataHandler(new DataHandler(new FileDataSource(pFile)));
        att.setFileName(sName);
        this.multi.addBodyPart(att);
    }

    public void addRecipient(Message.RecipientType pType, Address pAdress) throws MessagingException {
        super.addRecipient(pType, pAdress);
    }

    public void setReplyTo(Address pAdress) throws MessagingException {
        super.setReplyTo(new Address[]{pAdress});
    }

    public static Set<File> getContentOfAttachments(Message m) throws Exception {

        Set<File> rFiles = new HashSet<>(0);

        Object content = m.getContent();

        if(content instanceof Multipart){

            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {

                    File tFile = TempFileHandler.getTempFileName(part.getFileName());

                    FileUtils.copyInputStreamToFile(part.getInputStream(), tFile);

                    rFiles.add(tFile);

                }
            }

        }

        return rFiles;

    }

}