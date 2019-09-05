package com.secutix.plugin.util.impl;

import com.secutix.plugin.util.RemoteFileAccessor;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkl on 24.08.2018.
 */
public class TestRemoteFileAccessor {
    RemoteFileAccessor accessor = new RemoteFileAccessorDefaultImpl();

    @Test
    public void testUploadAndDownload(){

        String baseBath = Paths.get(System.getProperties().get("java.io.tmpdir").toString(),"testRemotrFileAccesor").toString();

        List<RemoteFileAccessor.FileData> files = new ArrayList<>();
        String content1 = "Hello\nHow are you ?";
        String content2 = "Fine, thank you\nAnd you ?\nAnd the children ?";

        Charset cs = Charset.forName("UTF-8");
        byte[] b1 = content1.getBytes(cs);
        byte[] b2 = content2.getBytes(cs);

        String fileName1 = "f1-" + System.currentTimeMillis() + ".txt";
        files.add(new RemoteFileAccessor.FileData(fileName1,b1));
        String fileName2 = "f2-" + System.currentTimeMillis() + ".txt";
        files.add(new RemoteFileAccessor.FileData(fileName2,b2));

        RemoteFileAccessor.RemoteFileAccessorConnectionData connectionDara=new RemoteFileAccessor.RemoteFileAccessorConnectionData(baseBath,null,null);
        RemoteFileAccessor.RemoteFileAccessorResponse res = accessor.uploadFiles(files, connectionDara);
        Assert.assertTrue(res.isSuccess());

        RemoteFileAccessor.FileListResponse res2 = accessor.downloadFilesForProcessing(s -> s.endsWith(".txt"), RemoteFileAccessor.HandledFileActionPolicy.DELETE, "", connectionDara);
        Assert.assertTrue(res2.isSuccess());
        Assert.assertEquals(2,res2.getFileData().size());
        Assert.assertEquals(fileName1,res2.getFileData().get(0).getFileName());
        Assert.assertEquals(fileName2,res2.getFileData().get(1).getFileName());
        Assert.assertEquals(content1, new String(  res2.getFileData().get(0).getFileContent(),cs));
        Assert.assertEquals(content2, new String(  res2.getFileData().get(1).getFileContent(),cs));
        //Assert.assertEquals(b2,res2.getFileData().get(1).getFileContent());


        RemoteFileAccessor.FileListResponse res3 = accessor.downloadFilesForProcessing(s -> s.endsWith(".txt"), RemoteFileAccessor.HandledFileActionPolicy.DELETE, "", connectionDara);
        Assert.assertTrue(res3.isSuccess());
        Assert.assertEquals(0,res3.getFileData().size());


    }
}
