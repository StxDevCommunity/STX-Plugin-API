package com.secutix.plugin.util.impl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.google.common.base.Strings;
import com.secutix.plugin.util.JaxwsSoapProxyHelper;
import com.secutix.plugin.util.SoapLogger;

public class JaxwsSoapProxyHelperDefaultImpl implements JaxwsSoapProxyHelper {

	private static final Integer DEFAULT_TIMEOUT = 10000;
	private Integer requestTimeout = DEFAULT_TIMEOUT;
	private Integer connectTimeout = DEFAULT_TIMEOUT;

	private SoapLogger soapLogger;

	public JaxwsSoapProxyHelperDefaultImpl(SoapLogger soapLogger) {
		super();
		this.soapLogger = soapLogger;
	}

	public JaxwsSoapProxyHelperDefaultImpl() {
	}

	@Override
	public <T> T getProxy(Class<T> serviceInterface, String endPoint, String wsdlPath) {

		WebService annotation = serviceInterface.getAnnotation(WebService.class);
		if (annotation == null) {
			throw new RuntimeException("Expecting annotation @WebService on serviceInterface:"
					+ serviceInterface + " to retrieve serviceName and namespaceUri");
		}

		String serviceName = !Strings.isNullOrEmpty(annotation.serviceName())
				? annotation.serviceName() : annotation.name();
		if (Strings.isNullOrEmpty(serviceName)) {
			throw new RuntimeException(
					"No service name for annotation @WebService on serviceInterface:"
							+ serviceInterface);
		}

		return getProxy(serviceInterface, endPoint, wsdlPath, annotation.targetNamespace(),
				serviceName);

	}

	@Override
	public <T> T getProxy(Class<T> serviceInterface, String endPoint, String wsdlPath,
			String namespaceUri, String serviceName) {
		QName qName = new QName(namespaceUri, serviceName);

		URL wsdlLocation;
		try {
			wsdlLocation = serviceInterface.getResource(wsdlPath);
			//
			// wsdlLocation = new URL(null, "classpath:" + wsdlPath,
			// new Handler(ClassLoader.getSystemClassLoader()));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		T result = Service.create(wsdlLocation, qName).getPort(serviceInterface);

		Map<String, Object> requestContext = ((BindingProvider) result).getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint);
		if (requestTimeout != null || connectTimeout != null) {
			if (requestTimeout != null) {
				requestContext.put("com.sun.xml.ws.request.timeout", requestTimeout);
			}
			if (connectTimeout != null) {
				requestContext.put("com.sun.xml.ws.connect.timeout", connectTimeout);
			}
		}

		if (soapLogger != null) {
			Binding binding = ((BindingProvider) result).getBinding();
			@SuppressWarnings("rawtypes")
			List<javax.xml.ws.handler.Handler> handlerChain = binding.getHandlerChain();
			handlerChain.add(new MessageLoggerImpl(soapLogger));
			binding.setHandlerChain(handlerChain);
		}
		return result;
	}

	@Override
	public <T> T getProxyWithWsSecurity(Class<T> serviceInterface, String url, String wsdlPath,
			String namespaceUri, String serviceName, String wsSecurityLogin,
			String wsSecurityPassword) {
		T proxy = getProxy(serviceInterface, url, wsdlPath, namespaceUri, serviceName);
		// This is the block that apply the Ws Security to the request
		BindingProvider bindingProvider = (BindingProvider) proxy;
		@SuppressWarnings("rawtypes")
		List<javax.xml.ws.handler.Handler> handlerChain = bindingProvider.getBinding()
				.getHandlerChain();
		handlerChain.add(0, new WSSecurityHeaderSOAPHandler(wsSecurityLogin, wsSecurityPassword));
		bindingProvider.getBinding().setHandlerChain(handlerChain);

		return proxy;
	}

	public static class Handler extends URLStreamHandler {
		/** The classloader to find resources from. */
		private final ClassLoader classLoader;

		public Handler() {
			this.classLoader = Thread.currentThread().getContextClassLoader();
		}

		public Handler(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			final URL resourceUrl = classLoader.getResource(u.getPath());
			return resourceUrl.openConnection();
		}
	}

	static class ConfigurableStreamHandlerFactory implements URLStreamHandlerFactory {
		private final Map<String, URLStreamHandler> protocolHandlers;

		public ConfigurableStreamHandlerFactory(String protocol, URLStreamHandler urlHandler) {
			protocolHandlers = new HashMap<String, URLStreamHandler>();
			addHandler(protocol, urlHandler);
		}

		public void addHandler(String protocol, URLStreamHandler urlHandler) {
			protocolHandlers.put(protocol, urlHandler);
		}

		@Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
			return protocolHandlers.get(protocol);
		}
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setDumpSoapExchanges(boolean dump) {

		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
				Boolean.toString(dump));
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
				Boolean.toString(dump));
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump",
				Boolean.toString(dump));
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump",
				Boolean.toString(dump));

	}

	public SoapLogger getSoapLogger() {
		return soapLogger;
	}

	@Override
	public void setSoapLogger(SoapLogger soapLogger) {
		this.soapLogger = soapLogger;
	}

	public class MessageLoggerImpl implements SOAPHandler<SOAPMessageContext> {

		private SoapLogger soapLogger;

		public MessageLoggerImpl(SoapLogger soapLogger) {
			super();
			this.soapLogger = soapLogger;
		}

		@Override
		public Set<QName> getHeaders() {
			return Collections.EMPTY_SET;
		}

		@Override
		public boolean handleMessage(SOAPMessageContext context) {
			return soapLogger.handleMessage(context);
		}

		@Override
		public boolean handleFault(SOAPMessageContext context) {
			return true;
		}

		@Override
		public void close(MessageContext context) {
		}
	}
}
