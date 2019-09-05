package com.secutix.plugin.v1_0;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.secutix.plugin.util.HttpClientHelper;
import com.secutix.plugin.util.InterfaceParametersProvider;
import com.secutix.plugin.util.JaxRpcSoapProxyHelper;
import com.secutix.plugin.util.JaxwsSoapProxyHelper;
import com.secutix.plugin.util.LogLevel;
import com.secutix.plugin.util.Logger;
import com.secutix.plugin.util.OperatorInternationalizedLogger;
import com.secutix.plugin.util.RemoteFileAccessor;
import com.secutix.plugin.util.SalesContextAware;
import com.secutix.plugin.util.StopWatch;
import com.secutix.plugin.util.StxFunction;
import com.secutix.plugin.util.TranslationHelper;
import com.secutix.plugin.util.UniqueValuesHelper;
import com.secutix.plugin.util.ValidationException;
import com.secutix.plugin.util.ValidationHelper;
import com.secutix.plugin.util.XmlRpcClientHelper;
import com.secutix.plugin.util.impl.DummyHttpClientHelpderDefaultImpl;
import com.secutix.plugin.util.impl.DummyXmlRpcClientHelperDefaultImpl;
import com.secutix.plugin.util.impl.HttpClientHelperDefaultImpl;
import com.secutix.plugin.util.impl.InterfaceParametersProviderDefaultImpl;
import com.secutix.plugin.util.impl.JaxRpcSoapProxyHelperDefaultImpl;
import com.secutix.plugin.util.impl.JaxwsSoapProxyHelperDefaultImpl;
import com.secutix.plugin.util.impl.LoggerDefaultImpl;
import com.secutix.plugin.util.impl.OperatorInternationalizedLoggerDefaultImpl;
import com.secutix.plugin.util.impl.RemoteFileAccessorDefaultImpl;
import com.secutix.plugin.util.impl.SoapLoggerDefaultImpl;
import com.secutix.plugin.util.impl.UniqueValueHelperDefaultImpl;
import com.secutix.plugin.util.impl.XmlRpcClientHelperDefaultImpl;
import com.secutix.plugin.v1_0.dto.ParameterDefinition;

/**
 * To implement a plugin, override this class. Fill abstract method (they may have been pre-implemented by SecuTix team)
 * <ul>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * Only override methods for behaviors handled by your plugin. A method must never throw any exception, and always
 * return a PluginCallResult object.
 * <ul>
 * <li>Connection and context parameters (url, login, password...) can be accessed through : interfaceParametersProvider
 * </li>
 * <li>Logging must be done through getLogger() helper.</li>
 * <li>Logging for operator (in the application) must i18nized and done through logForOperator method.</li>
 * <li>doing http requests must be done through getCurrentHttpClientHelper() (this helper guarantee the use of internal
 * SecuTix proxies, timeouts, etc.)</li>
 * </ul>
 *
 * @author qhr, lkl
 */
public abstract class AbstractStxPlugin {

	private static final int MAX_TRANSLATION_LENGTH = 500;

	private static final int MAX_LANGUAGE_CODE_LENGTH = 4;

	private static final int MIN_TRANSLATION_LENGTH = 2;

	private static final int MIN_LANGUAGE_CODE_LENGTH = 2;

	private static final int MAX_INTERFACE_TYPE_LENGTH = 15;

	private static final int MIN_INTERFACE_TYPE_LENGTH = 3;

	private InterfaceParametersProvider interfaceParametersProvider = new InterfaceParametersProviderDefaultImpl();

	private Logger logger = new LoggerDefaultImpl();

	private OperatorInternationalizedLogger internationalizedLogger = new OperatorInternationalizedLoggerDefaultImpl(
			logger);

	private HttpClientHelper httpClientHelper = new HttpClientHelperDefaultImpl();

	private HttpClientHelper dummyHttpClientHelper = new DummyHttpClientHelpderDefaultImpl();

	private XmlRpcClientHelper xmlRpcClientHelper = new XmlRpcClientHelperDefaultImpl();

	private XmlRpcClientHelper dummyXmlRpcClientHelper = new DummyXmlRpcClientHelperDefaultImpl();

	private JaxwsSoapProxyHelper jaxwsSoapProxyHelper = null;
	private JaxRpcSoapProxyHelper jaxRpcSoapProxyHelper = new JaxRpcSoapProxyHelperDefaultImpl();

	private UniqueValuesHelper uniqueValuesHelper = new UniqueValueHelperDefaultImpl();

	public abstract String getInterfaceType();

	public abstract Map<String, String> getInterfaceNamesByLanguageCode();

	private RemoteFileAccessor remoteFileAccessor = new RemoteFileAccessorDefaultImpl();

	public RemoteFileAccessor getRemoteFileAccessor() {
		return remoteFileAccessor;
	}

	public void setRemoteFileAccessor(RemoteFileAccessor remoteFileAccessor) {
		this.remoteFileAccessor = remoteFileAccessor;
	}

	protected StopWatch watch;

	/**
	 * 
	 */
	public AbstractStxPlugin() {
		watch = new StopWatch(getLogger());
		SoapLoggerDefaultImpl soapLogger = new SoapLoggerDefaultImpl(logger,
				internationalizedLogger, interfaceParametersProvider);
		jaxwsSoapProxyHelper = new JaxwsSoapProxyHelperDefaultImpl(soapLogger);
	}

	/**
	 * Object to store the execution context, which will be added at the end of all logs for operators
	 */
	private static final ThreadLocal<String> operatorLogsSuffix = new ThreadLocal<String>();

	public void initLogSuffix(final Map<String, Object> suffixes) {
		if (suffixes != null && suffixes.size() > 0) {
			final String suffix = " [" + Joiner.on("|").useForNull("-").withKeyValueSeparator("=").join(suffixes) + "]";
			operatorLogsSuffix.set(suffix);
		}
	}

	public void clearLogSuffix() {
		final String suffix = operatorLogsSuffix.get();
		if (suffix != null) {
			operatorLogsSuffix.remove();
		}
	}

	/**
	 * Logs an message for the operator in SecuTix backend.
	 *
	 * @param lvl            the level of the error. Beware : an error log can trigger an email to the operator. Be explicit in the
	 *                       problems that you log and propose a solution.
	 * @param defaultMessage the default message. Pattern must be : [localeCode] message. Example [en] reading the catalog. This
	 *                       message will be chosen if the locale code is not matching any of the messages below.
	 * @param translations   same pattern as above.
	 */
	public final void logForOperator(LogLevel lvl, String defaultMessage, String... translations) {
		final String logSuffix = operatorLogsSuffix.get() == null ? "" : operatorLogsSuffix.get();
		final String[] suffixedTranslations =
				FluentIterable.from(Arrays.asList(translations)).transform(new Function<String, String>() {

					@Override
					public String apply(String t) {
						return t + logSuffix;
					}

				}).toArray(String.class);
		internationalizedLogger.log(lvl, defaultMessage + logSuffix, suffixedTranslations);
	}

	public void validate() throws ValidationException {
		ValidationHelper.validateString("getInterfaceType", getInterfaceType(), MIN_INTERFACE_TYPE_LENGTH,
				MAX_INTERFACE_TYPE_LENGTH);
		if (getInterfaceNamesByLanguageCode() == null || getInterfaceNamesByLanguageCode().size() == 0) {
			throw new ValidationException("interfaceNameByLanguageCode must be filled");
		}
		for (final Map.Entry<String, String> entry : getInterfaceNamesByLanguageCode().entrySet()) {
			ValidationHelper.validateString("languageCode", entry.getKey(), MIN_LANGUAGE_CODE_LENGTH,
					MAX_LANGUAGE_CODE_LENGTH);
			ValidationHelper.validateString("translation for : " + entry.getKey(), entry.getValue(),
					MIN_TRANSLATION_LENGTH, MAX_TRANSLATION_LENGTH);
		}
		if (interfaceParametersProvider == null) {
			throw new ValidationException("interfaceParameterProvider must be defined");
		}

		if (httpClientHelper == null) {
			throw new ValidationException("httpClientHelper must be defined");
		}
		if (dummyHttpClientHelper == null) {
			throw new ValidationException("dummyHttpClientHelper must be defined");
		}

		if (xmlRpcClientHelper == null) {
			throw new ValidationException("xmlRpcClientHelper must be defined");
		}
		if (dummyXmlRpcClientHelper == null) {
			throw new ValidationException("dummyXmlRpcClientHelper must be defined");
		}

		if (internationalizedLogger == null) {
			throw new ValidationException("logger must be defined");
		}

		for (ParameterDefinition parameterDefinition : listParameterDefinitions()) {
			ValidationHelper.validateString(
					"parameterDefinition.getParameterCode()",
					parameterDefinition.getParameterCode(), MIN_INTERFACE_TYPE_LENGTH,
					MAX_INTERFACE_TYPE_LENGTH);
			validateStringWithTranslations(
					parameterDefinition.getParameterName(),
					"parameter: " + parameterDefinition.getParameterCode());

		}

		if (!this.getClass().getName().startsWith("com.secutix.plugin")) {
			throw new ValidationException("Plugin:" + this.getClass().getName()
					+ " must be in package (or subpackage of) com.secutix.plugin or it won't be registered.");
		}


		for (Method m : this.getClass().getMethods()) {
			StxFunction stxFunc = m.getAnnotation(StxFunction.class);
			if (stxFunc != null) {
				validateFunction(stxFunc, MIN_INTERFACE_TYPE_LENGTH, MAX_INTERFACE_TYPE_LENGTH);
			}
		}

		List<StxFunction> functions = findAllFunctions(this.getClass());

		if (functions.isEmpty()) {
			throw new ValidationException("At least one function must be declared !");
		}

		for (StxFunction stxFunc : functions) {
			validateFunction(stxFunc, MIN_INTERFACE_TYPE_LENGTH, MAX_INTERFACE_TYPE_LENGTH);
		}

		Optional<SalesContextAware> optAware = getSalesContextAware(this.getClass());
		if (optAware.isPresent()) {
			SalesContextAware aware = optAware.get();
			String salesChannelParamCode = aware.salesChannelParameterCode();
			String pointOfSalesParamCode = aware.pointOfSalesParameterCode();

			ValidationHelper.validateString(
					"salesContextAware.salesChannelParamCode",
					salesChannelParamCode, MIN_INTERFACE_TYPE_LENGTH,
					MAX_INTERFACE_TYPE_LENGTH);
			ValidationHelper.validateString(
					"salesContextAware.pointOfSalesParamCode",
					pointOfSalesParamCode, MIN_INTERFACE_TYPE_LENGTH,
					MAX_INTERFACE_TYPE_LENGTH);

			long nbParams = listParameterDefinitions().stream().filter(def -> def.getParameterCode().equals(salesChannelParamCode)).count();
			if (nbParams != 1) {
				throw new ValidationException("Unable to find a parameter defined having the following code :" + salesChannelParamCode);
			}
			nbParams = listParameterDefinitions().stream().filter(def -> def.getParameterCode().equals(pointOfSalesParamCode)).count();
			if (nbParams != 1) {
				throw new ValidationException("Unable to find a parameter defined having the following code :" + pointOfSalesParamCode);
			}

		}


	}

	Optional<SalesContextAware> getSalesContextAware(Class<?> aClass) {
		if (aClass == null) {
			return Optional.empty();
		} else {
			SalesContextAware aware = aClass.getAnnotation(SalesContextAware.class);
			if (aware != null) {
				return Optional.of(aware);
			} else {
				return getSalesContextAware(aClass.getSuperclass());
			}
		}

	}


	protected List<StxFunction> findAllFunctions(Class<?> aClass) {
		List<StxFunction> out = new ArrayList<>();
		for (Method m : aClass.getDeclaredMethods()) {
			StxFunction stxFunc = m.getAnnotation(StxFunction.class);
			if (stxFunc != null) {
				out.add(stxFunc);
			}
		}
		if (aClass.getSuperclass() != null) {
			out.addAll(findAllFunctions(aClass.getSuperclass()));
		}

		return out;
	}

	private void validateFunction(StxFunction stxFunc, int minInterfaceTypeLength, int maxInterfaceTypeLength) throws ValidationException {
		ValidationHelper.validateString("stxFunction.code", stxFunc.functionCode(),
				1, 8);
		ValidationHelper.validateString("stxFunction.functionInternalCode",
				stxFunc.functionInternalCode(), minInterfaceTypeLength,
				2 * maxInterfaceTypeLength);
		validateStringWithTranslations(stxFunc.functionName(),
				"function:" + stxFunc.functionInternalCode());
	}

	public Map<String, String> validateStringWithTranslations(String stringWithTranslations,
															  String desc)
			throws ValidationException {
		Map<String, String> parameterNames;
		try {

			parameterNames = Splitter.on(",").withKeyValueSeparator("=")
					.split(stringWithTranslations);
		} catch (Exception e) {

			throw new ValidationException(
					"Error when defining translation string for " + desc
							+ ". Pattern must be:languageCode1=translation1,languageCode2=translation2,etc. Example : en=Demo plugin, fr=Plugin de démo. Here:"
							+ stringWithTranslations);
		}
		for (Map.Entry<String, String> entry : parameterNames.entrySet()) {
			ValidationHelper.validateString("languageCode for " + desc, entry.getKey(),
					MIN_LANGUAGE_CODE_LENGTH, MAX_LANGUAGE_CODE_LENGTH);
			ValidationHelper.validateString("translation for " + desc + ": " + entry.getKey(),
					entry.getValue(), MIN_TRANSLATION_LENGTH, MAX_TRANSLATION_LENGTH);
		}
		return parameterNames;
	}

	public String getTranslatedMessage(String defaultMessage, String... translations) {
		final String currentLanguage = interfaceParametersProvider.getLocale().getLanguage();
		return TranslationHelper.translateMessage(currentLanguage, defaultMessage, translations);
	}

	public final InterfaceParametersProvider getInterfaceParametersProvider() {
		return interfaceParametersProvider;
	}

	public final void setInterfaceParametersProvider(InterfaceParametersProvider interfaceParametersProvider) {
		this.interfaceParametersProvider = interfaceParametersProvider;
	}

	public final OperatorInternationalizedLogger getInternationalizedLogger() {
		return internationalizedLogger;
	}

	public final void setInternationalizedLogger(OperatorInternationalizedLogger logger) {
		this.internationalizedLogger = logger;
	}

	public final Logger getLogger() {
		return logger;
	}

	public final void setLogger(Logger logger) {
		this.logger = logger;
	}

	protected final HttpClientHelper getCurrentHttpClientHelper() {
		if (interfaceParametersProvider.isDummyMode()) {
			return dummyHttpClientHelper;
		} else {
			return httpClientHelper;
		}
	}

	public final void setDummyHttpClientHelper(HttpClientHelper dummyHttpClientHelper) {
		this.dummyHttpClientHelper = dummyHttpClientHelper;
	}

	public final void setHttpClientHelper(HttpClientHelper httpClientHelper) {
		this.httpClientHelper = httpClientHelper;
	}

	public final HttpClientHelper getHttpClientHelper() {
		return httpClientHelper;
	}

	public final HttpClientHelper getDummyHttpClientHelper() {
		return dummyHttpClientHelper;
	}

	protected final XmlRpcClientHelper getCurrentXmlRpcClientHelper() {
		if (interfaceParametersProvider.isDummyMode()) {
			return dummyXmlRpcClientHelper;
		} else {
			return xmlRpcClientHelper;
		}
	}

	public final void setDummyXmlRpcClientHelper(XmlRpcClientHelper dummyXmlRpcClientHelper) {
		this.dummyXmlRpcClientHelper = dummyXmlRpcClientHelper;
	}

	public final void setXmlRpcClientHelper(XmlRpcClientHelper xmlRpcClientHelper) {
		this.xmlRpcClientHelper = xmlRpcClientHelper;
	}

	public final XmlRpcClientHelper getXmlRpcClientHelper() {
		return xmlRpcClientHelper;
	}

	public final XmlRpcClientHelper getDummyXmlRpcClientHelper() {
		return dummyXmlRpcClientHelper;
	}

	public List<ParameterDefinition> listParameterDefinitions() {
		return new ArrayList<>();
	}

	public JaxwsSoapProxyHelper getJaxwsSoapProxyHelper() {
		return jaxwsSoapProxyHelper;
	}

	public void setJaxwsSoapProxyHelper(JaxwsSoapProxyHelper jaxwsSoapProxyHelper) {
		this.jaxwsSoapProxyHelper = jaxwsSoapProxyHelper;
	}

	public JaxRpcSoapProxyHelper getJaxRpcSoapProxyHelper() {
		return jaxRpcSoapProxyHelper;
	}

	public void setJaxRpcSoapProxyHelper(JaxRpcSoapProxyHelper jaxRpcSoapProxyHelper) {
		this.jaxRpcSoapProxyHelper = jaxRpcSoapProxyHelper;
	}

	public boolean isDemoPlugin() {
		return false;
	}

	public UniqueValuesHelper getUniqueValuesHelper() {
		return uniqueValuesHelper;
	}

	public void setUniqueValuesHelper(UniqueValuesHelper uniqueValuesHelper) {
		this.uniqueValuesHelper = uniqueValuesHelper;
	}

	/**
	 * Allows to store data downloadable from a link in the executions.
	 *
	 * @param lvl          : log level
	 * @param fileName     : name of the downloadable file.
	 * @param data         : content of the data.
	 * @param message      : Optional : may contain a message accompanying the file.
	 * @param translations : possible translations for the message, in the format en=First translation in EN,
	 *                     fr=Translation in FR, etc.
	 */
	public void logDownloadableData(LogLevel lvl, String fileName, String data, String message, String... translations) {
		internationalizedLogger.logDownloadableData(lvl, fileName, data, message, translations);
	}

}
