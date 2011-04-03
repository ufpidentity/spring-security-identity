package com.ufp.security.identity.resolver;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

public class  SimpleIdentityResolver implements IdentityResolver {
    private static Logger logger = Logger.getLogger(SimpleIdentityResolver.class);

    public URL getNext() {
        URL url = null;
        try  {
            url = new URL("https://identity.ufp.com:8443/identity-services");
        } catch (MalformedURLException mue) {
            logger.error(mue.getMessage(), mue);
        }
        return url;
    }
}