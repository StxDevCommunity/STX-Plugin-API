package com.secutix.plugin.util;

import javax.xml.ws.handler.soap.SOAPMessageContext;

public interface SoapLogger {


	boolean handleMessage(SOAPMessageContext context);

}
