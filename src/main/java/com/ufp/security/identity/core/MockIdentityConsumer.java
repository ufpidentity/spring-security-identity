package com.ufp.security.identity.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;

public class MockIdentityConsumer implements IdentityConsumer {
    public MockIdentityConsumer() throws IdentityConsumerException {
    }

    public List<DisplayItem> beginConsumption(HttpServletRequest request, String username) throws IdentityConsumerException {
        List<DisplayItem> displayItems = new ArrayList<DisplayItem>();
        displayItems.add(new DisplayItem("Password", "<input type=\"text\" name=\"password\" id=\"AuthParam0\">", "Default Password"));
        return displayItems;
    }

    public Object continueConsumption(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityConsumerException {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        return new IdentityAuthenticationToken(username, grantedAuthorities);
    }
}