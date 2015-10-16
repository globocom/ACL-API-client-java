package com.globo.aclapi.client;

import com.globo.aclapi.client.model.AclAPIRoot;
import com.globo.aclapi.client.model.ErrorMessage;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;

public abstract class AbstractAPI<T> {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractAPI.class);
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
    static final JsonObjectParser parser = new JsonObjectParser(JSON_FACTORY);

    private final AclAPI aclAPI;

    private HttpRequestFactory requestFactory;

    protected AbstractAPI(AclAPI aclAPI) {
        if (aclAPI == null) {
            throw new IllegalArgumentException("No ACL API configured");
        }
        this.aclAPI = aclAPI;
        this.requestFactory = this.buildHttpRequestFactory();
    }

    protected AclAPI getAclAPI() { return this.aclAPI; }

    protected HttpRequestFactory buildHttpRequestFactory() {
        HttpRequestFactory request = this.getAclAPI().getHttpTransport().createRequestFactory(new HttpRequestInitializer() {
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
        return request;
    }

    protected void interceptRequest(HttpRequest request) { insertAuthenticationHeaders(request); }

    protected void insertAuthenticationHeaders(HttpRequest request) {
        request.getHeaders().setBasicAuthentication(aclAPI.getUsername(), aclAPI.getPassword());
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
                throw new AclAPIException(errorMsg.getCode() + " - " + errorMsg.getMsg());
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
}
