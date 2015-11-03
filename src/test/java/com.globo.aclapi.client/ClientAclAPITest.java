package com.globo.aclapi.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientAclAPITest {

    @Before
    public void setup() {

    }

    @Test
    public void testGetHttpParams() {
        ClientAclAPI clientAclAPI = ClientAclAPI.buildHttpAPI("api_url", "api_user", "api_password", 5000, false);

        assertEquals("api_url", clientAclAPI.getBaseUrl());
        assertEquals("api_user", clientAclAPI.getUsername());
        assertEquals("api_password", clientAclAPI.getPassword());
    }
}
