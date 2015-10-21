package com.globo.aclapi.client.model;

import com.google.api.client.util.Key;

public class ErrorMessage {
    @Key("code")
    private String code;

    @Key("message")
    private String msg;

    public String getCode() { return code; }
    public String getMsg() { return msg; }
}
