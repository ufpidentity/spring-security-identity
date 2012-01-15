spring-security-identity is a Spring Security implementation of an
Authentication provider and supporting classes for integration with
the ufpIdentity service. ufpIdentity provides strong, flexible, user
login for your website.

To get started, build the .jar using:

    mvn clean install

The sample application in the spring-tutorial subdirectory provides a complete login example packaged as a standard .war file. To 
build the .war, do:

    cd spring-tutorial
    mvn clean package

A typical securityContext.xml might look like:

    <?xml version="1.0" encoding="UTF-8"?>

    <!--
    - Sample namespace-based configuration
    -
    -->

    <beans:beans xmlns="http://www.springframework.org/schema/security"
                 xmlns:beans="http://www.springframework.org/schema/beans"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
                                     http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-3.1.xsd">

      <global-method-security pre-post-annotations="enabled">
      </global-method-security>

      <http use-expressions="true" entry-point-ref="defaultEntryPoint">
        <intercept-url pattern="/secure/**" access="isAuthenticated()" />
        <intercept-url pattern="/**" access="permitAll" />
        <custom-filter position="FORM_LOGIN_FILTER" ref="identityAuthenticationFilter" />
        <logout />
        <session-management session-authentication-strategy-ref="sessionAuthenticationStrategy"/>
      </http>

      <authentication-manager alias="authenticationManager">
        <authentication-provider ref="identityProvider"/>
      </authentication-manager>

      <beans:bean id="concurrencyFilter" class="org.springframework.security.web.session.ConcurrentSessionFilter">
        <beans:property name="sessionRegistry" ref="sessionRegistry" />
        <beans:property name="expiredUrl" value="/timeout.jsp" />
      </beans:bean>

      <beans:bean id="sessionAuthenticationStrategy" class="org.springframework.security.web.authentication.session.ConcurrentSessionControlStrategy">
        <beans:constructor-arg name="sessionRegistry" ref="sessionRegistry" />
        <beans:property name="maximumSessions" value="1" />
      </beans:bean>

      <beans:bean id="sessionRegistry" class="org.springframework.security.core.session.SessionRegistryImpl" />

      <beans:bean name="defaultFailureHandler" class="org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler">
        <beans:property name="defaultFailureUrl" value="/failure.jsp"/>
      </beans:bean>

      <beans:bean name="defaultEntryPoint" class="org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint">
        <beans:property name="loginFormUrl" value="/login.jsp"/>
      </beans:bean>

      <!-- Must provide your own UserDetailsService -->
      <beans:bean id="myUserDetailsService" class="com.myco.mypackage.MyUserDetailsService"/>

      <beans:bean name="identityProvider" class="com.ufp.security.identity.authentication.IdentityAuthenticationProvider">
        <beans:property name="userDetailsService" ref="myUserDetailsService"/>
      </beans:bean>

      <beans:bean name="identityAuthenticationFilter" class="com.ufp.security.identity.web.authentication.IdentityAuthenticationFilter">
        <beans:property name="authenticationManager" ref="authenticationManager"/>
        <beans:property name="authenticationFailureHandler" ref="defaultFailureHandler"/>
        <beans:property name="furtherAuthenticationUrl" value="login.jsp"/>
        <beans:property name="identityServiceBridge" ref="identityServiceBridge"/>
        <beans:property name="sessionAuthenticationStrategy" ref="sessionAuthenticationStrategy"/>
      </beans:bean>

      <beans:bean name="identityServiceBridge" class="com.ufp.security.identity.service.Identity4JServiceBridge">
        <beans:property name="identityServiceProvider" ref="identityServiceProvider"/>
      </beans:bean>

      <beans:bean name="keyManagerFactoryBuilder" class="com.ufp.identity4j.truststore.KeyManagerFactoryBuilder">
        <beans:property name="store" value="classpath:store/example.com.p12"/>
        <beans:property name="passphrase" value="${super_secret_certificate_password}"/>
      </beans:bean>

      <beans:bean name="trustManagerFactoryBuilder" class="com.ufp.identity4j.truststore.TrustManagerFactoryBuilder">
        <beans:property name="store" value="classpath:store/truststore.jks"/>
        <beans:property name="passphrase" value="${super_secret_truststore_password}"/>
      </beans:bean>

      <beans:bean id="identityServiceProvider" class="com.ufp.identity4j.provider.IdentityServiceProvider" init-method="afterPropertiesSet">
        <beans:property name="keyManagerFactoryBuilder" ref="keyManagerFactoryBuilder"/>
        <beans:property name="trustManagerFactoryBuilder" ref="trustManagerFactoryBuilder"/>
      </beans:bean>
    </beans:beans>
