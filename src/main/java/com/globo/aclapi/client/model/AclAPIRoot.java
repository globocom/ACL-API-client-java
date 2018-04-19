package com.globo.aclapi.client.model;

import com.google.api.client.json.GenericJson;

import java.util.ArrayList;
import java.util.List;

public class AclAPIRoot<T> extends GenericJson {

    private List<T> objectList;

    public AclAPIRoot() { this.objectList = new ArrayList<T>(); }

    public List<T> getObjectList() { return this.objectList; }

    public void setObjectList(List<T> objectList) { this.objectList = objectList; }

    /*
     * Return first object in list or <code>null</code> if list is empty
     * @return
     */
    public T getFirstObject() {
        if (this.getObjectList() == null || this.getObjectList().isEmpty()) {
            return null;
        }
        return this.getObjectList().get(0);
    }
}
