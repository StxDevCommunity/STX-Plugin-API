package com.secutix.plugin.util.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.secutix.plugin.util.RemoteFileAccessor;

/**
 * Created by lkl on 24.08.2018.
 * This basic default implementation looks inside a local folder.
 */
public class RemoteFileAccessorDefaultImpl implements RemoteFileAccessor {

    @Override
    public FileListResponse downloadFilesForProcessing(Predicate<String> fileNameMatcher, HandledFileActionPolicy handledFilePolicy, String handledFileRenamingSuffix, RemoteFileAccessorConnectionData connectionData) {
        String localPath = connectionData.getRemoteLocation();
        if (Strings.isNullOrEmpty(localPath)) {
            return new FileListResponse(false, "Please provide a remote location", null);
        }
        Path basePath = Paths.get(localPath);
        if (!Files.exists(basePath)) {
            return new FileListResponse(false, "Please provide an existing path remote location. " + basePath.toString() + " does not exist.", null);
        }
        try {
            List<FileData> downloadedFiles = new ArrayList<>();
            List<Path> matchingPaths = Files.list(basePath).filter(p -> Files.isRegularFile(p)).filter(p -> fileNameMatcher.test(p.getFileName().toString())).collect(Collectors.toList());
            for (Path p : matchingPaths) {
                handleFile(p, downloadedFiles, handledFilePolicy, handledFileRenamingSuffix);
            }
            Collections.sort(downloadedFiles,(f1,f2)-> f1.getFileName().compareTo(f2.getFileName()));

            return new FileListResponse(true, "Here are the files.", downloadedFiles);

        } catch (Exception e) {
            return new FileListResponse(false, "Technical error while handling files:" + e.getMessage(), null);
        }

    }

    private void handleFile(Path p, List<FileData> downloadedFiles, HandledFileActionPolicy handledFilePolicy, String handledFileRenamingSuffix) throws IOException {
        byte[] fileContent = Files.readAllBytes(p);
        String fileName = p.getFileName().toString();
        FileData fd = new FileData(fileName,fileContent);
        downloadedFiles.add(fd);

        switch (handledFilePolicy){


            case DELETE:
                Files.delete(p);
                break;
            case RENAME_AND_DELETE:
                Files.copy(p,p.getParent().resolve(fileName + "." + handledFileRenamingSuffix));
                Files.delete(p);
                break;
            case LEAVE_IN_PLACE:
                break;
        }

    }

    @Override
    public RemoteFileAccessorResponse uploadFiles(List<FileData> fileData, RemoteFileAccessorConnectionData connectionData) {
        String localPath = connectionData.getRemoteLocation();
        if (Strings.isNullOrEmpty(localPath)) {
            return new RemoteFileAccessorResponse(false, "Please provide a remote location");
        }
        Path basePath = Paths.get(localPath);
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
            for(FileData fd : fileData){
                Path child = basePath.resolve(fd.getFileName());
                Files.write(child,fd.getFileContent());
            }
            return new RemoteFileAccessorResponse(true, "Here are the files.");

        } catch (Exception e) {
            return new RemoteFileAccessorResponse(false, "Technical error while handling files:" + e.getMessage());
        }
    }
}
