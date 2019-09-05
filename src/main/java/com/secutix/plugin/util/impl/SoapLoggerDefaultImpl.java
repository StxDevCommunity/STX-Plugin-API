package com.secutix.plugin.util.impl;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.secutix.plugin.util.InterfaceParametersProvider;
import com.secutix.plugin.util.LogLevel;
import com.secutix.plugin.util.Logger;
import com.secutix.plugin.util.OperatorInternationalizedLogger;
import com.secutix.plugin.util.SoapLogger;

public class SoapLoggerDefaultImpl implements SoapLogger {

	private Logger logger;

	public SoapLoggerDefaultImpl(Logger logger, OperatorInternationalizedLogger operatorLogger,
			InterfaceParametersProvider interfaceParametersProvider) {
		super();
		this.logger = logger;
		this.operatorLogger = operatorLogger;
		this.interfaceParametersProvider = interfaceParametersProvider;
	}

	private OperatorInternationalizedLogger operatorLogger;

	private InterfaceParametersProvider interfaceParametersProvider;


	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		SOAPMessage msg = context.getMessage(); // Line 1
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			msg.saveChanges();
			msg.writeTo(baos);

			String messageDumped = baos.toString();
			logger.info("[soap] " + messageDumped);
			boolean dumpSoapMessages = interfaceParametersProvider
					.getBooleanParameter("dumpSoapMessages", false);
			if (dumpSoapMessages) {
				operatorLogger.logDownloadableData(LogLevel.DEBUG,
						"soapMessage-" + System.currentTimeMillis() + ".xml", messageDumped,
						"soap message");
			}

		} catch (Exception ex) {
			logger.warn("Error when loading message : " + ex.getMessage());
		}
		return true;
	}


}