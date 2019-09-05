package com.secutix.plugin.util.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Splitter;
import com.secutix.plugin.util.InterfaceParametersProvider;

public class InterfaceParametersProviderDefaultImpl implements InterfaceParametersProvider {

    private Map<String, String> parameters = new HashMap<>();

    private static class FileContent {
        final String fileName;
        final List<String> fileLines;

        public FileContent(String fileName, List<String> fileLines) {
            super();
            this.fileName = fileName;
            this.fileLines = fileLines;
        }

    }

    private Optional<FileContent> currentUploadedFile = Optional.empty();


    public InterfaceParametersProviderDefaultImpl() {

    }

    public InterfaceParametersProviderDefaultImpl(Map<String, String> parameters) {
        super();
        this.parameters = parameters;
    }

    @Override
    public Map<String, String> getInterfaceParameters() {
        return parameters;
    }

    @Override
    public String getUrl() {
        return parameters.get("url");
    }

    @Override
    public String getLogin() {

        return parameters.get("login");

    }

    @Override
    public String getPassword() {

        return parameters.get("password");
    }

    @Override
    public boolean isDummyMode() {
        return getBooleanParameter("dummyMode", true);
    }

    @Override
    public Locale getLocale() {
        return new Locale(readStringParameter("locale", "en"));
    }

    @Override
    public String getInstitutionCode() {

        return readStringParameter("institutionCode", "DEMO");
    }

    @Override
    public int readIntegerParameter(String paramKey, int defaultValue) {
        return Integer.valueOf(readStringParameter(paramKey, "" + defaultValue));
    }

    @Override
    public String readStringParameter(String paramKey, String defaultValue) {
        if (parameters.containsKey(paramKey)) {
            return parameters.get(paramKey);
        }
        return defaultValue;
    }

    @Override
    public List<String> readStringListParameter(String paramKey, List<String> defaultValue) {
        return Splitter.on(",").omitEmptyStrings().splitToList(readStringParameter(paramKey, ""));
    }

    @Override
    public long readLongParameter(String paramKey, long defaultValue) {

        return Long.valueOf(readStringParameter(paramKey, "" + defaultValue));
    }

    @Override
    public boolean getBooleanParameter(String paramKey, boolean defaultValue) {

        return Boolean.valueOf(readStringParameter(paramKey, "" + defaultValue));
    }

    @Override
    public String getOrganizationCode() {
        return readStringParameter("organizationCode", getInstitutionCode());
    }

    @Override
    public Long getOrganizationId() {
        return readLongParameter("organizationId", -1l);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Optional<List<String>> readUploadedFileContent(String charset) {
        if (isFileProvidedForBatch()) {
            return Optional.of(currentUploadedFile.get().fileLines);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> readUploadedFileName() {
        if (isFileProvidedForBatch()) {
            return Optional.of(currentUploadedFile.get().fileName);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean isFileProvidedForBatch() {
        return currentUploadedFile.isPresent();
    }

    public void uploadFile(String name, List<String> lines) {
        currentUploadedFile = Optional.ofNullable(new FileContent(name, lines));
    }

    @Override
    public Date readLastSuccessfullExecutionDateForCurrentBatch(boolean considerSchedule) {
        return new Date();
    }

}
