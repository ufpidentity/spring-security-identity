package com.ufp.security.identity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ufp.security.identity.core.DisplayItem;
import com.ufp.security.identity.authentication.IdentityAuthenticationToken;

public class MockIdentityService implements IdentityService {
    public MockIdentityService() throws IdentityServiceException {
    }

    public List<DisplayItem> beginService(HttpServletRequest request, String username) throws IdentityServiceException {
        List<DisplayItem> displayItems = new ArrayList<DisplayItem>();
        displayItems.add(new DisplayItem("Password", "<input type=\"text\" name=\"password\" id=\"AuthParam0\">", "Default Password"));
        displayItems.add(new DisplayItem("Secret", "<input type=\"text\" name=\"secret\" id=\"AuthParam1\">", "SAW w/sms"));
        return displayItems;
    }

    public Object continueService(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityServiceException {
        return new IdentityAuthenticationToken(username);
    }
}