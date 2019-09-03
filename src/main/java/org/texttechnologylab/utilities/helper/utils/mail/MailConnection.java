package org.texttechnologylab.utilities.helper.utils.mail;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class MailConnection {

    private String sAdress="";
    private String sUser="";
    private String sPass="";

    private String sPop = "";
    private String sImap = "";
    private String sSmtp = "";

    private int iPopPort = 995;
    private int iSmtpPort = 587;
    private int iImapPort= 993;

    final Properties props = new Properties();

    private Session session = null;
    private Store store = null;
    private Folder folder = null;

    public InternetAddress getAdress() throws AddressException {
        return new InternetAddress(sAdress);
    }

    private void log(String pString){

        //.log("["+sAdress+"] "+pString);
        System.out.println("["+sAdress+"]\t"+pString);

    }

    public MailConnection() throws MessagingException {
        init();
    }

    private void init() throws MessagingException{

        // GetMails
        if(sImap.length()>0){
            props.setProperty("mail.imap.host", sImap);
            props.setProperty("mail.imap.port", String.valueOf(iImapPort));
            props.setProperty("mail.imap.connectiontimeout", "10000");
        }
        else {
            props.setProperty("mail.pop3.host", sPop);
            props.setProperty("mail.pop3.port", String.valueOf(iPopPort));
            props.setProperty("mail.pop3.starttls.enable", "true");
        }


        // SendMails
        props.setProperty( "mail.smtp.host", sSmtp );
        props.setProperty( "mail.smtp.auth", "true" );
        props.setProperty("mail.smtp.ssl.enable", "true");
        props.setProperty( "mail.smtp.timeout", "10000");
        props.setProperty( "mail.smtp.port", String.valueOf(iSmtpPort) );

        props.setProperty("mail.smtp.connectiontimeout", "10000");

        props.setProperty("mail.smtp.debug", "true");

        //props.list(System.out);

//		this.session = Session.getDefaultInstance(props);

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sUser, sPass);
            }
        });

        this.store = this.session.getStore(sImap.length()>0 ? "imaps" : "pop3s" );
        store.connect(sImap.length()>0 ? sImap : sPop, sUser, sPass);

    }

    public void removeMessages(Set<Message> pMessageSet) throws MessagingException{

        for(Message m : pMessageSet){
            m.setFlag(Flags.Flag.DELETED, true);
            log("Remove: "+m.getSubject());
        }

    }

    public void removeMessage(Message pMessage) throws MessagingException{

        pMessage.setFlag(Flags.Flag.DELETED, true);
        log("Remove: "+pMessage.getSubject());
    }


    public Set<Message> getMessages(boolean pWrite) throws MessagingException{

        Set<Message> rSet = new HashSet<Message>();

        this.folder = this.store.getFolder( new URLName("INBOX") );
        this.folder.open( pWrite ? Folder.READ_WRITE : Folder.READ_ONLY );

        for ( Message m : this.folder.getMessages() )
        {
            rSet.add(m);
        }

        return rSet;

    }

    public Set<Message> getMessages() throws MessagingException{

        if(store == null || !store.isConnected()){
            this.init();
        }

        return getMessages(false);

    }

    public void close() throws MessagingException{

        this.closeFolder(true);
        this.store.close();

    }


    public void closeFolder(boolean pAll) throws MessagingException{
        if(this.folder!=null){
            this.folder.close(pAll);
        }
    }

    public void sendMessage(Message m) throws MessagingException{
        Transport.send(m);
    }

    public Session getSession(){
        return this.session;
    }

    public MailConnection(String sAdress, String sUser, String sPassword, String sImap, int iImapPort, String sPop, int iPopPort, String sSmtp, int iSmtpPort) throws Exception{

        this.sAdress = sAdress;
        this.sUser = sUser;
        this.sPass = sPassword;
        this.sPop = sPop;
        this.iPopPort = iPopPort;
        this.sImap= sImap;
        this.iImapPort= iImapPort;
        this.sSmtp = sSmtp;
        this.iSmtpPort = iSmtpPort;

        this.init();


    }


}