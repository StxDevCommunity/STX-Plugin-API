package com.secutix.plugin.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SalesContextAware {
	String salesChannelParameterCode() ;
	String pointOfSalesParameterCode() ;
}
