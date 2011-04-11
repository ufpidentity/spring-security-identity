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

/**
 * The Identity service provider does a direct interaction with the
 * Identity service. Data objects specific to the service are returned
 * in a generic way to allow for a wide variety of integrations.
 *
 * <p>
 * An example of a pretext (may contain multiple display items and subsequent calls may return any number of pretexts (n.b. the <code>form_element</code> values are escaped so that they can be directly injected into a page for rendering):
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_pretext&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;3&quot; message=&quot;OK&quot;&gt;SUCCESS&lt;/result&gt;
  &lt;display_item name=&quot;secret&quot;&gt;
    &lt;display_name&gt;Enter Secret&lt;/display_name&gt;
    &lt;form_element&gt;&amp;lt;input id=&amp;quot;AuthParam0&amp;quot; type=&amp;quot;text&amp;quot; name=&amp;quot;secret&amp;quot; /&amp;gt;&lt;/form_element&gt;
    &lt;nickname&gt;saw (w/AIM)&lt;/nickname&gt;
  &lt;/display_item&gt;
  &lt;display_item name=&quot;passphrase&quot;&gt;
    &lt;display_name&gt;Enter Passphrase&lt;/display_name&gt;
    &lt;form_element&gt;&amp;lt;input id=&amp;quot;AuthParam1&amp;quot; type=&amp;quot;text&amp;quot; name=&amp;quot;passphrase&amp;quot; /&amp;gt;&lt;/form_element&gt;
    &lt;nickname&gt;Default Password&lt;/nickname&gt;
  &lt;/display_item&gt;
&lt;/authentication_pretext&gt;
 * </pre>
 * <p>
 * An example of a successful authentication:
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_context&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;1&quot; message=&quot;OK&quot;&gt;SUCCESS&lt;/result&gt;
  &lt;name&gt;alice@example.com&lt;/name&gt;
&lt;/authentication_context&gt;
 * </pre>
 * An example of a successful authentication that requires a continuation:
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_pretext&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;0&quot; message=&quot;Further authentication is required&quot;&gt;CONTINUE&lt;/result&gt;
  &lt;display_item name=&quot;secret&quot;&gt;
    &lt;display_name&gt;Enter Secret&lt;/display_name&gt;
    &lt;form_element&gt;&lt;input id=&quot;AuthParam0&quot; type=&quot;text&quot; name=&quot;secret&quot; /&gt;&lt;/form_element&gt;
    &lt;nickname&gt;saw (w/AIM)&lt;/nickname&gt;
  &lt;/display_item&gt;
&lt;/authentication_pretext&gt;
 * </pre>
 * An example of a pre-authentication failure:
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_pretext&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;0&quot; message=&quot;User not found&quot;&gt;FAILURE&lt;/result&gt;
&lt;/authentication_pretext&gt;
 * </pre>
 * An example of an authentication failure:
 * <p>
 * <pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; standalone=&quot;yes&quot;?&gt;
&lt;authentication_context&gt;
  &lt;result quality=&quot;0.0&quot; level=&quot;0&quot; message=&quot;Authentication failed&quot;&gt;FAILURE&lt;/result&gt;
  &lt;name&gt;alice@example.com&lt;/name&gt;
&lt;/authentication_context&gt;
 * </pre>
 */
public class IdentityServiceProvider implements InitializingBean {
    private static Logger logger = Logger.getLogger(IdentityServiceProvider.class);

    private static Client client;
    private IdentityResolver identityResolver;
    private HostnameVerifier hostnameVerifier;
    private TrustManagerFactoryBuilder trustManagerFactoryBuilder;
    private KeyManagerFactoryBuilder keyManagerFactoryBuilder;

    /**
     * Handle default injection if nothing was explicitly set.
     * <p>
     * Meant to be used with Spring dependency injection. Will set a
     * default {@link IdentityResolver}, suitable for low-volume sites, and a default {@link HostnameVerifier} suitable for most integrations.
     * <p>
     * Methods are available to explicitly set (or have dependencies
     * injected) the {@link IdentityResolver} to one more suitable for
     * testing or for HA requirements, the {@link HostnameVerifier}
     * for testing or more lax requirements.
     * <p>
     * n. b. No {@link TrustManagerFactoryBuilder} or {@link  KeyManagerFactoryBuilder} are set by default. These objects are
     * specific to integrations and must be either explicitly
     * dependency injected or set using {@link #setTrustManagerFactoryBuilder(TrustManagerFactoryBuilder)} and
     * {@link #setKeyManagerFactoryBuilder(KeyManagerFactoryBuilder)}.
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

    public AuthenticationPretext preAuthenticate(String name, String host) throws IdentityServiceException {
        WebResource webResource = client.resource(identityResolver.getNext().resolve("preauthenticate"));
        MultivaluedMap queryParams = new MultivaluedMapImpl();
        queryParams.add("name", name);
        queryParams.add("client_ip", host);
        AuthenticationPretext authenticationPretext = webResource.queryParams(queryParams).get(AuthenticationPretext.class);
        logger.debug("got result of " + authenticationPretext.getResult().getValue() + ", with message " + authenticationPretext.getResult().getMessage());
        return authenticationPretext;
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
                r = handleClientResponse(clientResponse);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new IdentityServiceException(e.getMessage(), e);
            }
        } else
            logger.error("got response of " + clientResponse.getClientResponseStatus());
        return r;
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
    
    