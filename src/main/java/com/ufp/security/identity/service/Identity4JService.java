package com.ufp.security.identity.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;
import com.ufp.security.identity.provider.IdentityServiceProvider;
import com.ufp.security.identity.provider.data.DisplayItem;

/**
 * An example of a failure:
 * <p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;authentication_pretext&gt;
 *  &lt;result quality="0.0" level="0" message="User not found"&gt;FAILURE&lt;/result&gt;
 * &lt;/authentication_pretext&gt;
 * </pre>
 * <p>
 * An example of a pretext (may contain multiple display items and subsequent calls may return any number of pretexts (n.b. the <code>form_element</code> values are escaped so that they can be directly injected into a page for rendering)
 * <p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;authentication_pretext&gt;
 *  &lt;result quality="0.0" level="3" message="OK"&gt;SUCCESS&lt;/result&gt;
 *  &lt;display_item name="secret"&gt;
 *    &lt;display_name&gt;Enter Secret&lt;/display_name&gt;
 *    &lt;form_element&gt;&amp;lt;input id="AuthParam0"  type="text" name="secret" /&amp;gt;&lt;/form_element&gt;
 *    &lt;nickname&gt;saw (w/AIM)&lt;/nickname&gt;
 *  &lt;/display_item&gt;
 * &lt;/authentication_pretext&gt;
 * </pre>
 * <p>
 * An example of a successful authentication
 * <p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;authentication_context&gt;
 *  &lt;result quality="0.0" level="1" message="OK"&gt;SUCCESS&lt;/result&gt;
 *  &lt;name&gt;alice@example.com&lt;/name&gt;
 * &lt;/authentication_context&gt;
 * </pre>
 */

public class Identity4JService implements IdentityService {
    private IdentityServiceProvider identityServiceProvider;

    public List<DisplayItem> beginService(HttpServletRequest request, String username) throws IdentityServiceException {
        return identityServiceProvider.preAuthenticate(username, request.getRemoteHost());
    }

    public Object continueService(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityServiceException {
        return new IdentityAuthenticationToken(username);
    }
    
    @Required
    public void setIdentityServiceProvider(IdentityServiceProvider identityServiceProvider) {
        this.identityServiceProvider = identityServiceProvider;
    }
}