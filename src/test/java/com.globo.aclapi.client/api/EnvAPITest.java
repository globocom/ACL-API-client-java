package com.globo.aclapi.client.api;

import com.globo.aclapi.client.AbstractAPI;
import com.globo.aclapi.client.MockGloboACL;
import com.globo.aclapi.client.TestUtil;
import com.globo.aclapi.client.model.Environment;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnvAPITest {

    MockGloboACL globoAcl;
    private EnvAPI envAPI;

    @Before
    public void setUp(){
        this.globoAcl = new MockGloboACL("token_123");
        this.envAPI = this.globoAcl.getEnvAPI();
    }
    public void testListRequest() throws Exception {
        String result = TestUtil.getSample("env_list.json");
        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.GET, "/api/ipv4/env/" + 123l, result);

        List<Environment> list = this.envAPI.list();
        assertEquals(3, list.size());

    }

    @Test
    public void testList() throws Exception {
        String result = TestUtil.getSample("env_list.json");

        Environment.EnvironmentResponse response = AbstractAPI.parse(result, Environment.EnvironmentResponse.class);


        List<Environment> envs = response.getEnvs();
        assertEquals(3, envs.size());

        Environment environment = envs.get(0);
        assertEquals("ENV_A", environment.getName());
        assertEquals("12", environment.getId());
        assertEquals("139", environment.getEnvironmentId());
        assertTrue(environment.isAutoVlan());
        assertTrue(environment.isSupernet());
        assertEquals("250",environment.getSupernetVlan());
        assertEquals("ipv4",environment.getIpVersion());


        environment = envs.get(2);
        assertEquals("ENV_C", environment.getName());
        assertEquals("14", environment.getId());
        assertEquals("123", environment.getEnvironmentId());
        assertTrue(environment.isAutoVlan());
        assertTrue(environment.isSupernet());
        assertEquals("249",environment.getSupernetVlan());
        assertEquals("ipv6",environment.getIpVersion());

    }
}
