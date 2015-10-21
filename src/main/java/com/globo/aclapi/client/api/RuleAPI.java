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
import java.lang.reflect.Type;
import java.util.List;

public class RuleAPI extends AbstractAPI<Rule> {

    public RuleAPI(ClientAclAPI clientAclAPI) {
        super(clientAclAPI);
    }

    @Override
    protected Type getType() {
        return new TypeReference<Rule>() {}.getType();
    }

    public List<Rule> listByEnv(Long envId) {
        Rule.RuleResponse aclResponse = this.get("/api/ipv4/acl/" + envId, Rule.RuleResponse.class);

        return aclResponse.getRules();
    }


    public Rule.RuleSaveResponse save(Long envId, Rule rule) {
        Rule.RuleRequest request = new Rule.RuleRequest();
        request.addRule(rule);

        Rule.RuleSaveResponse result = this.put("/api/ipv4/acl/" + envId, request, Rule.RuleSaveResponse.class);
        rule.setId(result.getFirstRuleId().toString());

        return result;
    }

    /**
     * save the rule in ACL-API and force run the job, if the job fails, remove the rule and throw exception with jobs message
     * @param envId
     * @param numVlan
     * @param rule
     * @return
     */
    public Rule saveSync(Long envId, Long numVlan, Rule rule) {
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

    public void remove(Long envId, Long numVlan, Long ruleId) {
        this.delete("/api/ipv4/acl/" + envId + "/" + numVlan + "/" + ruleId);
    }

    public Rule.RuleSaveResponse save(Long envId, Long numVlan, Rule rule){
        Rule.RuleRequest request = new Rule.RuleRequest();
        request.addRule(rule);

        Rule.RuleSaveResponse result = this.put("/api/ipv4/acl/" + envId + "/" + numVlan, request, Rule.RuleSaveResponse.class);

        return result;
    }

}
