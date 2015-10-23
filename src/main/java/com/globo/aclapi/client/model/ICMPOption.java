package com.globo.aclapi.client.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class ICMPOption extends GenericJson{

    @Key("icmp-type")
    private String type;

    @Key("icmp-code")
    private String code;

    public ICMPOption() {

    }

    public ICMPOption(Integer type, Integer code) {
        this.type = type.toString();
        this.code = code.toString();
    }

//
//    public Integer getType() {
//        return type;
//    }
//
//    public void setType(Integer type) {
//        this.type = type;
//    }
//
//    public Integer getCode() {
//        return code;
//    }
//
//    public void setCode(Integer code) {
//        this.code = code;
//    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
