package com.ufp.security.identity.authentication;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class IdentityAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private Map<String, String> attributes;

    public IdentityAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        attributes = new HashMap<String, String>();
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal
     * @param authorities
     */
    public IdentityAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
    }


    //~ Methods ========================================================================================================

    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
    
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}