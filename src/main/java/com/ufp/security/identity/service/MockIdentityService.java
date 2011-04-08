package com.ufp.security.identity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ufp.security.identity.authentication.IdentityAuthenticationToken;
import com.ufp.security.identity.provider.data.DisplayItem;

public class MockIdentityService implements IdentityService {
    public MockIdentityService() throws IdentityServiceException {
    }

    public List<DisplayItem> beginService(HttpServletRequest request, String username) throws IdentityServiceException {
        List<DisplayItem> displayItems = new ArrayList<DisplayItem>();
        displayItems.add(createDisplayItem("Password", "<input type=\"text\" name=\"password\" id=\"AuthParam0\">", "Default Password"));
        displayItems.add(createDisplayItem("Secret", "<input type=\"text\" name=\"secret\" id=\"AuthParam1\">", "SAW w/sms"));
        return displayItems;
    }

    public Object continueService(HttpServletRequest request, String username, Map<String, String[]> responseMap) throws IdentityServiceException {
        return new IdentityAuthenticationToken(username);
    }

    private DisplayItem createDisplayItem(String name, String formElement, String nickname) {
        DisplayItem displayItem = new DisplayItem();
        displayItem.setNickname(nickname);
        displayItem.setDisplayName(name);
        displayItem.setName(name.toLowerCase());
        displayItem.setFormElement(formElement);
        return displayItem;
    }
}