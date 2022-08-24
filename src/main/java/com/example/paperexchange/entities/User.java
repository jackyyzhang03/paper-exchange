package com.example.paperexchange.entities;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = Role.USER_UNVERIFIED;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVerified() {
        return !role.toString().contains("UNVERIFIED");
    }

    public enum Role {
        USER_UNVERIFIED,
        USER_VERIFIED,
        ADMIN
    }
}
