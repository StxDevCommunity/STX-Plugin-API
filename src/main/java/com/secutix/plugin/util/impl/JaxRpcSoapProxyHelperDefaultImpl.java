package com.secutix.plugin.util.impl;

import java.lang.reflect.Method;
import java.net.URL;

import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;

import com.secutix.plugin.util.JaxRpcSoapProxyHelper;

public class JaxRpcSoapProxyHelperDefaultImpl implements JaxRpcSoapProxyHelper {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy(Class<T> serviceClass, Service serviceLocator, URL endpoint) {
		Method[] methods = serviceLocator.getClass().getMethods();
		Method createProxyMethod = null;
		for (Method m : methods) {
			if (serviceClass.isAssignableFrom(m.getReturnType())
					&& m.getParameterTypes().length == 1
					&& URL.class.isAssignableFrom(m.getParameterTypes()[0])) {
				createProxyMethod = m;
				break;

			}
		}
		if (createProxyMethod == null) {
			throw new IllegalArgumentException("Unable to find a method creating a proxy of type "
					+ serviceClass + " on locator : " + serviceLocator);
		}

		try {
			return (T) createProxyMethod.invoke(serviceLocator, endpoint);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	@Override
	public <T> T getProxyWithWsSecurity(Class<T> serviceClass, Service serviceLocator, URL endpoint,
			String login, String passwordParameter) {
		try {
			T proxy = getProxy(serviceClass, serviceLocator, endpoint);
			SOAPHeaderElement wsseSecurity = new SOAPHeaderElement(new PrefixedQName(
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
					"Security", "wsse"));
			String nullString = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
			MessageElement usernameToken = new MessageElement(nullString, "wsse:UsernameToken");
			MessageElement username = new MessageElement(nullString, "wsse:Username");
			MessageElement password = new MessageElement(nullString, "wsse:Password");
			username.setObjectValue(login);
			usernameToken.addChild(username);
			password.setObjectValue(passwordParameter);
			usernameToken.addChild(password);
			wsseSecurity.addChild(usernameToken);
			wsseSecurity.setMustUnderstand(true);
			wsseSecurity.setActor(null);
			((Stub) proxy).setHeader(wsseSecurity);
			return proxy;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
