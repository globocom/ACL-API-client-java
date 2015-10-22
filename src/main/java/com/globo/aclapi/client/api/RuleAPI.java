/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.globo.aclapi.client.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.globo.aclapi.client.AbstractAPI;
import com.globo.aclapi.client.AclErrorCodeAPIException;
import com.globo.aclapi.client.ClientAclAPI;
import com.globo.aclapi.client.model.Job;
import com.globo.aclapi.client.model.Rule;
import com.google.api.client.json.GenericJson;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import java.lang.reflect.Type;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleAPI extends AbstractAPI<Rule> {
    static final Logger LOGGER = LoggerFactory.getLogger(RuleAPI.class);

    public RuleAPI(ClientAclAPI clientAclAPI) {
        super(clientAclAPI);
    }

    @Override
    protected Type getType() {
        return new TypeReference<Rule>() {}.getType();
    }

    /**
     * https://gitlab.globoi.com/supseg/acl_api/wikis/get-api-ipv46-acl-environment
     *
     */
    @Trace(dispatcher = true)
    public List<Rule> listByEnv(Long envId) {
        NewRelic.setTransactionName(null, "/globoACL/rule/listByEnv");
        Rule.RuleResponse aclResponse = this.get("/api/ipv4/acl/" + envId, Rule.RuleResponse.class);

        return aclResponse.getRules();
    }

    /**
     * https://gitlab.globoi.com/supseg/acl_api/wikis/put-api-ipv46-acl-environment
     *
     * @return RuleSaveResponse with jobId and ruleId
     */
    @Trace(dispatcher = true)
    public Rule.RuleSaveResponse save(Long envId, Rule rule) {
        NewRelic.setTransactionName(null, "/globoACL/rule/saveByEnv");
        Rule.RuleRequest request = new Rule.RuleRequest();
        request.addRule(rule);

        Rule.RuleSaveResponse result = this.put("/api/ipv4/acl/" + envId, request, Rule.RuleSaveResponse.class);
        rule.setId(result.getFirstRuleId().toString());

        return result;
    }

    /**
     * https://gitlab.globoi.com/supseg/acl_api/wikis/put-api-ipv46-acl-environment-num_vlan
     *
     * @return RuleSaveResponse with jobId and ruleId
     */
    @Trace(dispatcher = true)
    public Rule.RuleSaveResponse save(Long envId, Long numVlan, Rule rule){
        NewRelic.setTransactionName(null, "/globoACL/rule/saveByEnvAndNumVlan");

        LOGGER.info("[ACL_API] removing rule: "+ rule + ", envId: " + envId + ", numVlan: " + numVlan + ". "+ this.getUserCredentials());

        Rule.RuleRequest request = new Rule.RuleRequest();
        request.addRule(rule);

        Rule.RuleSaveResponse result = this.put("/api/ipv4/acl/" + envId + "/" + numVlan, request, Rule.RuleSaveResponse.class);
        rule.setId(result.getFirstRuleId().toString());

        return result;
    }

    /**
     * save the rule in ACL-API and force run the job, if the job fails, remove the rule and throw exception with jobs message
     *
     * @return rule object with id
     */
    @Trace(dispatcher = true)
    public Rule saveSync(Long envId, Long numVlan, Rule rule) {
        NewRelic.setTransactionName(null, "/globoACL/rule/saveSync");
        Rule.RuleSaveResponse result = save(envId, numVlan, rule);

        JobAPI jobApi = this.getClientAclAPI().getJobAPI();
        Job job = jobApi.get(result.getJobId());

        Job.Status statusJob = job.getStatus();
        if (statusJob.equals(Job.Status.PENDING)){
            try {
                jobApi.run(result.getJobId());
            } catch (AclErrorCodeAPIException ex) {
                remove(envId, numVlan, result.getFirstRuleId());
                throw ex;
            }
        }

        return rule;
    }

    /**
     * https://gitlab.globoi.com/supseg/acl_api/wikis/delete-api-ipv46-acl-environment-num_vlan-id
     *
     * @return jobId
     */
    @Trace(dispatcher = true)
    public Long remove(Long envId, Long numVlan, Long ruleId) {
        NewRelic.setTransactionName(null, "/globoACL/rule/remove");
        LOGGER.info("[ACL_API] removing rule: "+ruleId + ", envId: " + envId + ", numVlan: " + numVlan + ". "+ this.getUserCredentials());

        GenericJson result = this.delete("/api/ipv4/acl/" + envId + "/" + numVlan + "/" + ruleId, GenericJson.class);

        return (Long)result.get("job");
    }

    @Trace(dispatcher = true)
    public void removeSync(Long envId, Long numVlan, Long ruleId) {
        NewRelic.setTransactionName(null, "/globoACL/rule/removeSync");

        Long jobId = remove(envId, numVlan, ruleId);

        JobAPI jobAPI = this.getClientAclAPI().getJobAPI();

        LOGGER.info("[ACL_API] running remove job: " + jobId + this.getClientAclAPI().getUsername());
        jobAPI.run(jobId);

    }
}
