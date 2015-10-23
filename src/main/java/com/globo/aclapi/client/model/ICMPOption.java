package com.globo.aclapi.client.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class ICMPOption extends GenericJson{

    @Key("icmp-type")
    private Integer type;

    @Key("icmp-code")
    private Integer code;

    public ICMPOption(Integer type, Integer code) {
        this.type = type;
        this.code = code;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
