package com.ufp.security.identity.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;
import com.ufp.security.identity.provider.IdentityServiceProvider;
import com.ufp.security.identity.provider.IdentityServiceProvider;

import com.ufp.security.identity.provider.data.AuthenticationContext;
import com.ufp.security.identity.provider.data.AuthenticationPretext;
import com.ufp.security.identity.provider.data.DisplayItem;
import com.ufp.security.identity.provider.data.Result;

import org.apache.log4j.Logger;

/**
 * An example of a failure:
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_pretext&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;0&quot; message=&quot;User not found&quot;&gt;FAILURE&lt;/result&gt;
&lt;/authentication_pretext&gt;
 * </pre>
 * <p>
 * An example of a pretext (may contain multiple display items and subsequent calls may return any number of pretexts (n.b. the <code>form_element</code> values are escaped so that they can be directly injected into a page for rendering)
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_pretext&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;3&quot; message=&quot;OK&quot;&gt;SUCCESS&lt;/result&gt;
  &lt;display_item name=&quot;secret&quot;&gt;
    &lt;display_name&gt;Enter Secret&lt;/display_name&gt;
    &lt;form_element&gt;&amp;lt;input id=&amp;quot;AuthParam0&amp;quot; type=&amp;quot;text&amp;quot; name=&amp;quot;secret&amp;quot; /&amp;gt;&lt;/form_element&gt;
    &lt;nickname&gt;saw (w/AIM)&lt;/nickname&gt;
  &lt;/display_item&gt;
  &lt;display_item name=&quot;passphrase&quot;&gt;
    &lt;display_name&gt;Enter Passphrase&lt;/display_name&gt;
    &lt;form_element&gt;&amp;lt;input id=&amp;quot;AuthParam1&amp;quot; type=&amp;quot;text&amp;quot; name=&amp;quot;passphrase&amp;quot; /&amp;gt;&lt;/form_element&gt;
    &lt;nickname&gt;Default Password&lt;/nickname&gt;
  &lt;/display_item&gt;
&lt;/authentication_pretext&gt;
 * </pre>
 * <p>
 * An example of a successful authentication
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_context&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;1&quot; message=&quot;OK&quot;&gt;SUCCESS&lt;/result&gt;
  &lt;name&gt;richardl@ufp.com&lt;/name&gt;
&lt;/authentication_context&gt;
 * </pre>
 */

public class Identity4JServiceBridge implements IdentityServiceBridge {
    private static Logger logger = Logger.getLogger(Identity4JServiceBridge.class);
    public static final String IDENTITY_DISPLAY_MESSAGE = "IDENTITY_DISPLAY_MESSAGE";
    private IdentityServiceProvider identityServiceProvider;

    public List<DisplayItem> preAuthenticate(HttpServletRequest request, String username) throws IdentityServiceException {
        AuthenticationPretext authenticationPretext = identityServiceProvider.preAuthenticate(username, request.getRemoteHost());
        Result result = authenticationPretext.getResult();
        logger.debug("handling a pretext with " + authenticationPretext.getDisplayItem().size() + " elements, result of " + result.getValue() + " and message of " + result.getMessage());
        if (!result.getValue().equals("SUCCESS") && !result.getValue().equals("CONTINUE"))
            throw new IdentityServiceException(result.getMessage());
        return authenticationPretext.getDisplayItem();
    }

    public Object authenticate(HttpServletRequest request, String username, Map<String, String[]> responseMap) throws IdentityServiceException {
        Object r = null;
        
        // clear any display messages 
        request.getSession(false).removeAttribute(IDENTITY_DISPLAY_MESSAGE);

        Object response = identityServiceProvider.authenticate(username, request.getRemoteHost(), responseMap);
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
            throw new IdentityServiceException(result.getMessage());
        logger.debug("returning name " + authenticationContext.getName());
        return new IdentityAuthenticationToken(authenticationContext.getName());
    }
            
    private List<DisplayItem> handleAuthenticationPretext(AuthenticationPretext authenticationPretext) throws IdentityServiceException {
        Result result = authenticationPretext.getResult();
        logger.debug("handling a pretext with " + authenticationPretext.getDisplayItem().size() + " elements, result of " + result.getValue() + " and message of " + result.getMessage());
        if (!result.getValue().equals("SUCCESS") && !result.getValue().equals("CONTINUE"))
            throw new IdentityServiceException(result.getMessage());
        return authenticationPretext.getDisplayItem();
    }
    
    @Required
    public void setIdentityServiceProvider(IdentityServiceProvider identityServiceProvider) {
        this.identityServiceProvider = identityServiceProvider;
    }
}