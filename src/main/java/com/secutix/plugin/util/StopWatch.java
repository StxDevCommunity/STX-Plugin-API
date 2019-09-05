package com.secutix.plugin.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StopWatch {
	private Logger logger;

	public StopWatch(Logger logger) {
		super();
		this.logger = logger;
	}

	private Map<String, Date> startTimes = new HashMap<>();

	public void startWatch(String task) {
		if (startTimes.containsKey(task)) {
			logger.warn("Task already watched :" + task);
		}
		startTimes.put(task, new Date());
	}

	/**
	 * stops the watch
	 */
	public void stopWatch(String task) {
		if (!startTimes.containsKey(task)) {
			logger.warn("Task already is NOT watched :" + task);
		}
		Date d = new Date();

		Date start = startTimes.get(task);
		startTimes.remove(task);
		logger.info("[" + task + "] " + (d.getTime() - start.getTime()) + " ms");
	}

}
