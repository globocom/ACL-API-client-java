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
package com.globo.aclapi.client;

import com.globo.aclapi.client.model.AclAPIRoot;
import com.globo.aclapi.client.model.ErrorMessage;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class AbstractAPI<T> {

    static final Logger LOGGER = LoggerFactory.getLogger(AbstractAPI.class);
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
    static final JsonObjectParser parser = new JsonObjectParser(JSON_FACTORY);
    private final ClientAclAPI clientAclAPI;

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private HttpRequestFactory requestFactory;

    protected AbstractAPI(ClientAclAPI clientAclAPI) {
        if (clientAclAPI == null) {
            throw new IllegalArgumentException("No ACL API configured");
        }
        this.clientAclAPI = clientAclAPI;
        this.requestFactory = this.buildHttpRequestFactory();
    }

    protected ClientAclAPI getClientAclAPI() { return this.clientAclAPI; }

    protected abstract Type getType();

    protected HttpRequestFactory buildHttpRequestFactory() {
        return this.getClientAclAPI().getHttpTransport().createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setNumberOfRetries(1);
                request.setThrowExceptionOnExecuteError(false);
                request.setParser(parser);
                request.setLoggingEnabled(true);
                request.getHeaders().setUserAgent("ACL-API-Client");
                request.setCurlLoggingEnabled(true);
                request.setUnsuccessfulResponseHandler(new HttpUnsuccessfulResponseHandler() {
                    @Override
                    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry) throws IOException {
                        return false;
                    }
                });
                request.setResponseInterceptor(new HttpResponseInterceptor() {
                    @Override
                    public void interceptResponse(HttpResponse response) throws IOException {
                        LOGGER.debug("Response from {} {} is {} {}",
                                response.getRequest().getRequestMethod(),
                                response.getRequest().getUrl(),
                                response.getStatusCode(),
                                response.getStatusMessage());
                        AbstractAPI.this.interceptResponse(response);
                    }
                });
                interceptRequest(request);
            }
        });
    }

    protected void interceptRequest(HttpRequest request) { insertAuthenticationHeaders(request); }

    protected void insertAuthenticationHeaders(HttpRequest request) {
        if ( this.clientAclAPI.getToken() != null && !this.clientAclAPI.getToken().isEmpty() ){
            request.getHeaders().setAuthorization("Bearer " + this.clientAclAPI.getToken());
        } else {
            request.getHeaders().setBasicAuthentication(this.clientAclAPI.getUsername(), this.clientAclAPI.getPassword());
        }
    }

    protected void interceptResponse(HttpResponse response) throws AclAPIException, IOException {
        handleExceptionIfNeeded(response);
    }

    protected void handleExceptionIfNeeded(HttpResponse response) throws AclAPIException, IOException {
        int statusCode = response.getStatusCode();
        if (statusCode/100 == 2) {
            // 200 family code
            return;
        } else if (statusCode/100 == 4 || statusCode/100 == 5) {
            // 400 and 500 family codes
            String responseAsString = response.parseAsString();
            if (responseAsString == null || !responseAsString.startsWith("{")) {
                // Not a valid JSON
                throw new AclAPIException("Unknown error in ACL API: " + responseAsString);
            }

            AclAPIRoot<ErrorMessage> responseObj = this.parse(responseAsString, ErrorMessage.class);
            ErrorMessage errorMsg = responseObj.getFirstObject();
            if (errorMsg != null && errorMsg.getCode() != null && errorMsg.getMsg() != null) {
                throw new AclErrorCodeAPIException(errorMsg.getCode(), errorMsg.getMsg());
            } else {
                throw new AclAPIException(responseAsString);
            }
        } else {
            throw new AclAPIException(response.parseAsString());
        }
    }

    protected <E> AclAPIRoot<E> parse(String responseAsString, Type type) throws AclAPIException {
        try {
            AclAPIRoot<E> aclAPIRoot = new AclAPIRoot<E>();

            if ("".equalsIgnoreCase(responseAsString)) {
                // Empty response
                return aclAPIRoot;
            }

            boolean isList = (responseAsString.startsWith("[") && responseAsString.endsWith("]")) ? true : false;

            Reader in = new StringReader(responseAsString);
            if (isList) {
                List<E> retList = (List<E>) parser.parseAndClose(in, type);
                aclAPIRoot.setObjectList(retList);
            } else {
                E retObj = (E) parser.parseAndClose(in, type);
                aclAPIRoot.getObjectList().add(retObj);
            }
            return aclAPIRoot;
        } catch (IOException e) {
            throw new AclAPIException("IO Error when parsing response: " + e.getMessage(), e);
        }
    }

    protected GenericUrl buildUrl(String suffixUrl) { return new GenericUrl(this.clientAclAPI.getBaseUrl() + suffixUrl); }

    protected <T extends GenericJson> T put(String suffixUrl, Object payload, Map<String, String> headers, Class<T> type) throws AclAPIException {
        try {
            GenericUrl url = this.buildUrl(suffixUrl);
            JsonHttpContent content = null;
            if (payload != null) {
                content = new JsonHttpContent(JSON_FACTORY, payload);
            }
            HttpRequest request = this.requestFactory.buildPutRequest(url, content);
            HttpHeaders httpHeaders = createHeaders(headers);
            if(httpHeaders != null) {
                request.setHeaders(httpHeaders);
            }
            HttpResponse response = request.execute();
            return parse(response.parseAsString(), type);
        } catch (IOException e) {
            throw new AclAPIException("IO Error during PUT request: " + e, e);
        }
    }

    protected <T extends GenericJson> T delete(String suffixUrl, Map<String, String> headers, Class<T> type) throws AclAPIException {
        try {
            GenericUrl url = this.buildUrl(suffixUrl);
            HttpRequest request = this.requestFactory.buildDeleteRequest(url);
            HttpHeaders httpHeaders = createHeaders(headers);
            if(httpHeaders != null) {
                request.setHeaders(httpHeaders);
            }
            HttpResponse response = request.execute();
            return parse(response.parseAsString(), type);
        } catch (IOException e) {
            throw new AclAPIException("IO Error during DELETE request: " + e, e);
        }
    }

    public <T extends GenericJson> T get(String suffixUrl, Class<T> type) {
        try {
            GenericUrl url = this.buildUrl(suffixUrl);
            HttpRequest request = this.requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();

            if ( response.getStatusCode()/100 == 2) {
                String result = response.parseAsString();
                return parse(result, type);
            }
            return null;
        } catch (IOException e) {
            throw new AclAPIException("IO Error during GET request: " + e, e);
        }
    }

    private HttpHeaders createHeaders(Map<String, String> headers) {
        if(headers != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            for (String key : headers.keySet()) {
                httpHeaders.set(key, headers.get(key));
            }
            return httpHeaders;
        }
        return null;
    }

    public static <T extends GenericJson> T parse(String output, Class<T> dataType) throws IOException {
        InputStream stream = new ByteArrayInputStream(output.getBytes(DEFAULT_CHARSET));

        com.google.api.client.json.JsonFactory jsonFactory = new JacksonFactory();
        return new JsonObjectParser(jsonFactory).parseAndClose(stream, DEFAULT_CHARSET, dataType);
    }

    public String getUserCredentials() {
        return ". Token: " + this.getClientAclAPI().getToken() + ", User: " + this.getClientAclAPI().getUsername();
    }
}
