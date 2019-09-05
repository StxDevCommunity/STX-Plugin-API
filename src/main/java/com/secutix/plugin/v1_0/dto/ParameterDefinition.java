package com.secutix.plugin.v1_0.dto;

import java.util.List;

public class ParameterDefinition {

	/**
	 * Must be a string without spaces.
	 */
	private String parameterCode;

	/**
	 * A string of format :
	 * languageCode1=Translation1,languageCode2=Translation2,languageCode3=Translation3.
	 * languageCode1 being the default translation if nothing else is defined.
	 * 
	 */
	private String parameterName;

	/**
	 * The list of possible values for the choice values parameter.
	 * If none is specified, parameter will behave like a String parameter.
	 */
	private List<String> choiceValues;

	public static enum ParameterType {
		/** The parameter is represented as a string */
		STRING,
		/** The parameter is represented as a boolean */
		BOOLEAN,
		/** The parameter is represented as an integer */
		INTEGER,
		/** The parameter is represented as a password */
		PASSWORD,
		/** The parameter is a string that can be chosen amont a list */
		CHOICE;
	}

	/**
	 * The type of parameter. Strings will be displayed as text box. Boolean as a check box.
	 * Integers as a text box.
	 */
	private ParameterType parameterType;


	private ParameterDefinition(String parameterCode, String parameterName,
			ParameterType parameterType, boolean mandatory, String defaultValue,
			String associatedFunctionCode) {
		super();
		this.parameterCode = parameterCode;
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
		this.associatedFunctionCode = associatedFunctionCode;
	}

	private ParameterDefinition(String parameterCode, String parameterName,
			ParameterType parameterType, boolean mandatory, String defaultValue) {
		super();
		this.parameterCode = parameterCode;
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
	}

	private boolean mandatory;

	private String defaultValue;

	/**
	 * Optional. If set, the parameter will be associated to a specific function.
	 */
	private String associatedFunctionCode;;

	public String getParameterCode() {
		return parameterCode;
	}

	public String getParameterName() {
		return parameterName;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}
	
	public static ParameterDefinition buildParameterDefinition(String parameterCode,
			ParameterType parameterType, String parameterName, boolean mandatory,
			String defaultValue) {
		return new ParameterDefinition(parameterCode, parameterName, parameterType, mandatory,
				defaultValue);
	}

	public static ParameterDefinition buildParameterDefinitionForFunction(
			String associatedFunctionCode, String parameterCode,
			ParameterType parameterType, String parameterName, boolean mandatory,
			String defaultValue) {
		return new ParameterDefinition(parameterCode, parameterName, parameterType, mandatory,
				defaultValue, associatedFunctionCode);
	}

	public String getAssociatedFunctionCode() {
		return associatedFunctionCode;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public List<String> getChoiceValues() {
		return choiceValues;
	}

	public void setChoiceValues(List<String> choiceValues) {
		this.choiceValues = choiceValues;
	}
}
