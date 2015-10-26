package com.globo.aclapi.client.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.Objects;

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


    public Integer getType() {
        return (type != null ? Integer.valueOf(type) : null);
    }

    public void setType(Integer type) {
        this.type = type != null ? type.toString() : null;
    }

    public Integer getCode() {
        return (code != null ? Integer.valueOf(code) : null);
    }

    public void setCode(Integer code) {
        this.code = code != null ? code.toString() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ICMPOption that = (ICMPOption) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, code);
    }
}
