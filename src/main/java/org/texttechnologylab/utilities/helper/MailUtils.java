package org.texttechnologylab.utilities.helper;


import org.texttechnologylab.utilities.helper.utils.mail.MailConnection;
import org.texttechnologylab.utilities.helper.utils.mail.TTMail;

import javax.mail.MessagingException;

/**
 * Created by gabrami on 08.08.19.
 */
public class MailUtils {

    public static TTMail createMessage(MailConnection mc, String sSubject) throws MessagingException {

        return new TTMail(mc, sSubject);

    }

}