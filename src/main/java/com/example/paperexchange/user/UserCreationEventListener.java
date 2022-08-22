package com.example.paperexchange.user;

import com.example.paperexchange.verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserCreationEventListener {
    private final VerificationService verificationService;

    @Autowired
    public UserCreationEventListener(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Async
    @EventListener
    public void handleUserCreationEvent(UserCreationEvent event) {
        User user = event.getUser();
        this.verificationService.sendVerificationEmail(user.getEmail());
    }
}
