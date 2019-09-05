package com.secutix.plugin.util.impl;

import java.net.ProxySelector;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.google.common.base.Preconditions;
import com.secutix.plugin.util.SSLContextHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.secutix.plugin.util.HttpClientHelper;
import com.secutix.plugin.util.StxPluginHttpResponse;

public class HttpClientHelperDefaultImpl implements HttpClientHelper {

    private static final Integer DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final Integer DEFAULT_CONNECTION_TIMEOUT = 5000;

    private int defaultReadTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int defaultConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    private int readTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    private PoolingHttpClientConnectionManager connManager;

    private boolean ignoreSslVerification = false;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SSLContext sslContext;
    private boolean checkHostames = true;

    {
        MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    protected HttpClientBuilder getHttpClientBuilder() {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        //TODO need to be redesigned
        buildConnectionManager();

        if (sslContext != null) {
            httpClientBuilder.setSSLContext(sslContext);
        } else if (isIgnoreSslVerification()) {
            try {
                sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            } catch (final Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }
        buildConnectionManager();

        RequestConfig.Builder requestBuilder = RequestConfig.custom();

        requestBuilder = requestBuilder.setSocketTimeout(readTimeout);

        requestBuilder = requestBuilder.setConnectTimeout(connectionTimeout);

        requestBuilder = requestBuilder.setConnectionRequestTimeout(connectionTimeout);

        httpClientBuilder.setConnectionManager(connManager);
        httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());

        final SystemDefaultRoutePlanner routePlanner =
                new SystemDefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE, ProxySelector.getDefault());
        httpClientBuilder.setRoutePlanner(routePlanner);

        return httpClientBuilder;
    }

    private void buildConnectionManager() {
        if (!checkHostames) {
            Preconditions.checkArgument(sslContext != null, "SSL Context should not be null when asking not to check names");
            // don't check Hostnames, either.
            // -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to
            // weaken
            @SuppressWarnings("deprecation") final HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            // here's the special part:
            // -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
            // -- and create a Registry, to register it.
            //
            final SSLConnectionSocketFactory sslSocketFactory =
                    new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory).build();

            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            connManager = new PoolingHttpClientConnectionManager();
        }
    }

    @Override
    public StxPluginHttpResponse doPost(final String service, final HttpEntity httpEntityToPost,
                                        final Map<String, String> additionalHeaders) {
        try {
            return doPostInternal(service, httpEntityToPost, additionalHeaders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StxPluginHttpResponse doPost(String service, Object objectToPost, Map<String, String> additionalHeaders) {
        try {
            final StringEntity input = mapObjectToJsonEntity(objectToPost);
            return doPostInternal(service, input, additionalHeaders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StxPluginHttpResponse doPostInternal(final String service, final HttpEntity entity,
                                                final Map<String, String> additionalHeaders) {
        final HttpClientBuilder httpClientBuilder = getHttpClientBuilder();

        final HttpPost httpPost = new HttpPost(service);

        if (additionalHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (entity != null) {
            httpPost.setEntity(entity);
        }

        return executeHttpCall(httpClientBuilder, httpPost);
    }

    @Override
    public StxPluginHttpResponse doPut(final String service, final HttpEntity httpEntityToPost,
                                       final Map<String, String> additionalHeaders) {
        try {
            return doPutInternal(service, httpEntityToPost, additionalHeaders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StxPluginHttpResponse doPut(String service, Object objectToPost, Map<String, String> additionalHeaders) {
        try {
            final StringEntity input = mapObjectToJsonEntity(objectToPost);
            return doPutInternal(service, input, additionalHeaders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StxPluginHttpResponse doPutInternal(final String service, final HttpEntity entity,
                                               final Map<String, String> additionalHeaders) {
        final HttpClientBuilder httpClientBuilder = getHttpClientBuilder();

        final HttpPut httpPut = new HttpPut(service);

        if (additionalHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (entity != null) {
            httpPut.setEntity(entity);
        }

        return executeHttpCall(httpClientBuilder, httpPut);
    }

    @Override
    public StxPluginHttpResponse doGet(String service, Map<String, String> additionalHeaders) {
        return doGet(service, additionalHeaders, null);
    }

    @Override
    public StxPluginHttpResponse doPatch(final String service, final HttpEntity httpEntityToPost,
                                         final Map<String, String> additionalHeaders) {
        try {
            return doPatchInternal(service, httpEntityToPost, additionalHeaders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StxPluginHttpResponse doPatch(String service, Object objectToPost, Map<String, String> additionalHeaders) {
        try {
            final StringEntity input = mapObjectToJsonEntity(objectToPost);
            return doPatchInternal(service, input, additionalHeaders);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public StxPluginHttpResponse doPatchInternal(final String service, final HttpEntity entity,
                                                 final Map<String, String> additionalHeaders) {
        final HttpClientBuilder httpClientBuilder = getHttpClientBuilder();

        final HttpPatch httpPatch = new HttpPatch(service);

        if (additionalHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                httpPatch.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if (entity != null) {
            httpPatch.setEntity(entity);
        }

        return executeHttpCall(httpClientBuilder, httpPatch);
    }

    @Override
    public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders) {
        return doDelete(service, additionalHeaders, null);
    }

    @Override
    public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders,
                                          HttpClientContext context) {
        final HttpClientBuilder httpClientBuilder = getHttpClientBuilder();
        if (context == null) {
            context = HttpClientContext.create();
        }
        final HttpDelete httpDelete = new HttpDelete(service);
        if (additionalHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return executeHttpCall(httpClientBuilder, httpDelete);
    }

    @Override
    public StxPluginHttpResponse doGet(String service, Map<String, String> additionalHeaders,
                                       HttpClientContext context) {
        final HttpClientBuilder httpClientBuilder = getHttpClientBuilder();
        if (context == null) {
            context = HttpClientContext.create();
        }
        final HttpGet httpGet = new HttpGet(service);
        if (additionalHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return executeHttpCall(httpClientBuilder, httpGet);
    }

    private StringEntity mapObjectToJsonEntity(Object objectToPost) throws JsonProcessingException {
        final String inputJson = MAPPER.writeValueAsString(objectToPost);
        final StringEntity input = new StringEntity(inputJson, "UTF-8");
        input.setContentType("application/json");
        return input;
    }

    private StxPluginHttpResponse executeHttpCall(final HttpClientBuilder httpClientBuilder,
                                                  final HttpUriRequest httpRequest) {
        try (CloseableHttpClient httpClient = httpClientBuilder.build();
             final CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {
            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            return new StxPluginHttpResponse(statusCode, EntityUtils.toString(httpResponse.getEntity()));

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public PoolingHttpClientConnectionManager getConnManager() {
        return connManager;
    }

    public void setConnManager(PoolingHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public boolean isIgnoreSslVerification() {
        return ignoreSslVerification;
    }

    public void setIgnoreSslVerification(boolean ignoreSslVerification) {
        this.ignoreSslVerification = ignoreSslVerification;
    }

    public static Map<String, String> addBasicAuthenticationHeader(String login, String password,
                                                                   Map<String, String> currentHeaders) {

        HashMap<String, String> extendedHeaders;
        if (currentHeaders != null) {
            extendedHeaders = new HashMap<>(currentHeaders);
        } else {
            extendedHeaders = new HashMap<>();
        }

        final String loginChain = Optional.fromNullable(login).or("") + ":" + Optional.fromNullable(password).or("");

        final byte[] encodedBytes = Base64.encodeBase64(loginChain.getBytes());
        extendedHeaders.put("Authorization", "Basic " + new String(encodedBytes));
        return extendedHeaders;
    }

    @Override
    public void setDefaultConnectionTimeout(int connectionTimeout) {
        this.defaultConnectionTimeout = connectionTimeout;
    }

    @Override
    public void setDefaultReadTimeout(int readTimeout) {
        this.defaultReadTimeout = readTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }


    @Override
    public void setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public void setCheckHostnames(boolean checkHostnames) {
        this.checkHostames = checkHostnames;
    }

}
