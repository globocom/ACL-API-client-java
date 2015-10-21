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
