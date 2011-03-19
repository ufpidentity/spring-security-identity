package com.ufp.security.authentication;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class IdentityAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private List<Object> credentials;

    public IdentityAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        credentials = new ArrayList<Object>();
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
     * implementations that are satisfied with producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal
     * @param credentials
     * @param authorities
     */
    public IdentityAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); // must use super, as we override
    }


    //~ Methods ========================================================================================================

    public List<Object> getCredentials() {
        return this.credentials;
    }

    public void addCredential(Object credential) {
        credentials.add(credential);
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials.clear();
        credentials = null;
    }



}