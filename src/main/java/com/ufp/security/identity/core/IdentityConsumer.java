package com.ufp.security.identity.core;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface IdentityConsumer {
    public List<DisplayItem> beginConsumption(HttpServletRequest request, String username) throws IdentityConsumerException;
    public Object continueConsumption(HttpServletRequest request, String username, Map<String, String> responseMap) throws IdentityConsumerException;
}