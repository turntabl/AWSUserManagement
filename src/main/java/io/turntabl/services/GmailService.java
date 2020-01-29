package io.turntabl.services;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.ImmutableList;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

public class GmailService {
    private static final String APPLICATION_NAME = "Gmail API ";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = ImmutableList.of(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_COMPOSE);
    private static final String CREDENTIALS_FILE_PATH = "credentials/google.json";


    public static void sendMail(String to, String subject, String bodyText) throws IOException, GeneralSecurityException {
        Gmail service = getGmail();
        String user = "me";
        try {
            service.users().messages().send(user,
                    createMessageWithEmail(createEmail(to, user, subject, bodyText))
            ).execute();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Gmail getGmail() throws GeneralSecurityException, IOException {
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential gcFromJson = GoogleCredential
                .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(SCOPES);

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(gcFromJson.getTransport())
                .setJsonFactory(gcFromJson.getJsonFactory())
                .setServiceAccountId(gcFromJson.getServiceAccountId())
                .setServiceAccountUser(System.getenv("GSUITE_ADMIN_EMAIL"))
                .setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
                .setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
                .setTokenServerEncodedUrl(gcFromJson.getTokenServerEncodedUrl())
                .build();
        ;

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private static MimeMessage createEmail(String to,
                                           String from,
                                           String subject,
                                           String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText, "UTF-8", "html");
        return email;
    }
}
