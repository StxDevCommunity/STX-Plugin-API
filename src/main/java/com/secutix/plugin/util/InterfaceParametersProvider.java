package com.secutix.plugin.util;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public interface InterfaceParametersProvider {

	@Deprecated
	public Map<String, String> getInterfaceParameters();

	public String getUrl();

	public String getLogin();

	public String getPassword();

	public boolean isDummyMode();

	public Locale getLocale();

	public String getInstitutionCode();

	int readIntegerParameter(String paramKey, int defaultValue);

	String readStringParameter(String paramKey, String defaultValue);

	List<String> readStringListParameter(String paramKey, List<String> defaultValue);

	long readLongParameter(String paramKey, long defaultValue);

	boolean getBooleanParameter(String paramKey, boolean defaultValue);

	default Optional<List<String>> readUploadedFileContent(String charset) {
		return Optional.empty();
	}

	default Optional<String> readUploadedFileName() {
		return Optional.empty();
	}

	default String getOrganizationCode() {
		return null;
	}

	default Long getOrganizationId() {
		return null;
	}

	default boolean isFileProvidedForBatch() {
		return false;
	}

	default void storeIntegerParameter(String paramKey, int value) {
		throw new IllegalStateException("Not implemented");
	}

	default void storeStringParameter(String paramKey, String value) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Returns the last successfull execution date for the current batch. This method works only
	 * when called from a plugin method which is a batch function.
	 * @param considerSchedule if set to true, the date of last execution will only consider the current schedule. Recommandation: set to true if you do not know what to put.
	 * @return date of last execution for current batch.
	 */
	default Date readLastSuccessfullExecutionDateForCurrentBatch(boolean considerSchedule) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * Returns the code of the current logged in operator in SecuTix.
	 * @return
	 */
	default String getCurrentOperator (){
		throw new IllegalStateException("Not implemented");
	}
}
