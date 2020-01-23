package io.turntabl.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Set;

public class EMail {
    private  JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("idawudinho@gmail.com");
        mailSender.setPassword(String.valueOf(System.getenv("mailpass")));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }

    public void send(String name, Set<String> roles) throws MessagingException {
        JavaMailSender javaMailSender = getJavaMailSender();

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo("dawud.ismail@turntabl.io");
        helper.setSubject("Request for AWS Role Permission");

        StringBuilder htmlText = new StringBuilder( name + ", is seeking permission to the following AWS role(s): <br>");
        htmlText.append("<ul>");
        roles.forEach( role -> htmlText.append("<li>").append(role).append("</li>"));
        htmlText.append("</ul> <br>"  +
                "<form action=\"/url/accept\">\n" +
                " <input type=\"submit\" value=\"Approve\">\n" +
                "</form>\n" +
                "<form action=\"/url/decline\">\n" +
                " <input type=\"submit\" value=\"Decline\">\n" +
                "</form>");

        helper.setText(htmlText.toString(), true);
        javaMailSender.send(msg);
    }
}
