package com.tigerit.soa.serviceImpl.email;

import com.tigerit.soa.entity.FailedEmailEntity;
import com.tigerit.soa.repository.FailedEmailRepository;
import com.tigerit.soa.service.email.EmailService;
import com.tigerit.soa.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DIPU on 6/4/20
 */
@Service
@Log4j2
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FailedEmailRepository failedEmailRepository;

    private final static String fromEmail="actiontracker7@gmail.com";

    @Async("taskExecutor")
    @Override
    public void sendSimpleMail(String to, String subject, String body) {
        log.info("sending email...");
        SimpleMailMessage mailMessage = getSimpleMailMessage(Arrays.asList(to), Arrays.asList(), subject, body);
        sendMail(mailMessage);
        log.info("email sent...");
    }

    @Override
    public void sendSimpleMail(List<String> to, List<String> cc, String subject, String body) {
        SimpleMailMessage mailMessage = getSimpleMailMessage(to, cc, subject, body);
        sendMail(mailMessage);
    }


    private SimpleMailMessage getSimpleMailMessage(List<String> to, List<String> cc, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(Util.toArray(to));
        message.setCc(Util.toArray(cc));
        message.setSubject(subject);
        message.setText(body);

        return message;
    }

    private void sendMail(SimpleMailMessage mailMessage) {
        try {
            mailMessage.setFrom(fromEmail);
            mailSender.send(mailMessage);
        } catch (MailException ex) {
            saveFailedEmail(mailMessage, ex);
            log.error("email err:", ex.getMessage());
        }
    }


    private void saveFailedEmail(SimpleMailMessage mailMessage, Exception ex) {
        FailedEmailEntity failedEmail = new FailedEmailEntity(null, Arrays.asList(mailMessage.getTo()),
                Arrays.asList(mailMessage.getCc()), mailMessage.getSubject(),
                mailMessage.getText(), ex.getMessage());

        failedEmailRepository.save(failedEmail);

    }
}
