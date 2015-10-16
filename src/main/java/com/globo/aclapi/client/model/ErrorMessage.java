package com.globo.aclapi.client.model;

import com.google.api.client.util.Key;

public class ErrorMessage {
    @Key("code")
    private Integer code;

    @Key("message")
    private String msg;

    public Integer getCode() { return code; }
    public String getMsg() { return msg; }
}
