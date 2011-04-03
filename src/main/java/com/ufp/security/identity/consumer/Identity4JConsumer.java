package com.ufp.security.identity.consumer;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ufp.security.identity.core.DisplayItem;
import com.ufp.security.identity.authentication.IdentityAuthenticationToken;

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
 * An example of a pretext (may contain multiple display items and subsequent calls may return any number of pretexts
 * <p>
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;authentication_pretext&gt;
 *  &lt;result quality="0.0" level="3" message="OK"&gt;SUCCESS&lt;/result&gt;
 *  &lt;display_item name="secret"&gt;
 *    &lt;display_name&gt;Enter Secret&lt;/display_name&gt;
 *    &lt;form_element&gt;&lt;input id="AuthParam0"  type="text" name="secret" /&gt;&lt;/form_element&gt;
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

public class Identity4JConsumer implements IdentityConsumer {
    public Identity4JConsumer() throws IdentityConsumerException {
    }

    public List<DisplayItem> beginConsumption(HttpServletRequest request, String username) throws IdentityConsumerException {
        return null;
    }

    public Object continueConsumption(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityConsumerException {
        return new IdentityAuthenticationToken(username);
    }
}