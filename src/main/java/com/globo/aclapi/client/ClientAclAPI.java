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

import com.globo.aclapi.client.api.JobAPI;
import com.globo.aclapi.client.api.RuleAPI;
import com.globo.aclapi.client.api.EnvAPI;
import com.globo.aclapi.client.oauth.BearerAuthSchemeFactory;
import com.globo.aclapi.client.oauth.TokenCredentials;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.net.ProxySelector;

public class ClientAclAPI {
    static final Logger LOGGER = LoggerFactory.getLogger(ClientAclAPI.class);

    private final HttpTransport httpTransport;
    private String baseUrl;
    private String username;
    private String password;
    private String token;

    protected ClientAclAPI(HttpTransport httpTransport) { this.httpTransport = httpTransport; }

    public static ClientAclAPI buildHttpAPI(String baseUrl, String username, String password) {
        ClientAclAPI clientAclAPI = new ClientAclAPI(getTransport());
        clientAclAPI.setBaseUrl(baseUrl);
        clientAclAPI.setUsername(username);
        clientAclAPI.setPassword(password);
        return clientAclAPI;
    }
    public static ClientAclAPI buildHttpAPI(String baseUrl, String token) {
        ClientAclAPI clientAclAPI = new ClientAclAPI(getTransport());
        clientAclAPI.setBaseUrl(baseUrl);
        clientAclAPI.setToken(token);
        return clientAclAPI;
    }

    protected HttpTransport getHttpTransport() { return this.httpTransport; }

    public String getBaseUrl() { return this.baseUrl; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static ApacheHttpTransport getTransport() throws RuntimeException {


        try {
            //@TODO verificar com o daniel a utilização desse context
            SSLContext ctx = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            };

            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);

//            return new ApacheHttpTransport(newDefaultHttpClient(SSLSocketFactory.getSocketFactory(), getHttpParams(), ProxySelector.getDefault()));
            return new ApacheHttpTransport(newDefaultHttpClient(ssf, getHttpParams(), ProxySelector.getDefault()));
        }catch (Exception e ){
            throw new RuntimeException("ERRO ssl schema", e);
        }
    }

    private static HttpParams getHttpParams() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpConnectionParams.setConnectionTimeout(params, 5000);
        ConnManagerParams.setMaxTotalConnections(params, 200);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(20));
        return params;
    }

    private static HttpClient newDefaultHttpClient(SSLSocketFactory socketFactory, HttpParams params, ProxySelector proxySelector) {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", socketFactory, 443));

        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);


        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, params) {
            @Override
            protected HttpContext createHttpContext() {
                HttpContext httpContext = super.createHttpContext();
                AuthSchemeRegistry authSchemeRegistry = new AuthSchemeRegistry();
                authSchemeRegistry.register("Bearer", new BearerAuthSchemeFactory());
                httpContext.setAttribute(ClientContext.AUTHSCHEME_REGISTRY, authSchemeRegistry);
                AuthScope sessionScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, "Bearer");

                Credentials credentials = new TokenCredentials("");
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(sessionScope, credentials);
                httpContext.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);
                return httpContext;
            }
        };
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        httpClient.setRoutePlanner(new ProxySelectorRoutePlanner(registry, proxySelector));



        return httpClient;
    }

    public EnvAPI getEnvAPI() {

        return new EnvAPI(this);
    }

    public RuleAPI getAclAPI() {
        return new RuleAPI(this);
    }

    public JobAPI getJobAPI() {
        return new JobAPI(this);
    }
}
