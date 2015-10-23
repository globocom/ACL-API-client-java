package com.globo.aclapi.client.model;


import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.List;

public class Job extends GenericJson{

    @Key("id_job")
    private String id;

    @Key("result")
    private String result;

    @Key("status")
    private String status;

    @Key("owner")
    private String owner;

    @Key("ip_version")
    private String ipVersion;

    @Key("num_vlan")
    private String numVlan;

    @Key("environment")
    private String environment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    public String getNumVlan() {
        return numVlan;
    }

    public void setNumVlan(String numVlan) {
        this.numVlan = numVlan;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Status getStatus() {
        return Status.valueOf(status);
    }

    public enum Status {
        PENDING, ERROR, SUCCESS;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", result='" + result + '\'' +
                ", status='" + status + '\'' +
                ", owner='" + owner + '\'' +
                ", ipVersion='" + ipVersion + '\'' +
                ", numVlan='" + numVlan + '\'' +
                ", environment='" + environment + '\'' +
                '}';
    }

    public static class JobListResponse extends GenericJson{

        @Key("jobs")
        private List<Job> jobs;

        public List<Job> getJobs() {
            return jobs;
        }

        public void setJobs(List<Job> jobs) {
            this.jobs = jobs;
        }
    }
    public static class JobGetResponse extends GenericJson {
        @Key("jobs")
        private Job job;

        public Job getJob() {
            return job;
        }

        public void setJob(Job job) {
            this.job = job;
        }
    }
}
