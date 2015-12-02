package com.globo.aclapi.client;

public class AclAPIException extends RuntimeException {

    protected Integer httpStatus;

    public AclAPIException(String msg) {
        super(msg);
    }

    public AclAPIException(String msg, Integer httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public AclAPIException(String msg, Throwable e) {
        super(msg, e);
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}
