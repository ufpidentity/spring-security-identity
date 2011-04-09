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

public class Identity4JService implements IdentityService {
    private IdentityServiceProvider identityServiceProvider;

    public List<DisplayItem> beginService(HttpServletRequest request, String username) throws IdentityServiceException {
        return identityServiceProvider.preAuthenticate(username, request.getRemoteHost());
    }

    public Object continueService(HttpServletRequest request, String username, Map<String, String[]> responseMap) throws IdentityServiceException {
        Object object = identityServiceProvider.authenticate(username, request.getRemoteHost(), responseMap);
        
        if (object instanceof String)
            object = new IdentityAuthenticationToken((String)object);
        return object;
    }
    
    @Required
    public void setIdentityServiceProvider(IdentityServiceProvider identityServiceProvider) {
        this.identityServiceProvider = identityServiceProvider;
    }
}