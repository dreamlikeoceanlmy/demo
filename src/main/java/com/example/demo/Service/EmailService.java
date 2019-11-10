package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Component
public class EmailService {
    @Value("${mail.sender}")
    String sender;
    @Autowired
private JavaMailSender javaMailSender;
    @Async
    public void sendSimpleMail(String receiver,String subject,String context,String cc){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        if (cc!=null) {
            mailMessage.setCc(cc);
        }
        mailMessage.setSubject(subject);
        mailMessage.setText(context);
        mailMessage.setTo(receiver);
        System.out.println(Thread.currentThread().getName());
        javaMailSender.send(mailMessage);
    }
    public void sendAttachFileMail(String receiver, String subject, String context, String cc, File file){
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(context);
            if (cc != null) {
                helper.setCc(cc);
            }
            helper.addAttachment(file.getName(),file);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

}
