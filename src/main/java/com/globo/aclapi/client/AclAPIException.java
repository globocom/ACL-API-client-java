package com.globo.aclapi.client;

public class AclAPIException extends RuntimeException {
    public AclAPIException(String msg) { super(msg); }
    public AclAPIException(String msg, Throwable e) { super(msg, e); }
}
