package com.secutix.plugin.util;

public class Try<T> {
	private T result;
	private String errorCode;
	private String errorMessage;

	private Try(T result, String errorCode, String errorMessage) {
		super();
		this.result = result;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public static <T> Try<T> of(T obj) {
		if (obj == null) {
			return new Try<T>(null, "error.null", "");
		}

		return new Try<T>(obj, "success", "");
	}

	public static <T> Try<T> failure(String errorCode, String errorMessage) {
		return new Try<T>(null, errorCode, errorMessage);
	}

	public boolean isSuccess() {
		return "success".equals(errorCode);
	}

	public T getResult() {
		return result;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getErrorCode());
		if (getErrorMessage() != null && getErrorMessage().length() > 0) {
			sb.append(" / ").append(getErrorMessage());
		}

		return sb.toString();
	}

}