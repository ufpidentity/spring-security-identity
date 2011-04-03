package com.ufp.security.identity.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ufp.security.identity.core.DisplayItem;
import com.ufp.security.identity.authentication.IdentityAuthenticationToken;

public class MockIdentityConsumer implements IdentityConsumer {
    public MockIdentityConsumer() throws IdentityConsumerException {
    }

    public List<DisplayItem> beginConsumption(HttpServletRequest request, String username) throws IdentityConsumerException {
        List<DisplayItem> displayItems = new ArrayList<DisplayItem>();
        displayItems.add(new DisplayItem("Password", "<input type=\"text\" name=\"password\" id=\"AuthParam0\">", "Default Password"));
        displayItems.add(new DisplayItem("Secret", "<input type=\"text\" name=\"secret\" id=\"AuthParam1\">", "SAW w/sms"));
        return displayItems;
    }

    public Object continueConsumption(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityConsumerException {
        return new IdentityAuthenticationToken(username);
    }
}