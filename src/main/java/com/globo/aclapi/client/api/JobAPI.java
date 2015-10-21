package com.globo.aclapi.client.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.globo.aclapi.client.AbstractAPI;
import com.globo.aclapi.client.ClientAclAPI;
import com.globo.aclapi.client.model.Job;
import com.globo.aclapi.client.model.Rule;
import com.google.api.client.json.GenericJson;
import java.lang.reflect.Type;
import java.util.List;

public class JobAPI extends AbstractAPI<Rule> {

    public JobAPI(ClientAclAPI clientAclAPI) {
        super(clientAclAPI);
    }

    @Override
    protected Type getType() {
        return new TypeReference<Rule>() {}.getType();
    }

    public GenericJson run(Long jobId) {
        GenericJson result = this.get("/api/jobs/" + jobId + "/run", GenericJson.class);
        return result;
    }

    public Job get(Long jobId) {
        Job.JobGetResponse jobResponse = this.get("/api/jobs/" + jobId, Job.JobGetResponse.class);
        return jobResponse.getJob();
    }

    public List<Job> list() {
        Job.JobListResponse jobResponse = this.get("/api/jobs", Job.JobListResponse.class);

        return jobResponse.getJobs();
    }
}
