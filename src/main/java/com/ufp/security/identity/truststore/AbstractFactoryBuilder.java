package com.ufp.security.identity.truststore;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractFactoryBuilder {
    protected File store;
    protected String passphrase;

    @Required
    public void setStore(File store) {
        this.store = store;
    }

    @Required
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
