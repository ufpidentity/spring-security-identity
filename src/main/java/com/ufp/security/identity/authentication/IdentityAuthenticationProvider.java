package com.ufp.security.identity.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;

import com.ufp.security.identity.core.ContinueAuthenticationException;
import com.ufp.security.identity.core.DisplayItem;

import org.apache.log4j.Logger;

public class IdentityAuthenticationProvider implements AuthenticationProvider {
    private static Logger logger = Logger.getLogger(IdentityAuthenticationProvider.class);

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("processing " + authentication);
        if (authentication instanceof IdentityAuthenticationToken) {
            ((IdentityAuthenticationToken)authentication).addDisplayItem(new DisplayItem("AuthParam0", "<input type=\"text\" name=\"password\">", "Default Password"));
        }
        throw new ContinueAuthenticationException(authentication, "Continue" /* messages from service */ );
    }

    public boolean supports(Class<? extends Object> authentication) {
        return (IdentityAuthenticationToken.class.isAssignableFrom(authentication));
    }
}    
