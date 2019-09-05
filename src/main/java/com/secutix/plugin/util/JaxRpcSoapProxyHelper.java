package com.secutix.plugin.util;

import java.net.URL;

import org.apache.axis.client.Service;

public interface JaxRpcSoapProxyHelper {

	<T> T getProxy(Class<T> serviceClass, Service serviceLocator, URL endpoint);

	<T> T getProxyWithWsSecurity(Class<T> serviceClass, Service serviceLocator, URL endpoint,
			String login, String password);

}
