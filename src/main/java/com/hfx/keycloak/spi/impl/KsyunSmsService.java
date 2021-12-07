package com.hfx.keycloak.spi.impl;

import com.hfx.keycloak.SmsException;
import com.hfx.keycloak.VerificationCodeRepresentation;
import com.hfx.keycloak.spi.SmsService;
import okhttp3.Response;
import org.keycloak.models.KeycloakSession;

import java.io.IOException;
import java.util.*;

public class KsyunSmsService implements SmsService<Object> {
    private final KeycloakSession session;

    String ak = "";
    String sk = "";
    String signName = "";
    String ExtId = "dadsdasd";
    //开启了权限，才能使用下面这两个参数
    String content = "";
    String smsType = "";

    public KsyunSmsService(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean send(String phoneNumber, Map<String, ? super Object> params) throws SmsException {
        return true;
    }

    @Override
    public boolean sendVerificationCode(VerificationCodeRepresentation rep, Map<String, ? super Object> params) throws SmsException {
        String templateId = System.getProperty("template_id");
        List<String> extraData = new ArrayList<>();
        extraData.add(rep.getCode());
        params.put("datas", extraData);

        Map<String, String> tplParams = Collections.singletonMap("code", rep.getCode());
        System.out.printf("============= DEBUG ============: %s", rep.getCode());
        return true;
    }
}
