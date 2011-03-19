package com.ufp.security.core;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class ContinueAuthenticationException extends AuthenticationException {
    public ContinueAuthenticationException(Authentication authentication, String message) {
        super(message);
        setAuthentication(authentication);
    }
}