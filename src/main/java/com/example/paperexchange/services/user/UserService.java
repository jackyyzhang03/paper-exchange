package com.example.paperexchange.services.user;

import com.example.paperexchange.dtos.RegistrationRequest;
import com.example.paperexchange.entities.User;
import com.example.paperexchange.exception.UserAlreadyExistsException;
import com.example.paperexchange.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void createUser(RegistrationRequest registrationRequest) {
        String email = registrationRequest.email();
        String rawPassword = registrationRequest.password();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        if (userRepository.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException();
        }
        User user = new User(email, encodedPassword);
        userRepository.save(user);
        applicationEventPublisher.publishEvent(new UserCreationEvent(user));
    }

    public User findUser(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void verifyUserEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user.getRole() == User.Role.USER_UNVERIFIED) {
            user.setRole(User.Role.USER_VERIFIED);
        }
    }
}
