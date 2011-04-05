package com.ufp.security.identity.provider;

import java.util.List;

import javax.net.ssl.SSLContext;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import com.ufp.security.identity.provider.data.AuthenticationPretext;
import com.ufp.security.identity.provider.data.DisplayItem;

import com.ufp.security.identity.resolver.IdentityResolver;
import com.ufp.security.identity.resolver.SimpleIdentityResolver;
import com.ufp.security.identity.service.IdentityServiceException;
import com.ufp.security.identity.truststore.IdentityHostnameVerifier;

public class IdentityServiceProvider {
    private static Client client;
    private IdentityResolver identityResolver;
    private HostnameVerifier hostnameVerifier;

    public void afterPropertiesSet() {
        if (identityResolver == null)
            identityResolver = new SimpleIdentityResolver();
        if (hostnameVerifier == null) 
            hostnameVerifier = new IdentityHostnameVerifier();
    }

    public IdentityServiceProvider() throws IdentityServiceException {
        try {
            ClientConfig clientConfig = new DefaultClientConfig();
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            //sslContext.init(trustStore.getKeyManagerFactory().getKeyManagers(), trustManager.getTrustManagers(), null);
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hostnameVerifier, sslContext));
            client = Client.create(clientConfig);
        } catch (Exception e) {
            throw new IdentityServiceException(e.getMessage(), e);
        }
    }

    public List<DisplayItem> preAuthenticate(String name, String host) {
        WebResource webResource = client.resource(identityResolver.getNext());
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("name", name);
        queryParams.add("client_ip", host);
        AuthenticationPretext authenticationPretext = webResource.queryParams(queryParams).get(AuthenticationPretext.class);
        return authenticationPretext.getDisplayItem();
    }
    
    public void setIdentityResolver(IdentityResolver identityResolver) {
        this.identityResolver = identityResolver;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }
}
    
    