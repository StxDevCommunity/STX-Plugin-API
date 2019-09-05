package com.secutix.plugin.util;

public class StxPluginHttpResponse {

	public static final int HTTP_200 = 200;

	private int statusCode;
	private String responseData;

	public StxPluginHttpResponse() {

	}

	public StxPluginHttpResponse(int statusCode, String responseData) {
		super();
		this.statusCode = statusCode;
		this.responseData = responseData;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

}
