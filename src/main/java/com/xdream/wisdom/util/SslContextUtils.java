package com.xdream.wisdom.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SslContextUtils {
	
	private TrustManager trustAllManager;
	SSLContext sslcontext;
	HostnameVerifier allHostsValid;

	public SslContextUtils() {
		initContext();
	}

	private void initContext() {
		trustAllManager = new X509TrustManager() {
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] arg0, String arg1) {
			}
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] arg0, String arg1) {
			}
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { trustAllManager }, null);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		// Create all-trusting host name verifier
		allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	}

	public void initHttpsConnect(HttpsURLConnection httpsConn) {
		httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
		httpsConn.setHostnameVerifier(allHostsValid);
	}
	
}
