package com.secutix.plugin.util;

public class SSLConnectionData {

    private char[] storePassword;
    private char[] keyPassword;

    private byte[] keyMaterialFileContentB64Encoded;
    private byte[] trustMaterialFileContentB64Encoded;



    public char[] getStorePassword() {
        return storePassword;
    }

    public void setStorePassword(char[] storePassword) {
        this.storePassword = storePassword;
    }

    public char[] getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(char[] keyPassword) {
        this.keyPassword = keyPassword;
    }

    public byte[] getKeyMaterialFileContentB64Encoded() {
        return keyMaterialFileContentB64Encoded;
    }

    public void setKeyMaterialFileContentB64Encoded(byte[] keyMaterialFileContentB64Encoded) {
        this.keyMaterialFileContentB64Encoded = keyMaterialFileContentB64Encoded;
    }

    public byte[] getTrustMaterialFileContentB64Encoded() {
        return trustMaterialFileContentB64Encoded;
    }

    public void setTrustMaterialFileContentB64Encoded(byte[] trustMaterialFileContentB64Encoded) {
        this.trustMaterialFileContentB64Encoded = trustMaterialFileContentB64Encoded;
    }
}
