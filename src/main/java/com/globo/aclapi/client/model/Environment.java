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
import java.util.List;

public class Environment extends GenericJson {

    @Key("id_environment")
    private String id; //at aclAPI

    @Key("environment")
    private String environmentId; //at networkAPI

    @Key("name")
    private String name;

    @Key("auto_vlan")
    private String autoVlan;

    @Key("ip_version")
    private String ipVersion;

    @Key("supernet")
    private String supernet;

    @Key("supernet_vlan")
    private String supernetVlan;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAutoVlan() {
        return "1".equals(autoVlan);
    }

    public void setAutoVlan(boolean autoVlan) {
        this.autoVlan = (autoVlan) ? "1" : "0";
    }

    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    public boolean isSupernet() {
        return "1".equals(this.supernet);
    }

    public void setSupernet(boolean supernet) {
        this.supernet = supernet ? "1" : "0";
    }

    public String getSupernetVlan() {
        return supernetVlan;
    }

    public void setSupernetVlan(String supernetVlan) {
        this.supernetVlan = supernetVlan;
    }

    public static class EnvironmentResponse extends GenericJson {
        @Key("envs")
        private List<Environment> envs;

        public List<Environment> getEnvs() {
            return envs;
        }

        public void setEnvs(List<Environment> envs) {
            this.envs = envs;
        }
    }

}
