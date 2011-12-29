package com.ufp.security.identity.provisioning;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

public class IdentityUserDetailsManager implements UserDetailsManager {
    public void changePassword(String oldPassword, String newPassword) {
    }

    public void deleteUser(String username) {
    }

    public void createUser(UserDetails user) {
    }

    public void updateUser(UserDetails user) {
    }

    public boolean userExists(String username) {
        return false;
    }

    public UserDetails loadUserByUsername(String username) {
        return null;
    }
}