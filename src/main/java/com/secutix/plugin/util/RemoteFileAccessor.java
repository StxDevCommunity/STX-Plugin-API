package com.secutix.plugin.util;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by lkl on 24.08.2018.
 */
public interface RemoteFileAccessor {
    FileListResponse downloadFilesForProcessing(Predicate<String> fileNameMatcher, HandledFileActionPolicy handledFilePolicy, String handledFileRenamingSuffix, RemoteFileAccessorConnectionData connectionData);

    RemoteFileAccessorResponse uploadFiles(List<FileData> fileData, RemoteFileAccessorConnectionData connectionData);


    public static class RemoteFileAccessorResponse {
        private boolean success;
        private String errorMessage;


        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public RemoteFileAccessorResponse(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;

        }
    }

    public static class FileData {
        private String fileName;
        private byte[] fileContent;

        public FileData(String fileName, byte[] fileContent) {
            this.fileName = fileName;
            this.fileContent = fileContent;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getFileContent() {
            return fileContent;
        }
    }

    public static class FileListResponse extends RemoteFileAccessorResponse {
        private final List<FileData> fileData;


        public FileListResponse(boolean success, String errorMessage, List<FileData> fileData) {
            super(success, errorMessage);
            this.fileData = fileData;
        }

        public List<FileData> getFileData() {
            return fileData;
        }
    }


    public static enum HandledFileActionPolicy {
        DELETE, RENAME_AND_DELETE, LEAVE_IN_PLACE;
    }

    public static class RemoteFileAccessorConnectionData {
        private String remoteLocation;
        private String login;
        private String password;

        public RemoteFileAccessorConnectionData(String remoteLocation, String login, String password) {
            this.remoteLocation = remoteLocation;
            this.login = login;
            this.password = password;
        }

        public String getRemoteLocation() {
            return remoteLocation;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }
    }

}
