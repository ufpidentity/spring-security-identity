package com.ufp.security.identity.userdetails;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.apache.log4j.Logger;

public class GuestUserDetailsService implements UserDetailsService {
    private static Logger logger = Logger.getLogger(GuestUserDetailsService.class);
    
    public UserDetails loadUserByUsername(String username) {
        logger.debug("loading UserDetails for " + username);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        String newRole = "ROLE_USER";
        if (username.startsWith("guest")) {
            String id = username.substring("guest".length());
            BigInteger idl = new BigInteger(id, 16);
            if (idl.mod(new BigInteger("2")).equals(BigInteger.ZERO))
                newRole = "ROLE_SUPERVISOR";
        }
        authorities.add(new GrantedAuthorityImpl(newRole));
        return new User(username, "", true, true, true, true, authorities);
    }
}
        
        
        