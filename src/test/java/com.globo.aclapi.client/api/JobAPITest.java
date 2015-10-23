package com.globo.aclapi.client.api;


import com.globo.aclapi.client.MockGloboACL;
import com.globo.aclapi.client.TestUtil;
import com.globo.aclapi.client.model.Job;
import com.google.api.client.json.GenericJson;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class JobAPITest {

    private MockGloboACL globoAcl;
    private JobAPI jobAPI;

    @Before
    public void setUp() {
        this.globoAcl = new MockGloboACL("token_123");
        this.jobAPI = this.globoAcl.getJobAPI();
    }

    @Test
    public void testGet()  {
        String result = TestUtil.getSample("job_getById_response.json");
        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.GET, "/api/jobs/" + 357, result);

        Job job = jobAPI.get(357l);

        assertNotNull(job);
        assertEquals(Job.Status.ERROR, job.getStatus());
        assertEquals("357", job.getId());
        assertEquals("120", job.getEnvironment());
        assertEquals("97", job.getNumVlan());
        assertEquals("ipv4", job.getIpVersion());
        assertEquals("user_123", job.getOwner());
    }

    @Test
    public void testRun() throws Exception{
        String result = TestUtil.getSample("job_run_ok_response.json");

        this.globoAcl.registerFakeRequest(MockGloboACL.HttpMethod.GET, "/api/jobs/" + 357 + "/run", result);

        GenericJson job = jobAPI.run(357l);

        assertEquals(Arrays.asList(new BigDecimal(357l)), job.get("ids"));
    }



}

