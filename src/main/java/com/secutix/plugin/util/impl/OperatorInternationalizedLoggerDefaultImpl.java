package com.secutix.plugin.util.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import com.google.common.base.Strings;
import com.secutix.plugin.util.LogLevel;
import com.secutix.plugin.util.Logger;
import com.secutix.plugin.util.OperatorInternationalizedLogger;

public class OperatorInternationalizedLoggerDefaultImpl implements OperatorInternationalizedLogger {

	private final Logger logger;

	private String basePath;

	public OperatorInternationalizedLoggerDefaultImpl(Logger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void log(LogLevel level, String message, String... translations) {
		switch(level){

			case ERROR:
				logger.error(message,null);
				break;
			case WARNING:
				logger.warn(message);
				break;
			case INFO:
				logger.info(message);
				break;
			case DEBUG:
				logger.debug(message);
				break;
		}


	}

	@Override
	public void logDownloadableData(LogLevel level, String fileName, String data, String message,
			String... translations) {
		logger.info(level.name() + " - " + message);
		try {
			Path p;
			if (Strings.isNullOrEmpty(basePath)) {
				p = Files.createTempFile("storage", ".txt");
			} else {
				Path baseDir = Paths.get(basePath);
				Files.createDirectories(baseDir);
				p = Files.createTempFile(baseDir, "storage", ".txt");
			}
			if (!Strings.isNullOrEmpty(fileName)) {
				p = Paths.get(p.getParent().toString(), fileName);
			}
			Files.write(p, Collections.singletonList(data));
			logger.debug("Data dumped to file : " + p.toAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}

}
