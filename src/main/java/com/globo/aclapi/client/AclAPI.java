package com.globo.aclapi.client;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.net.ProxySelector;

public class AclAPI {
    static final Logger LOGGER = LoggerFactory.getLogger(AclAPI.class);

    private final HttpTransport httpTransport;
    private String baseUrl;
    private String username;
    private String password;

    protected AclAPI(HttpTransport httpTransport) { this.httpTransport = httpTransport; }

    public static AclAPI buildHttpAPI(String baseUrl, String username, String password) {
        AclAPI aclAPI = new AclAPI(getTransport());
        aclAPI.setBaseUrl(baseUrl);
        aclAPI.setUsername(username);
        aclAPI.setPassword(password);
        return aclAPI;
    }

    protected HttpTransport getHttpTransport() { return this.httpTransport; }

    public String getBaseUrl() { return this.baseUrl; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static ApacheHttpTransport getTransport() {
        return new ApacheHttpTransport(newDefaultHttpClient(SSLSocketFactory.getSocketFactory(), getHttpParams(), ProxySelector.getDefault()));
    }

    private static HttpParams getHttpParams() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        ConnManagerParams.setMaxTotalConnections(params, 200);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(20));
        return params;
    }

    private static HttpClient newDefaultHttpClient(SSLSocketFactory socketFactory, HttpParams params, ProxySelector proxySelector) {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", socketFactory, 443));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, params);
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        httpClient.setRoutePlanner(new ProxySelectorRoutePlanner(registry, proxySelector));
        return httpClient;
    }
}
