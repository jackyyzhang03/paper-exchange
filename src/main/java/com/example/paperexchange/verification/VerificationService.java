package com.example.paperexchange.verification;

import com.example.paperexchange.exception.InvalidEmailVerificationToken;
import com.example.paperexchange.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    private final Map<String, UUID> emailTokenMap = new ConcurrentHashMap<>();
    private final UserService userService;
    private final SendGridEmailService emailService;

    @Autowired
    public VerificationService(UserService userService, SendGridEmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    public void verify(UUID verificationToken, String email) {
        if (emailTokenMap.containsKey(email) && emailTokenMap.get(email).equals(verificationToken)) {
            userService.verifyUserEmail(email);
        } else {
            throw new InvalidEmailVerificationToken();
        }
    }

    public void sendVerificationEmail(String email) {
        UUID token = UUID.randomUUID();
        emailTokenMap.put(email, token);
        emailService.sendVerificationEmail(email, token);
    }
}
