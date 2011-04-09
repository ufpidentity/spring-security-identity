package com.ufp.security.identity.provider;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import com.ufp.security.identity.provider.data.AuthenticationPretext;
import com.ufp.security.identity.provider.data.AuthenticationContext;
import com.ufp.security.identity.provider.data.DisplayItem;
import com.ufp.security.identity.provider.data.Result;

import com.ufp.security.identity.resolver.IdentityResolver;
import com.ufp.security.identity.resolver.StaticIdentityResolver;

import com.ufp.security.identity.service.IdentityServiceException;

import com.ufp.security.identity.truststore.IdentityHostnameVerifier;
import com.ufp.security.identity.truststore.KeyManagerFactoryBuilder;
import com.ufp.security.identity.truststore.TrustManagerFactoryBuilder;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.InitializingBean;

import org.apache.log4j.Logger;

public class IdentityServiceProvider implements InitializingBean {
    private static Logger logger = Logger.getLogger(IdentityServiceProvider.class);

    private static Client client;
    private IdentityResolver identityResolver;
    private HostnameVerifier hostnameVerifier;
    private TrustManagerFactoryBuilder trustManagerFactoryBuilder;
    private KeyManagerFactoryBuilder keyManagerFactoryBuilder;

    /**
     * Handle default injection if nothing was explicitly set
     * 
     * @pad.exclude
     */
    public void afterPropertiesSet() {
        if (identityResolver == null)
            identityResolver = new StaticIdentityResolver();
        if (hostnameVerifier == null) 
            hostnameVerifier = new IdentityHostnameVerifier();
        try {
            ClientConfig clientConfig = new DefaultClientConfig();
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(keyManagerFactoryBuilder.getKeyManagerFactory().getKeyManagers(), trustManagerFactoryBuilder.getTrustManagerFactory().getTrustManagers(), null);
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hostnameVerifier, sslContext));
            client = Client.create(clientConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public List<DisplayItem> preAuthenticate(String name, String host) throws IdentityServiceException {
        WebResource webResource = client.resource(identityResolver.getNext().resolve("preauthenticate"));
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("name", name);
        queryParams.add("client_ip", host);
        AuthenticationPretext authenticationPretext = webResource.queryParams(queryParams).get(AuthenticationPretext.class);
        logger.debug("got result of " + authenticationPretext.getResult().getValue() + ", with message " + authenticationPretext.getResult().getMessage());
        return handleAuthenticationPretext(authenticationPretext);
    }

    public Object authenticate(String name, String host, Map<String, String[]> responseParams) throws IdentityServiceException {
        Object r = null;
        WebResource webResource = client.resource(identityResolver.getNext().resolve("authenticate"));
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("name", name);
        queryParams.add("client_ip", host);
        for (String key : responseParams.keySet()) {
            String [] values = responseParams.get(key);
            for (String value : values) {
                if (!key.equals("submit")) {
                    logger.debug("adding key " + key + ", with value " + value);
                    queryParams.add(key, value);
                }
            }
        }
        ClientResponse clientResponse = webResource.queryParams(queryParams).get(ClientResponse.class);
        if (clientResponse.getClientResponseStatus().equals(ClientResponse.Status.OK)) {
            try {
                Object response = handleClientResponse(clientResponse);
                if (response instanceof AuthenticationPretext)
                    r = handleAuthenticationPretext((AuthenticationPretext)response);
                else if (response instanceof AuthenticationContext)
                    r = handleAuthenticationContext((AuthenticationContext)response);
                else
                    logger.error("unknown response object: " + response.toString());
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new IdentityServiceException(e.getMessage(), e);
            }
        } else
            logger.error("got response of " + clientResponse.getClientResponseStatus());
        return r;
    }

    private String handleAuthenticationContext(AuthenticationContext authenticationContext) throws IdentityServiceException {
        Result result = authenticationContext.getResult();
        logger.debug("handling a context with result " + result.getValue() + " and message " + result.getMessage());
        if (!result.getValue().equals("SUCCESS"))
            throw new IdentityServiceException(result.getMessage());
        logger.debug("returning name " + authenticationContext.getName());
        return authenticationContext.getName();
    }
            
    private List<DisplayItem> handleAuthenticationPretext(AuthenticationPretext authenticationPretext) throws IdentityServiceException {
        Result result = authenticationPretext.getResult();
        logger.debug("handling a pretext with " + authenticationPretext.getDisplayItem().size() + " elements, result of " + result.getValue() + " and message of " + result.getMessage());
        if (!result.getValue().equals("SUCCESS") && !result.getValue().equals("CONTINUE"))
            throw new IdentityServiceException(result.getMessage());
        return authenticationPretext.getDisplayItem();
    }
        
    private Object handleClientResponse(ClientResponse clientResponse) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(AuthenticationPretext.class, AuthenticationContext.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(clientResponse.getEntityInputStream());
    }
    
    public void setIdentityResolver(IdentityResolver identityResolver) {
        this.identityResolver = identityResolver;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    @Required
    public void setTrustManagerFactoryBuilder(TrustManagerFactoryBuilder trustManagerFactoryBuilder) {
        this.trustManagerFactoryBuilder = trustManagerFactoryBuilder;
    }

    @Required
    public void setKeyManagerFactoryBuilder(KeyManagerFactoryBuilder keyManagerFactoryBuilder) {
        this.keyManagerFactoryBuilder = keyManagerFactoryBuilder;
    }
}
    
    