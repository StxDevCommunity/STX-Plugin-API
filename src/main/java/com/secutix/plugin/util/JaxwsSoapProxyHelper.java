package com.secutix.plugin.util;

public interface JaxwsSoapProxyHelper {

	<T> T getProxy(Class<T> serviceInterface, String endPoint,
			String wsdlPath,
			String namespaceUri, String serviceName);

	<T> T getProxyWithWsSecurity(Class<T> serviceInterface, String endPoint, String wsdlPath,
			String namespaceUri, String serviceName,
			String wsSecurityLogin, String wsSecurityPassword);

	<T> T getProxy(Class<T> serviceInterface, String endPoint, String wsdlPath);

	void setSoapLogger(SoapLogger soapLogger);

}
