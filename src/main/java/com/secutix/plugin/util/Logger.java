package com.secutix.plugin.util;

public interface Logger {
	void info(String message);
	void debug(String message);
	void error(String message, Throwable e);
	void warn(String message);
}
