package io.turntabl.services;


import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Set;

public class EMail {

    public static JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("idawudinho@gmail.com");
        mailSender.setPassword( "tuesday1996");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    public static void requestMessage(String userEmail, Set<String> roles, String requestId) throws MessagingException {

        String subject = "Request for AWS Role Permission";
        StringBuilder body = new StringBuilder( " <p style=\"font-size: 16px;\">" + userEmail + ", is seeking permission to the following AWS role(s): </p>");
        body.append("<ul style=\"font-size: 16px;\">");
        roles.forEach( role -> body.append("<li>").append(role).append("</li>"));
        body.append("</ul> <br>" );
        body.append("<a href=\"http://accountpermission-env.nbhdf7zwaf.eu-west-2.elasticbeanstalk.com/v1/api/aws-mgnt/approve/")
                .append(requestId).append("\" target=\"_self\" class=\"button\" style=\"background-color: #4CAF50;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 12px\">Approve</a>       \n").append(" \n")
                .append(" <a href=\"http://accountpermission-env.nbhdf7zwaf.eu-west-2.elasticbeanstalk.com/v1/api/aws-mgnt/decline/").append(requestId).append("\" target=\"_blank\" class=\"button button3\" style=\"background-color: #f44336;border: none;color: white;padding: 10px 22px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 12px\">Decline</a> ");

        MimeMessage message = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("ismaildawud96@gmail.com");
        helper.setSubject(subject);
        helper.setText(body.toString(), true);
        getJavaMailSender().send(helper.getMimeMessage());
    }

    public static void feedbackMessage(String userEmail, Boolean granted) throws MessagingException {
        String subject, body;
        if (granted) {
            subject = "AWS Role Permission Request Granted";
            body = " <p style=\"font-size: 16px;\"> This permissions will last for only 20min. </p>" ;
        }else {
            subject = "AWS Role Permission Request Declined";
            body = " <p style=\"font-size: 16px;\"> Sorry, this request has being declined, contact the administrator sam@turntabl.io</p>";
        }
        MimeMessage message = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo( userEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        getJavaMailSender().send(helper.getMimeMessage());
    }

}
