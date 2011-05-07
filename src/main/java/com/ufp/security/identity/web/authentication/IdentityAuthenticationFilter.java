package com.ufp.security.identity.web.authentication;

import java.util.List;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.Assert;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;

import com.ufp.security.identity.service.IdentityServiceBridge;
import com.ufp.security.identity.service.MockIdentityServiceBridge;
import com.ufp.security.identity.service.UserDisplay;

import com.ufp.identity4j.data.DisplayItem;
import com.ufp.identity4j.service.IdentityServiceException;

public class IdentityAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "j_username";
    public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";
    public static final String IDENTITY_DISPLAY_ITEMS = "IDENTITY_DISPLAY_ITEMS";

    private boolean postOnly = true;
    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
    private IdentityServiceBridge identityServiceBridge;
    private String furtherAuthenticationUrl;

    public IdentityAuthenticationFilter() {
        super("/j_spring_security_check");
    }

    /** 
     * @pad.exclude
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (identityServiceBridge == null) {
            try {
                identityServiceBridge = new MockIdentityServiceBridge();
            } catch (IdentityServiceException ice) {
                throw new IllegalArgumentException("Failed to initialize Identity", ice);
            }
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        Authentication authentication = null;
        // Place the last username attempted into HttpSession for views
        HttpSession session = request.getSession(false);

        if (session != null || getAllowSessionCreation()) {
            if (session == null)
                session = request.getSession();
            
            if (username == null) {
                Object attribute = session.getAttribute(SPRING_SECURITY_LAST_USERNAME_KEY);
                if (attribute != null) 
                    username = attribute.toString(); 
            } 

            if (session.getAttribute(IDENTITY_DISPLAY_ITEMS) == null) {
                try {
                    UserDisplay userDisplay = identityServiceBridge.preAuthenticate(request, username);
                    session.setAttribute(IDENTITY_DISPLAY_ITEMS, userDisplay.getDisplayItems());
                    session.setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, userDisplay.getUsername());
                    response.sendRedirect(furtherAuthenticationUrl); 
                    return null;    // indicated to parent that authentication is continuing
                } catch (IdentityServiceException ice) {
                    throw new AuthenticationServiceException(ice.getMessage());
                }
            } else {
                try {
                    Object object = identityServiceBridge.authenticate(request, username, request.getParameterMap());
                    if (object instanceof IdentityAuthenticationToken) {
                        session.removeAttribute(IDENTITY_DISPLAY_ITEMS);
                        IdentityAuthenticationToken token = (IdentityAuthenticationToken)object;
                        // Allow subclasses to set the "details" property
                        setDetails(request, token);
                        authentication = this.getAuthenticationManager().authenticate(token);
                    } else if (object instanceof UserDisplay) {
                        UserDisplay userDisplay = (UserDisplay)object;
                        session.setAttribute(IDENTITY_DISPLAY_ITEMS, userDisplay.getDisplayItems());
                        session.setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, userDisplay.getUsername());
                        response.sendRedirect(furtherAuthenticationUrl);
                        return null;
                    }
                } catch (IdentityServiceException ice) {
                    throw new AuthenticationServiceException(ice.getMessage());
                }
            }
        } else
            throw new SessionAuthenticationException("no session");
        return authentication;
    }

    protected void setDetails(HttpServletRequest request, IdentityAuthenticationToken token) {
        token.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    protected String obtainUsername(HttpServletRequest request) {
        String name = request.getParameter(usernameParameter);
        if (name != null) 
            name = name.trim();
        return name;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setFurtherAuthenticationUrl(String furtherAuthenticationUrl) {
        this.furtherAuthenticationUrl = furtherAuthenticationUrl;
    }

    public void setIdentityServiceBridge(IdentityServiceBridge identityServiceBridge) {
        this.identityServiceBridge = identityServiceBridge;
    }
}