package com.ufp.security.identity.authentication;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.ufp.security.identity.core.DisplayItem;

public class IdentityAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private List<DisplayItem> displayItems;

    public IdentityAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        displayItems = new ArrayList<DisplayItem>();
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

    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
    
    public void addDisplayItem(DisplayItem displayItem) {
        displayItems.add(displayItem);
    }
     
    public void clearDisplayItems() {
        displayItems.clear();
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }
}