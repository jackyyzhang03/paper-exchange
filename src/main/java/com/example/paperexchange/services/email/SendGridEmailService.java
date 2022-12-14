package com.example.paperexchange.services.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class SendGridEmailService {
    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);
    private final SendGrid sendGrid;
    @Value("${verification.endpoint}")
    private String verificationEndpoint;
    @Value("${verification.template.id")
    private String templateId;
    @Value("${verification.email")
    private String verificationEmail;

    @Autowired
    public SendGridEmailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    public void sendVerificationEmail(String recipientEmail, UUID token) {
        Email from = new Email(verificationEmail);
        Email to = new Email(recipientEmail);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("verificationUrl", verificationEndpoint + "?token=" + token);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject("PaperExchange | Verify email address");
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (IOException ex) {
            logger.error("Error sending verification message to: {}", recipientEmail);
        }
    }
}
