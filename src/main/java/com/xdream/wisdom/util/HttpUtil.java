package com.xdream.wisdom.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import com.xdream.wisdom.config.Parameters;

import javax.net.ssl.*;


public class HttpUtil {

	public static PoolingHttpClientConnectionManager connMgr;
	private static String ENCODE = "UTF-8";
	public static RequestConfig requestConfig;
	public static final int MAX_TIMEOUT = 10000;
	/**
	 * 通用http post请求
	 * @param url 请求URL地址
	 * @param entity 发送数据实体
	 * @param method 请求类型
	 * @throws Exception
	 */

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}


	public static ThirdResponseObj httpToServerPost(String urladd,String entity,String type)throws Exception{
		HttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(urladd);
		StringEntity entityStr= new StringEntity(entity,HTTP.UTF_8);
		entityStr.setContentType(type);
		postMethod.setEntity(entityStr);
		//HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(postMethod);
        
        int statusCode = resp.getStatusLine().getStatusCode();
        
        ThirdResponseObj responseObj = new ThirdResponseObj();
        if (200 == statusCode) {
        	responseObj.setCode("success");
        	String str = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
        	responseObj.setResponseEntity(str);
        }else{
        	responseObj.setCode(statusCode+"");
        }
        
        return responseObj;
	}
	/**
	 * 通用http get请求
	 * @param url 请求URL地址
	 * @param entity 发送数据实体
	 * @param method 请求类型
	 * @throws Exception
	 */
	public static ThirdResponseObj httpToServerGet(String urladd,String entity,String type)throws Exception{
		HttpClient httpClient = new SSLClient();
		if(type.equals(Parameters.JSON_TYPE)) {
			entity=URLEncoder.encode(entity,"utf-8");
		}
		String params = urladd+"?"+entity;
		HttpGet getMethod = new HttpGet(params);
		StringEntity entityStr= new StringEntity(entity,HTTP.UTF_8);
		entityStr.setContentType(type);
		//HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(getMethod);
        
        int statusCode = resp.getStatusLine().getStatusCode();
        
        ThirdResponseObj responseObj = new ThirdResponseObj();
        if (200 == statusCode) {
        	responseObj.setCode("success");
        	String str = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
        	responseObj.setResponseEntity(str);
        }else{
        	responseObj.setCode(statusCode+"");
        }
        
        return responseObj;
	}


	public static ThirdResponseObj doGet(String url,String charest) {
		return doGet(url, new HashMap<String, Object>(),charest,null,null);
	}

	public static ThirdResponseObj doGet(String url, Map<String, Object> params, String charset, Map<String, String> headerMap, Header[] headers) {
		String apiUrl = url;
		StringBuffer param = new StringBuffer();

		ThirdResponseObj responseObj = new ThirdResponseObj();
		int i = 0;
		for (String key : params.keySet()) {
			if (i == 0)
				param.append("?");
			else
				param.append("&");
			param.append(key).append("=").append(params.get(key));
			i++;
		}
		apiUrl += param;
//        log.debug("http请求参数:"+apiUrl);
		String result = null;
		HttpClient httpclient = url.startsWith("https") ? HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build(): HttpClients.createDefault();

		try {
			HttpGet httpPost = new HttpGet(apiUrl);
			if(headerMap != null){
				for(Map.Entry<String , String> entry : headerMap.entrySet()){
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			if(headers != null)
				httpPost.setHeaders(headers);

			HttpResponse response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity entity = response.getEntity();
//           log.debug("http请求状态:"+statusCode);


			if (200 == statusCode) {
				responseObj.setCode("success");
				String str = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				responseObj.setResponseEntity(str);
			}else{
				responseObj.setCode(statusCode+"");
			}

//			if (entity != null) {
//				InputStream instream = entity.getContent();
//				result = IOUtils.toString(instream, charset);
////                log.debug("http请求结果:"+result);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseObj;
	}


	public static ThirdResponseObj http2Nvp(String url,Map<String,String>map,String encodeType) throws Exception{

		List<BasicNameValuePair> nvps=new ArrayList<BasicNameValuePair>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
		}

		HttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(url);

//		  //链接超时
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
//        //读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 300000);

		postMethod.setEntity(new UrlEncodedFormEntity(nvps, encodeType));
		//HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(postMethod);

		int statusCode = resp.getStatusLine().getStatusCode();
		System.out.println("TTTTT");
		ThirdResponseObj responseObj = new ThirdResponseObj();
		if (200 == statusCode) {
			responseObj.setCode("success");
			String str = EntityUtils.toString(resp.getEntity(), encodeType);
			responseObj.setResponseEntity(str);
		}else{
			responseObj.setCode(statusCode+"");
		}

		return responseObj;
	}


	public static ThirdResponseObj http2Nvp(String url,List<BasicNameValuePair> nvps) throws Exception{
		HttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(url);
//		  //链接超时
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
//        //读取超时 设置为30秒
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

		postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		//HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(postMethod);

		int statusCode = resp.getStatusLine().getStatusCode();

		ThirdResponseObj responseObj = new ThirdResponseObj();
		if (200 == statusCode) {
			responseObj.setCode("success");
			String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
			responseObj.setResponseEntity(str);
		}else{
			responseObj.setCode(statusCode+"");
		}

		return responseObj;
	}


	public static String httpsGet(String protocol,String url,String port, String api, Map<String, String> params) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		String result = "";
		BufferedReader in = null;
		String urlStr = protocol +"://"+ url +":"+ port + api + "?" + getParamStr(params);
		System.out.println("get请求的URL为：" + urlStr);
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
				new java.security.SecureRandom());
		URL realUrl = new URL(urlStr);
		// 打开和URL之间的连接
		HttpsURLConnection connection = (HttpsURLConnection) realUrl
				.openConnection();
		// 设置https相关属性
		connection.setSSLSocketFactory(sc.getSocketFactory());
		connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");


		// 建立实际的连接
		connection.connect();

		// 定义 BufferedReader输入流来读取URL的响应
		in = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), ENCODE));
		String line;
		while ((line = in.readLine()) != null) {
			result += line;
		}
		System.out.println("获取的结果为：" + result);
		if (in != null) {
			in.close();
		}
		return result;

	}


	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}


	private static String getParamStr(Map<String, String> keyValueParams) {
		String paramStr = "";
		// 获取所有响应头字段
		Map<String, String> params = keyValueParams;
		// 获取参数列表组成参数字符串
		for (String key : params.keySet()) {
			paramStr += key + "=" + params.get(key) + "&";
		}
		// 去除最后一个"&"
		paramStr = paramStr.substring(0, paramStr.length() - 1);
		return paramStr;
	}

	/**
	 * 创建SSL安全连接池
	 *
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			});
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return sslsf;
	}

	/**
	 * 通用http get请求
	 * @param url 请求URL地址
	 * @param entity 发送数据实体
	 * @param method 请求类型
	 * @throws Exception
	 */
	public static ThirdResponseObj httpToServerGet(String urladd,HashMap<String,String> headers,String entity,String type)throws Exception{
		HttpClient httpClient = new SSLClient();
		if(type.equals(Parameters.JSON_TYPE)) {
			entity=URLEncoder.encode(entity,"utf-8");
		}
		RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setSocketTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();
		String params = urladd+"?"+entity;
		HttpGet getMethod = new HttpGet(params);
		Iterator<Map.Entry<String, String>> iter= headers.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, String> next = iter.next();
			getMethod.setHeader(next.getKey(),next.getValue());
		}
		getMethod.setConfig(config);
		StringEntity entityStr= new StringEntity(entity,HTTP.UTF_8);
		entityStr.setContentType(type);
		//HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(getMethod);
        
        int statusCode = resp.getStatusLine().getStatusCode();
        
        ThirdResponseObj responseObj = new ThirdResponseObj();
        if (200 == statusCode) {
        	responseObj.setCode("success");
        	String str = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
        	responseObj.setResponseEntity(str);
        }else{
        	responseObj.setCode(statusCode+"");
        }
        
        return responseObj;
	}


	private static final ContentType CONTENT_TYPE_APPLICATION_FORM_URLENCODED = ContentType.create("application/x-www-form-urlencoded", "UTF-8");
	private static final String REG_EXP_CONTENT_TYPE = "([\\w*?|\\*?]/[\\w*?|\\*?])(;\\s*)(charset=(\\w+))";
	private static Pattern PATTERN_CONTENT_TYPE = Pattern.compile(REG_EXP_CONTENT_TYPE);

	public static String httpThree(String url,Map<String,byte[]>photoMap,String filename,Map<String, String>map) throws Exception{
		HttpClient httpClient = new SSLClient();

		HttpPost postMethod = new HttpPost(url);

		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setCharset(CharsetUtils.get("UTF-8"));
		//普通字符
		for (Map.Entry<String, String> entry : map.entrySet()) {
			try {
				StringBody stringBody = new StringBody(entry.getValue(), CONTENT_TYPE_APPLICATION_FORM_URLENCODED);
				entityBuilder.addPart(entry.getKey(), stringBody);
			} catch (Exception e) {
				return null;
			}

		}
		if (photoMap.size()>=1) {
			for (Map.Entry<String, byte[]> entry : photoMap.entrySet()) {
				String key = entry.getKey();
				byte[] value = entry.getValue();
				ByteArrayBody byteArrayBody = new ByteArrayBody(value, filename);
				entityBuilder.addPart(key, byteArrayBody);
			}
		}

		postMethod.setEntity(entityBuilder.build());

		HttpResponse response = httpClient.execute(postMethod);

		boolean consumed = false;

		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		HttpEntity entity = response.getEntity();

		try {
			if (statusCode == HttpStatus.SC_OK) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				entity.writeTo(buffer);

				consumed = true;

				String contentType = response.getFirstHeader("Content-Type").getValue();
				Matcher matcher = PATTERN_CONTENT_TYPE.matcher(contentType);
				String encoding;
				if (matcher.matches()) {
					encoding = matcher.group(4);
				} else {
					encoding = "UTF-8";
				}
				String bodyContent = new String(buffer.toByteArray(), encoding);
				//	log.info("\nURL: " + request.getURI() + "\nBodyContent:" + bodyContent);
				return bodyContent;
			} else {
				entity.getContent().close(); // i.e. EUs.cons(e);
				consumed = true;
				System.out.println("要素认证three连接异常:"+statusCode);
				return null;
			}
		} finally {
			if(!consumed){
				entity.getContent().close();
			}
		}

	}


	public static ThirdResponseObj http2Se(String url,StringEntity entity,String encodingType) throws Exception{

		HttpClient httpClient = new SSLClient();
		HttpPost postMethod = new HttpPost(url);

		postMethod.setEntity(entity);
		//HttpResponse resp = null;
		HttpResponse resp = httpClient.execute(postMethod);

		int statusCode = resp.getStatusLine().getStatusCode();

		ThirdResponseObj responseObj = new ThirdResponseObj();
		if (200 == statusCode) {
			responseObj.setCode("success");
			String str = EntityUtils.toString(resp.getEntity(), encodingType);
			responseObj.setResponseEntity(str);
		}else{
			responseObj.setCode(statusCode+"");
		}

		return responseObj;
	}

	private static SslContextUtils sslContextUtils = new SslContextUtils();
	public static String sendXMLDataByPost(String postUrl, String xmlData) {
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(postUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

			if (httpConn instanceof HttpsURLConnection) {
				sslContextUtils.initHttpsConnect((HttpsURLConnection) httpConn);
			}

			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestProperty("Content-type", "text/xml");
			httpConn.setConnectTimeout(60000);
			httpConn.setReadTimeout(60000);
			// 发送请求
			httpConn.getOutputStream().write(xmlData.getBytes("UTF-8"));
			httpConn.getOutputStream().flush();
			httpConn.getOutputStream().close();
			// 获取输入流
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			char[] buf = new char[1024];
			int length = 0;
			while ((length = reader.read(buf)) > 0) {
				sb.append(buf, 0, length);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// } catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
		}
		return sb.toString();
	}


}
