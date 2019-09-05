package com.secutix.plugin.em.v1_0;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.HttpEntity;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Assert;

import com.secutix.plugin.util.HttpClientHelper;
import com.secutix.plugin.util.InterfaceParametersProvider;
import com.secutix.plugin.util.LogLevel;
import com.secutix.plugin.util.Logger;
import com.secutix.plugin.util.OperatorInternationalizedLogger;
import com.secutix.plugin.util.StxPluginHttpResponse;
import com.secutix.plugin.util.TranslationHelper;
import com.secutix.plugin.v1_0.AbstractStxPlugin;

import javax.net.ssl.SSLContext;

public abstract class PluginInitializer {
	protected AbstractStxPlugin plugin;

	protected abstract AbstractStxPlugin initPlugin();

	public void initPluginWithBasicHelpers() {
		plugin.setInterfaceParametersProvider(initInterfaceParametersProvider());
		plugin.setInternationalizedLogger(initOperatorLogger());
		plugin.setLogger(new Logger() {

			@Override
			public void warn(String message) {
				System.out.println("WARN:" + message);

			}

			@Override
			public void info(String message) {
				System.out.println("INFO:" + message);

			}

			@Override
			public void error(String message, Throwable e) {
				System.out.println("ERROR:" + message);
				e.printStackTrace();

			}

			@Override
			public void debug(String message) {
				System.out.println("DEBUG:" + message);

			}
		});

		HttpClientHelper dummyHelper = new HttpClientHelper() {

			@Override
			public StxPluginHttpResponse doPost(String service, HttpEntity httpEntityToPost,
					Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No post defined");
			}

			@Override
			public StxPluginHttpResponse doPost(String service, Object obtectToPost,
					Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No post defined");
			}

			@Override
			public StxPluginHttpResponse doPut(String service, HttpEntity httpEntityToPost,
					Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No post defined");
			}

			@Override
			public StxPluginHttpResponse doPut(String service, Object objectToPost,
					Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No post defined");
			}

			@Override
			public StxPluginHttpResponse doPatch(String service, HttpEntity httpEntityToPost,
					Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No patch defined");
			}

			@Override
			public StxPluginHttpResponse doPatch(String service, Object objectToPost,
					Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No patch defined");
			}

			@Override
			public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No delete defined");
			}

			@Override
			public StxPluginHttpResponse doDelete(String service, Map<String, String> additionalHeaders,
					HttpClientContext context) {
				return new StxPluginHttpResponse(503, "No delete defined");
			}

			@Override
			public StxPluginHttpResponse doGet(String service, Map<String, String> additionalHeaders) {
				return new StxPluginHttpResponse(503, "No post defined");
			}

			@Override
			public void setDefaultConnectionTimeout(int connectionTimeout) {
				throw new NotImplementedException();
			}

			@Override
			public void setDefaultReadTimeout(int readTimeout) {
				throw new NotImplementedException();
			}

			@Override
			public void setSSLContext (SSLContext sslContext) {
				throw new NotImplementedException();
			}

			};

		if (plugin.getDummyHttpClientHelper() == null) {
			plugin.setDummyHttpClientHelper(dummyHelper);
		}
		if (plugin.getHttpClientHelper() == null) {
			plugin.setHttpClientHelper(dummyHelper);
		}

	}

	protected static class LogLineData {
		protected Date date;
		protected LogLevel level;
		protected String message;

		private LogLineData(Date date, LogLevel level, String message) {
			super();
			this.date = date;
			this.level = level;
			this.message = message;
		}

		public Date getDate() {
			return date;
		}

		public LogLevel getLevel() {
			return level;
		}

		public String getMessage() {
			return message;
		}

	}

	protected List<LogLineData> recordedLogLines = new ArrayList<>();

	private OperatorInternationalizedLogger initOperatorLogger() {
		return new OperatorInternationalizedLogger() {

			@Override
			public void log(LogLevel level, String message, String... translations) {
				String langCode = "en";
				String translated = TranslationHelper.translateMessage(langCode, message,
						translations);
				
				
				Assert.assertNotNull(translated);
				Assert.assertNotNull(level);
				recordedLogLines.add(new LogLineData(new Date(), level, translated));

			}

			@Override
			public void logDownloadableData(LogLevel lvl, String fileName, String data, String message, String... translations) {
				Path p = Paths.get(fileName);
				try {
					Files.write(p, Collections.singletonList(data));
				} catch (Exception e) {

					throw new RuntimeException(e.getMessage(), e);
				}

			}
		};
	}

	protected InterfaceParametersProvider initInterfaceParametersProvider() {

		return new InterfaceParametersProvider() {

			@Override
			public boolean isDummyMode() {
				return true;
			}

			@Override
			public String getUrl() {
				return "http://myservice.com";
			}

			@Override
			public String getPassword() {
				return "password";
			}

			@Override
			public String getLogin() {

				return "LOGIN";
			}

			@Override
			public Map<String, String> getInterfaceParameters() {
				return new HashMap<>();
			}

			@Override
			public Locale getLocale() {

				return Locale.getDefault();
			}

			@Override
			public String getInstitutionCode() {
				return "TOKYO";
			}

			@Override
			public int readIntegerParameter(String paramKey, int defaultValue) {
				// TODO Auto-generated method stub
				return defaultValue;
			}

			@Override
			public String readStringParameter(String paramKey, String defaultValue) {
				// TODO Auto-generated method stub
				return defaultValue;
			}

			@Override
			public long readLongParameter(String paramKey, long defaultValue) {
				// TODO Auto-generated method stub
				return defaultValue;
			}

			@Override
			public boolean getBooleanParameter(String paramKey, boolean defaultValue) {
				// TODO Auto-generated method stub
				return defaultValue;
			}

			@Override
			public List<String> readStringListParameter(String paramKey,
					List<String> defaultValue) {
				// TODO Auto-generated method stub
				return defaultValue;
			}

		};
	}
}
