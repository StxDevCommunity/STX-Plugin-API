package com.secutix.plugin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Splitter;

public class UrlHelper {

	public static UrlBuilder getUrlBuilder() {
		return new UrlBuilder();
	}

	private static final String CHARSET = "UTF-8";

	private static String appendParamToUrl(final String url, final String paramName,
			final String paramValue) {
		if (url != null && paramName != null) {
			final StringBuilder sb = new StringBuilder(url);
			if (url.contains("?")) {
				sb.append("&");
			} else {
				sb.append("?");
			}
			try {
				sb.append(URLEncoder.encode(paramName, CHARSET)).append("=")
						.append(paramValue == null ? "" : URLEncoder.encode(paramValue, CHARSET));
				return sb.toString();
			} catch (UnsupportedEncodingException uee) {
				throw new RuntimeException(uee);
			}
		} else {
			return null;
		}
	}

	// private static String appendParamsToUrl(final String url,
	// final Map<String, String> parameters) {
	// String finalUrl = url;
	// List<String> orderedKeys = new ArrayList<>(parameters.keySet());
	// Collections.sort(orderedKeys);
	// for (String key : orderedKeys) {
	// finalUrl = appendParamToUrl(finalUrl, key, parameters.get(key));
	// }
	// return finalUrl;
	//
	// }

	public static class UrlBuilder {
		private String baseUrl;

		private Map<String, String> urlParametes = new TreeMap<>();

		public UrlBuilder() {
		}

		public UrlBuilder setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;

			return this;
		}

		public UrlBuilder addParameters(Map<String, String> parameters) {
			urlParametes.putAll(parameters);
			return this;
		}

		public UrlBuilder addParameter(String key, String value) {
			urlParametes.put(key, value);
			return this;
		}

		public String build() {
			if (baseUrl == null) {
				throw new IllegalArgumentException("BaseUrl must be defined !");
			}

			String finalUrl = baseUrl;
			List<String> orderedKeys = new ArrayList<>(urlParametes.keySet());
			Collections.sort(orderedKeys);
			for (String key : orderedKeys) {
				finalUrl = appendParamToUrl(finalUrl, key, urlParametes.get(key));
			}
			return finalUrl;
		}


	}

	public static Map<String, String> extractParametersFromUrl(String url) {
		if (url == null || url.indexOf("?") == -1) {
			return Collections.emptyMap();
		} else {
			int idx = url.indexOf("?");
			String paramString = url.substring(idx + 1);
			Map<String, String> encodedParameters = Splitter.on("&").withKeyValueSeparator("=")
					.split(paramString);
			Map<String, String> decodedParameters = new HashMap<>();
			for (Map.Entry<String, String> paramAndValue : encodedParameters.entrySet()) {
				try {
					String value = URLDecoder.decode(paramAndValue.getValue(), CHARSET);
					decodedParameters.put(paramAndValue.getKey(), value);
				} catch (UnsupportedEncodingException uee) {
					throw new RuntimeException(uee);
				}
			}
			return decodedParameters;
		}

	}

	public static String extractBasicUrl(String url) {
		if (url == null) {
			return "";
		}
		int idx = url.lastIndexOf("?");
		if (idx == -1) {
			return url;
		} else {
			return url.substring(0, idx);
		}

	}

}
