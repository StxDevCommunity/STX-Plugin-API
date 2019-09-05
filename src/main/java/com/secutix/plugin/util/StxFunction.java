package com.secutix.plugin.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StxFunction {

	String functionInternalCode() default "UNDEFINED";

	String functionName() default "";
	
	String functionCode() default "CodeX";
	
	boolean batchFunction() default false;
}
