package com.globo.aclapi.client;

/**
 * Created by lucas.castro on 10/21/15.
 */
public class AclErrorCodeAPIException  extends AclAPIException {

    private static final long serialVersionUID = -7272459136902597384L;

    private int code;

    private String description;

    public AclErrorCodeAPIException(String code, String description, Integer httpStatus) {
        super(code + ":" + description);
        this.code = Integer.valueOf(code);
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
