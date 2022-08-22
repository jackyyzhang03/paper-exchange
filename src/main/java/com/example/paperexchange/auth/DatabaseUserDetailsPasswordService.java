package com.example.paperexchange.auth;

import com.example.paperexchange.user.User;
import com.example.paperexchange.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class DatabaseUserDetailsPasswordService implements UserDetailsPasswordService {
    private final UserRepository userRepository;

    @Autowired
    public DatabaseUserDetailsPasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = userRepository.findUserByEmail(userDetails.getUsername());
        user.setPassword(newPassword);
        return new BasicUserDetails(user);
    }
}
