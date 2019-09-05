package com.secutix.plugin.util;

import java.util.List;

import com.google.common.base.Splitter;

public class ValidationHelper {
	private static final int MAX_FUNCTION_NAME_LENGTH = 60;
	private static final int MAX_FUNCTION_CODE_LENGTH = 12;
	private static final int MIN_LENGTH = 2;

	public static void validateString(String valueName, String value, int minLength, int maxLength)
			throws ValidationException {
		if (value == null) {
			throw new ValidationException("" + valueName + " cannot be null");
		}
		if (value.length() < minLength) {
			throw new ValidationException(
					"" + valueName + "=" + value + " must have a length of more than " + minLength
							+ " characters");
		}
		if (value.length() > maxLength) {
			throw new ValidationException(
					"" + valueName + "=" + value + " must  have a length of less than " + maxLength
							+ " characters");
		}
	}

	public static void validateAnnotation(StxFunction annotation) throws ValidationException {
		validateString("function code", annotation.functionCode(), MIN_LENGTH, MAX_FUNCTION_CODE_LENGTH);
		String functionNameElements = annotation.functionName();
		List<String> elements = Splitter.on(",").splitToList(functionNameElements);
		for (String elem : elements) {
			validateString("function name", elem, MIN_LENGTH, MAX_FUNCTION_NAME_LENGTH);
		}

		if (annotation.functionCode().equals("UNDEFINED")) {
			throw new ValidationException("function code must be defined !");
		}
	}

	public static boolean isSecutixFunctionAnnotationValid(StxFunction annotation) {
		if (annotation == null) {
			return false;
		}
		try {
			validateAnnotation(annotation);
			return true;
		} catch (final ValidationException e) {

			return false;
		}
	}
}
