package com.secutix.plugin.util;

import org.apache.commons.io.FileUtils;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

public class SSLContextHelper {

    public static SSLContext buildSSLContext(SSLConnectionData connectionData) {
        try {
            SSLContextBuilder builder;

            SSLContext sslContext = null;

            //TODO this could be used as 'ignoreSslVerification' alternative - when no key or/and trust material present - return defauls SSLContext
            //need to be tested
            if (connectionData.getKeyMaterialFileContentB64Encoded() == null ||
                    connectionData.getTrustMaterialFileContentB64Encoded() == null) {

                return SSLContext.getDefault();

            }

            builder = SSLContexts.custom();

            File keyMaterialFile = null;
            File trustMaterialFile = null;

            keyMaterialFile = File.createTempFile("tmpKMF", "p12");
            keyMaterialFile.deleteOnExit();
            FileUtils.writeByteArrayToFile(keyMaterialFile, Base64.getDecoder().decode(connectionData.getKeyMaterialFileContentB64Encoded()));

            trustMaterialFile = File.createTempFile("tmpTMF", "jks");
            trustMaterialFile.deleteOnExit();
            FileUtils.writeByteArrayToFile(trustMaterialFile, Base64.getDecoder().decode(connectionData.getTrustMaterialFileContentB64Encoded()));


            sslContext = SSLContexts.custom()
                    .loadKeyMaterial(keyMaterialFile, connectionData.getStorePassword(),
                            connectionData.getKeyPassword())
                    .loadTrustMaterial(trustMaterialFile)
                    .build();
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
