package com.ufp.security.identity.resolver;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

/**
 * An {@link IdentityResolver} that does no load balancing. Suitable for low-volume authentication requirements
 */
public class  SimpleIdentityResolver implements IdentityResolver {
    private static Logger logger = Logger.getLogger(SimpleIdentityResolver.class);

    public URI getNext() {
        URI uri = null;
        try  {
            uri = new URI("https://identity.ufp.com:8443/identity-services/");
        } catch (URISyntaxException use) {
            logger.error(use.getMessage(), use);
        }
        return uri;
    }
}