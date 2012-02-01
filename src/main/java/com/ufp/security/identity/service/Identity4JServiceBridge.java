package com.ufp.security.identity.service;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;
import com.ufp.security.identity.web.authentication.IdentityAuthenticationFilter;

import com.ufp.identity4j.data.AuthenticationContext;
import com.ufp.identity4j.data.AuthenticationPretext;
import com.ufp.identity4j.data.DisplayItem;
import com.ufp.identity4j.data.Result;

import com.ufp.identity4j.provider.IdentityServiceProvider;
import com.ufp.identity4j.service.IdentityServiceException;

import org.apache.log4j.Logger;

/**
 * The Bridge implementation betweeen Spring Security and Identity4j. 
 *
 * Identity4j talks directly to the ufpIdentity service and returns data
 * objects specifically related to the service. It is up to the Bridge
 * implementation to translate the data objects returned by the
 * service, into data objects necessary for a login implementation
 * such as Spring Security (or other).
 *
 */
public class Identity4JServiceBridge implements IdentityServiceBridge {
    private static Logger logger = Logger.getLogger(Identity4JServiceBridge.class);
    public static final String IDENTITY_DISPLAY_MESSAGE = "IDENTITY_DISPLAY_MESSAGE";
    private IdentityServiceProvider identityServiceProvider;

    public UserDisplay preAuthenticate(HttpServletRequest request, String username) throws IdentityServiceException {
        AuthenticationPretext authenticationPretext = identityServiceProvider.preAuthenticate(username, request.getRemoteHost());
        Result result = authenticationPretext.getResult();
        logger.debug("handling a pretext with " + authenticationPretext.getDisplayItem().size() + " elements, result of " + result.getValue() + " and message of " + result.getMessage());
        if (!result.getValue().equals("SUCCESS") && !result.getValue().equals("CONTINUE"))
            throw new IdentityServiceException(result);
        return new UserDisplay(authenticationPretext.getName(), authenticationPretext.getDisplayItem());
    }

    public Object authenticate(HttpServletRequest request, String username, Map<String, String[]> responseMap) throws IdentityServiceException {
        Object r = null;
        
        // clear any display messages 
        request.getSession(false).removeAttribute(IDENTITY_DISPLAY_MESSAGE);
        
        List<DisplayItem> displayItems = (List<DisplayItem>)request.getSession(false).getAttribute(IdentityAuthenticationFilter.IDENTITY_DISPLAY_ITEMS);
        Map<String, String []> filteredResponseMap = new HashMap<String, String[]>();
        for (DisplayItem displayItem : displayItems) {
            logger.debug("found display item named " + displayItem.getDisplayName() + " with input named " + displayItem.getName());
            if (responseMap.containsKey(displayItem.getName())) {
                filteredResponseMap.put(displayItem.getName(), responseMap.get(displayItem.getName()));
            }
        }

        Object response = identityServiceProvider.authenticate(username, request.getRemoteHost(), filteredResponseMap);
        if (response instanceof AuthenticationPretext) {
            AuthenticationPretext authenticationPretext = (AuthenticationPretext)response;
            r = handleAuthenticationPretext(authenticationPretext);
            if (authenticationPretext.getResult().getValue().equals("CONTINUE")) {
                request.getSession(false).setAttribute(IDENTITY_DISPLAY_MESSAGE, authenticationPretext.getResult().getMessage());
            }
        } else if (response instanceof AuthenticationContext)
            r = handleAuthenticationContext((AuthenticationContext)response);
        else
            logger.error("unknown response object: " + response.toString());

        return r;
    }

    private IdentityAuthenticationToken handleAuthenticationContext(AuthenticationContext authenticationContext) throws IdentityServiceException {
        Result result = authenticationContext.getResult();
        logger.debug("handling a context with result " + result.getValue() + " and message " + result.getMessage());
        if (!result.getValue().equals("SUCCESS"))
            throw new IdentityServiceException(result);
        logger.debug("returning name " + authenticationContext.getName());
        return new IdentityAuthenticationToken(authenticationContext.getName());
    }
            
    private UserDisplay handleAuthenticationPretext(AuthenticationPretext authenticationPretext) throws IdentityServiceException {
        Result result = authenticationPretext.getResult();
        logger.debug("handling a pretext with " + authenticationPretext.getDisplayItem().size() + " elements, result of " + result.getValue() + " and message of " + result.getMessage());
        if (!result.getValue().equals("SUCCESS") && !result.getValue().equals("CONTINUE"))
            throw new IdentityServiceException(result);
        return new UserDisplay(authenticationPretext.getName(), authenticationPretext.getDisplayItem());
    }
    
    @Required
    public void setIdentityServiceProvider(IdentityServiceProvider identityServiceProvider) {
        this.identityServiceProvider = identityServiceProvider;
    }
}