package com.hfx.keycloak.spi.impl;

import com.hfx.keycloak.spi.SmsService;
import com.hfx.keycloak.spi.SmsServiceProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class KsyunSmsServiceProviderFactory implements SmsServiceProviderFactory {
    @Override
    public SmsService create(KeycloakSession session) {
        return new KsyunSmsService(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return "KsyunServiceProviderFactory";
    }
}
