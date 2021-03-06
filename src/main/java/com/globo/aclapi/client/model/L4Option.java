package com.globo.aclapi.client.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.Objects;

public class L4Option extends GenericJson{

    @Key("src-port-op")
    private String srcPortOperation;

    @Key("dest-port-op")
    private String destPortOperation;

    @Key("src-port-start")
    private String srcPortStart;

    @Key("dest-port-start")
    private String destPortStart;

    @Key("dest-port-end")
    private String destPortEnd;




    public String getSrcPortOperation() {
        return srcPortOperation;
    }

    public void setSrcPortOperation(String srcPortOperation) {
        this.srcPortOperation = srcPortOperation;
    }

    public String getDestPortOperation() {
        return destPortOperation;
    }

    public void setDestPortOperation(String destPortOperation) {
        this.destPortOperation = destPortOperation;
    }

    public Integer getSrcPortStart() {
        return (srcPortStart != null ? Integer.valueOf(srcPortStart) : null);
    }

    public void setSrcPortStart(Integer srcPortStart) {
        this.srcPortStart = srcPortStart != null ?  srcPortStart.toString() : null;
    }

    public Integer getDestPortStart() {
        return (destPortStart != null ? Integer.valueOf(destPortStart) : null);
    }

    public void setDestPortStart(Integer destPortStart) {
        this.destPortStart = destPortStart != null ? destPortStart.toString() : null;
    }

    public Integer getDestPortEnd() {
        return (destPortEnd != null ? Integer.valueOf(destPortEnd) : null);
    }

    public void setDestPortEnd(Integer destPortEnd) {
        this.destPortEnd = destPortEnd != null ? destPortEnd.toString() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        L4Option l4Option = (L4Option) o;
        return Objects.equals(srcPortOperation, l4Option.srcPortOperation) &&
                Objects.equals(destPortOperation, l4Option.destPortOperation) &&
                Objects.equals(srcPortStart, l4Option.srcPortStart) &&
                Objects.equals(destPortStart, l4Option.destPortStart) &&
                Objects.equals(destPortEnd, l4Option.destPortEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), srcPortOperation, destPortOperation, srcPortStart, destPortStart, destPortEnd);
    }
}
