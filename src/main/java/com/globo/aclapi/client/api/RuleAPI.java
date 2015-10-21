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
import com.globo.aclapi.client.ClientAclAPI;
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


    public void save(Long envId, Rule rule) {
        Rule.RuleRequest request = new Rule.RuleRequest();
        request.addRule(rule);
        GenericJson result = this.put("/api/ipv4/acl/" + envId, request, GenericJson.class);

        System.out.println(result);
    }


}
