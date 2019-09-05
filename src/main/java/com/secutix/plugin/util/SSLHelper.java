package com.secutix.plugin.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLHelper {
	public static void diableSslVerification() {

			try {
				// Create a trust manager that does not validate certificate chains
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] arg0,
							String arg1) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] arg0,
							String arg1) throws CertificateException {
					}
				} };

				// Install the all-trusting trust manager
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

				// Create all-trusting host name verifier
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};

				// Install the all-trusting host verifier
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}

	}
}
