package com.ufp.security.identity.authentication;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import com.ufp.security.identity.core.ContinueAuthenticationException;
import com.ufp.security.identity.core.DisplayItem;

public class IdentityAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    private UserDetailsService userDetailsService;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "The userDetailsService must be set");
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication newAuthentication = null;
        if (authentication instanceof IdentityAuthenticationToken) {
            IdentityAuthenticationToken token = (IdentityAuthenticationToken)authentication;
            UserDetails userDetails = userDetailsService.loadUserByUsername(token.getPrincipal().toString());
            newAuthentication = createSuccessfulAuthentication(userDetails, token);
        }
        return newAuthentication;
    }

    protected Authentication createSuccessfulAuthentication(UserDetails userDetails, IdentityAuthenticationToken authentication) {
        return new IdentityAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    public boolean supports(Class<? extends Object> authentication) {
        return (IdentityAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}    
