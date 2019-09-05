package com.secutix.plugin.util.impl;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.secutix.plugin.util.XmlRpcClientHelper;

public class XmlRpcClientHelperDefaultImpl implements XmlRpcClientHelper {

	private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
	private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

	private int readTimeout = DEFAULT_SOCKET_TIMEOUT;
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	@Override
	public Object execute(String serverUrl, String username, String password, String method, Object[] params) {
		try {
			final XmlRpcClientConfigImpl xmlRpcClientConfig = new XmlRpcClientConfigImpl();
			xmlRpcClientConfig.setEnabledForExtensions(true);
			xmlRpcClientConfig.setServerURL(new URL(serverUrl));
			xmlRpcClientConfig.setConnectionTimeout(connectionTimeout);
			xmlRpcClientConfig.setReplyTimeout(readTimeout);
			xmlRpcClientConfig.setBasicUserName(username);
			xmlRpcClientConfig.setBasicPassword(password);
			final XmlRpcClient xmlRpcClient = new XmlRpcClient();
			xmlRpcClient.setConfig(xmlRpcClientConfig);
			return xmlRpcClient.execute(method, params);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
