package com.example.paperexchange.user;

import org.springframework.context.ApplicationEvent;

public class UserCreationEvent extends ApplicationEvent {
    public UserCreationEvent(User user) {
        super(user);
    }

    public User getUser() {
        return (User) source;
    }
}
