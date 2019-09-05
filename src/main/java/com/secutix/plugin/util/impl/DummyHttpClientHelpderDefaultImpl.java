package com.secutix.plugin.util.impl;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.protocol.HttpClientContext;

import com.secutix.plugin.util.HttpClientHelper;
import com.secutix.plugin.util.StxPluginHttpResponse;

import javax.net.ssl.SSLContext;

public class DummyHttpClientHelpderDefaultImpl implements HttpClientHelper {

	@Override
	public StxPluginHttpResponse doPost(String service, HttpEntity obtectToPost,
			Map<String, String> additionnalHeaders) {
		return new StxPluginHttpResponse(503, "No post defined");
	}

	@Override
	public StxPluginHttpResponse doPost(String service, Object obtectToPost,
			Map<String, String> additionnalHeaders) {
		return new StxPluginHttpResponse(503, "No post defined");
	}

	@Override
	public StxPluginHttpResponse doPut(String service, HttpEntity httpEntityToPost,
			Map<String, String> additionalHeaders) {
		return new StxPluginHttpResponse(503, "No put defined");
	}

	@Override
	public StxPluginHttpResponse doPut(String service, Object objectToPost, Map<String, String> additionalHeaders) {
		return new StxPluginHttpResponse(503, "No put defined");
	}

	@Override
	public StxPluginHttpResponse doPatch(String service, HttpEntity httpEntityToPost,
			Map<String, String> additionalHeaders) {
		return new StxPluginHttpResponse(503, "No patch defined");
	}

	@Override
	public StxPluginHttpResponse doPatch(String service, Object objectToPost, Map<String, String> additionalHeaders) {
		return new StxPluginHttpResponse(503, "No patch defined");
	}

	@Override
	public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders) {
		return new StxPluginHttpResponse(503, "No delete defined");
	}

	@Override
	public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders,
			HttpClientContext context) {
		return new StxPluginHttpResponse(503, "No put defined");
	}

	@Override
	public StxPluginHttpResponse doGet(String service, Map<String, String> additionnalHeaders) {
		return new StxPluginHttpResponse(503, "No get defined");
	}

	@Override
	public void setDefaultConnectionTimeout(int connectionTimeout) {

	}

	@Override
	public void setDefaultReadTimeout(int readTimeout) {

	}


}
