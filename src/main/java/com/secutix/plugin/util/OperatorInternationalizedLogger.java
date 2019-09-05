package com.secutix.plugin.util;

public interface OperatorInternationalizedLogger {

	void log(LogLevel level, String message, String... translations);

	void logDownloadableData(LogLevel lvl, String fileName, String data, String message, String... translations);
}
