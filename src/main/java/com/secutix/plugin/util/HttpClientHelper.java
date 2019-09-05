package com.secutix.plugin.util;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.protocol.HttpClientContext;

import javax.net.ssl.SSLContext;

public interface HttpClientHelper {

    public StxPluginHttpResponse doPost(String service, HttpEntity httpEntityToPost, Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doPost(String service, Object objectToPost, Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doPut(String service, HttpEntity httpEntityToPost,
                                       Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doPut(String service, Object objectToPost, Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doPatch(String service, HttpEntity httpEntityToPost,
                                         Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doPatch(String service, Object objectToPost, Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders);

    public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders,
                                          HttpClientContext context);

    public StxPluginHttpResponse doGet(String service, Map<String, String> additionalHeaders);

    public void setDefaultConnectionTimeout(int connectionTimeout);

    public void setDefaultReadTimeout(int readTimeout);

    default StxPluginHttpResponse doGet(String service, Map<String, String> additionalHeaders,
                                        HttpClientContext context) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * Allows to provide this connection with an SSL context.
     * @param sslContext
     */
    default public void setSSLContext(SSLContext sslContext) {

    }

    /**
     * In the SSL case, do not check hostnames. Handle with care !
     * @param checkHostnames
     */
    default void setCheckHostnames(boolean checkHostnames) {
    }


}
