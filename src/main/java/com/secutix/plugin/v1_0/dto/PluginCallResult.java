package com.secutix.plugin.v1_0.dto;

public class PluginCallResult {
	private static final String SUCCESS = "success";
	private static final String WARNING = "warning";
	private static final String FATAL = "fatal";
	protected String statusCode;
	protected String statusMessage;

	public PluginCallResult() {
		super();
	}

	public PluginCallResult(String statusCode, String statusMessage) {
		super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public boolean isSuccess() {
		return SUCCESS.equals(statusCode);
	}

	public boolean isWarning() {
		return WARNING.equals(statusCode);
	}
	
	public boolean isFatalFailure() {
		return FATAL.equals(statusCode);
	}

	public boolean isFailure() {
		return !isWarning() && !isSuccess();
	}
	
	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public static <T extends PluginCallResult> T addSucess(T result, String message) {
		result.statusCode = SUCCESS;
		result.statusMessage = message;
		return result;
	}

	public static <T extends PluginCallResult> T addWarning(T result, String message) {
		result.statusCode = WARNING;
		result.statusMessage = message;
		return result;
	}

	public static <T extends PluginCallResult> T addFailure(T result, String code, String message) {
		if (code == null) {
			throw new IllegalArgumentException("code must not be null");
		}
		if (SUCCESS.equals(code)) {
			throw new IllegalArgumentException("You are not building a failure, but a success !");
		}
		if (WARNING.equals(code)) {
			throw new IllegalArgumentException("You are not building a failure, but a warning !");
		}
		result.statusCode = code;
		result.statusMessage = message;
		return result;
	}
	
	public static <T extends PluginCallResult> T addFatalFailure(final T result, final String message) {
		return addFailure(result, FATAL, message);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PluginCallResult [statusCode=").append(statusCode).append(", statusMessage=")
				.append(statusMessage).append("]");
		return builder.toString();
	}

}
