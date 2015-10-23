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
package com.globo.aclapi.client.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.ArrayList;
import java.util.List;

public class Rule extends GenericJson {

    @Key("id")
    private String id;

    @Key("protocol")
    private String protocol;

    @Key("action")
    private String action;

    @Key("source")
    private String source;

    @Key("destination")
    private String destination;

    @Key("icmp-options")
    private ICMPOption icmpOptions;

    @Key("l4-options")
    private L4Option l4Options;

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public L4Option getL4Options() {
        return l4Options;
    }

    public void setL4Options(L4Option l4Options) {
        this.l4Options = l4Options;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Action getAction() {
        return Action.fromApiCode(action);
    }

    public void setAction(Action action) {
        this.action = action.apiCode;
    }

    public Protocol getProtocol() {
        return Protocol.fromApiCode(protocol);
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol.apiCode;
    }

    public ICMPOption getIcmpOptions() {
        return icmpOptions;
    }

    public void setIcmpOptions(ICMPOption icmpOptions) {
        this.icmpOptions = icmpOptions;
    }

    public enum Action {
        PERMIT("permit");
        private String apiCode;

        private Action(String apiCode) {
            this.apiCode = apiCode;
        }

        public static Action fromApiCode(String code) {
            for (Action action : Action.values()) {
                if (action.apiCode.equals(code)) {
                    return action;
                }
            }
            return null;
        }
    }
    public enum Protocol {
        TCP("tcp"),
        UDP("udp"),
        ICMP("icmp"),
        IP("ip");

        private String apiCode;
        private Protocol(String apiCode) {
            this.apiCode = apiCode;
        }

        public static Protocol fromApiCode(String code) {
            for (Protocol action : Protocol.values()) {
                if (action.apiCode.equals(code)) {
                    return action;
                }
            }
            return null;
        }
    }
    public static class RuleResponse extends GenericJson {

        @Key("rules")
        private List<Rule> rules;

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }
    public static class RuleSaveResponse extends GenericJson {
        @Key("jobs")
        private List<Long> jobId;

        @Key("id")
        private List<Long> ids;

        public Long getFirstRuleId() {
            return ids.get(0);
        }

        public List<Long> getIds() {
            return ids;
        }

        public Long getJobId() {
            return jobId.get(0);
        }
    }

    public static class RuleRequest extends GenericJson {

        @Key("kind")
        private String kind = "default#acl";

        @Key("rules")
        private List<Rule> rules = new ArrayList<Rule>();

        public void addRule(Rule rule) {
            this.rules.add(rule);
        }
    }
}
