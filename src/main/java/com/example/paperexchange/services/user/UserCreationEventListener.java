package com.example.paperexchange.services.user;

import com.example.paperexchange.entities.User;
import com.example.paperexchange.services.email.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserCreationEventListener {
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public UserCreationEventListener(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Async
    @EventListener
    public void handleUserCreationEvent(UserCreationEvent event) {
        User user = event.getUser();
        this.emailVerificationService.sendVerificationEmail(user.getEmail());
    }
}
