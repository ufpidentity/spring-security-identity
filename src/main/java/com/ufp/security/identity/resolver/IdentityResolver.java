package com.ufp.security.identity.resolver;

import java.net.URI;

public interface IdentityResolver {
    public URI getNext();
}