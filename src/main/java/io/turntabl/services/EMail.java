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
        mailSender.setPassword("tuesday1996");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }

    public void send(String name, Set<String> roles, String requestId) throws MessagingException {
        JavaMailSender javaMailSender = getJavaMailSender();

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo("sam@turntabl.io");
        helper.setSubject("Request for AWS Role Permission");

        StringBuilder htmlText = new StringBuilder( " <p style=\"font-size: 16px;\">" + name + ", is seeking permission to the following AWS role(s): </p>");
        htmlText.append("<ul style=\"font-size: 16px;\">");
        roles.forEach( role -> htmlText.append("<li>").append(role).append("</li>"));
        htmlText.append("</ul> <br>" );
        htmlText.append(
                "<a href=\"hostname/v1/api/aws-mgnt/approve/" + requestId +"\" target=\"_self\" class=\"button\" style=\"background-color: #4CAF50;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 12px\">Approve</a>       \n" +
                        " \n" +
                        " <a href=\"hostname/v1/api/aws-mgnt/decline/"+ requestId +"\" target=\"_blank\" class=\"button button3\" style=\"background-color: #f44336;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 12px\">Decline</a> "
        );

        helper.setText(htmlText.toString(), true);
        javaMailSender.send(msg);
    }

    public void send(String userEmail, Boolean granted) throws MessagingException {
        JavaMailSender javaMailSender = getJavaMailSender();

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(userEmail);
        if (granted) {
            helper.setSubject("AWS Role Permission Request Granted");
            helper.setText(" <p style=\"font-size: 16px;\"> This permissions will last for only 20min. </p>", true);
        }else {
            helper.setSubject("AWS Role Permission Request Declined");
            helper.setText(" <p style=\"font-size: 16px;\"> Sorry, this request has being declined, contact the administrator sam@turntabl.io</p>", true);
        }
        javaMailSender.send(msg);
    }
}
