package com.ufp.security.identity.service;

import java.util.List;
import java.util.Map;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;
import com.ufp.security.identity.provider.data.DisplayItem;

import javax.servlet.http.HttpServletRequest;

/**
 * Methods to handle an Identity authentication. Initially a context
 * is setup for a user id which is fully qualified along with the
 * domain requesting authentication on behalf of the user.  
 * <p> 
 * This is performed by the {@link #beginService(HttpServletRequest, String)} which requires the username passed in and the incoming
 * request. If the username, fully qualified by the domain, is found,
 * a list of {@link DisplayItem} is passed back for the user to fill
 * in to satisfy the authentication requirements. Otherwise an
 * exception is thrown.  
 * <p> 
 * After the user fills in the responses to
 * the proffered {@link DisplayItem}s the keys and values are placed
 * in a {@link Map} and {@link #continueService(HttpServletRequest, String, Map)} is called.
 * <p> 
 * The object returned by this method may be
 * EITHER another list of {@link DisplayItem} OR an {@link IdentityAuthenticationToken} indicating a successful authentication
 * occured.
 */
public interface IdentityService {
    public List<DisplayItem> beginService(HttpServletRequest request, String username) throws IdentityServiceException;
    public Object continueService(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityServiceException;
}