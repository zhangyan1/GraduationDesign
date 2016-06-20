package com.zhangyan.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class HttpClientUtil {
	private static Logger log = Logger.getLogger(HttpClientWrapper.class);

	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String SSL_DEFAULT_SCHEME = "https";
	private static final String DEFAULT_SCHEME = "http";
	private static final String PARAMETER_SEPARATOR = "&";
	private static final String NAME_VALUE_SEPARATOR = "=";
	private static final int CONNECT_TIMEOUT = 3 * 1000;
	private static final int RECEIVE_TIMEOUT = 10 * 1000;

	private static KeyStore KEY_STORE = null;
	private static boolean INIT_KEY_STORE = false;
	private static HttpClientWrapper httpClientWrapper = httpConnPool();

	public static class AllowHostnameVerifier extends AbstractVerifier {

		public final void verify(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
			String tmpHost = host.trim().toLowerCase();
			if (tmpHost.contains(".easyon.cn") || tmpHost.contains(".cnwisdom.com") || tmpHost.contains("10.255.0.72")) {
				return;
			}
			verify(host, cns, subjectAlts, false);
		}

		public final String toString() {
			return "ALLOW";
		}

	}

	public static class HttpClientWrapper {

		private boolean shutDown = true;
		private HttpClientConnectionManager connman = null;

		/** 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复 */
		private HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
			/** 自定义的恢复策略 */
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				/** 设置恢复策略，在发生异常时候将自动重试3次 */
				if (executionCount >= 3) {
					log.error("request times >= 3");
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					return false;
				}
				HttpRequest request = (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
				boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
				if (!idempotent) {
					return true;
				}
				return false;
			}
		};

		/** 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放 */
		private ResponseHandler<String> stringResponseHandler = new ResponseHandler<String>() {
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return null;
				}
				ContentType contentType = ContentType.getOrDefault(entity);
				if (contentType == null) {
					return EntityUtils.toString(entity, DEFAULT_CHARSET);
				}
				String charset = ContentType.get(entity).getParameter("encoding");
				return EntityUtils.toString(entity, charset);
			}
		};

		private ResponseHandler<byte[]> bytesResponseHandler = new ResponseHandler<byte[]>() {
			public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return null;
				}
				int statusCode = response.getStatusLine().getStatusCode();
				if (HttpStatus.SC_OK != statusCode) {
					return null;
				}
				return EntityUtils.toByteArray(entity);
			}
		};

		public HttpClientWrapper() {
			this.connman = new BasicHttpClientConnectionManager();
		}

		public HttpClientWrapper(HttpClientConnectionManager conman) {
			this.shutDown = false;
			this.connman = conman;
		}

		private HttpClientBuilder httpClient(String charset) {
			HttpClientBuilder httpclient = HttpClientBuilder.create();
			httpclient.setConnectionManager(connman);
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setExpectContinueEnabled(false).setSocketTimeout(RECEIVE_TIMEOUT).build();
			httpclient.setDefaultRequestConfig(requestConfig);
			String userAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";
			httpclient.setUserAgent(userAgent);
			httpclient.setRetryHandler(requestRetryHandler);
			if (charset == null || "".equals(charset)) {
				charset = DEFAULT_CHARSET;
			}
			ConnectionConfig cconfig = ConnectionConfig.custom().setCharset(Charsets.toCharset(charset)).build();
			httpclient.setDefaultConnectionConfig(cconfig);

			return httpclient;
		}

		/**
		 * 释放HttpClient连接
		 *
		 * <pre>
		 * @param httpRequestBase 请求对象
		 * @param httpclient client对象
		 * </pre>
		 */
		private void shutdown(HttpRequestBase httpRequestBase, CloseableHttpClient httpclient) {
			if (httpRequestBase != null) {
				httpRequestBase.releaseConnection();
			}
			if (httpclient != null && shutDown) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void shutdown() {
			connman.shutdown();
		}

		private String deCode(String content, String encoding) {
			try {
				String enc = encoding != null ? encoding : DEFAULT_CHARSET;
				return URLDecoder.decode(content, enc);
			} catch (UnsupportedEncodingException problem) {
				throw new RuntimeException("decode error！");
			}
		}

		private String enCode(String content, String encoding) {
			try {
				String enc = encoding != null ? encoding : DEFAULT_CHARSET;
				return URLEncoder.encode(content, enc);
			} catch (UnsupportedEncodingException problem) {
				throw new RuntimeException("encode error！");
			}
		}

		/**
		 * 将传入的键/值对参数转换为NameValuePair参数集
		 * 
		 * <pre>
		 * @param paramsMap 参数集, 键/值对
		 * @return NameValuePair参数集
		 * </pre>
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private List<NameValuePair> getNameValuePairs(Map paramsMap) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (paramsMap == null || paramsMap.size() == 0) {
				return params;
			}
			for (Object entry : paramsMap.entrySet()) {
				Map.Entry<Object, Object> map = (Map.Entry<Object, Object>) entry;
				if (map.getValue() instanceof Collection) {
					Collection values = (Collection) map.getValue();
					for (Object value : values) {
						params.add(new BasicNameValuePair((String) map.getKey(), (String) value));
					}
				} else {
					params.add(new BasicNameValuePair((String) map.getKey(), (String) map.getValue()));
				}
			}
			return params;
		}

		private List<BasicHeader> getBasicHeaders(Map<String, String> headsMap) {
			List<BasicHeader> params = new ArrayList<BasicHeader>();
			if (headsMap == null || headsMap.size() == 0) {
				return params;
			}
			for (Map.Entry<String, String> map : headsMap.entrySet()) {
				params.add(new BasicHeader(map.getKey(), map.getValue()));
			}
			return params;
		}

		public String getCookies(Map<String, String> cookies) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> cookie : cookies.entrySet()) {
				sb.append(cookie.getKey() + "=" + cookie.getValue() + ";");
			}
			return sb.toString();
		}

		public String mapToString(Map<String, List<String>> paramsMap, String encoding) {
			StringBuilder result = new StringBuilder();
			for (Map.Entry<String, List<String>> parameter : paramsMap.entrySet()) {
				String encodedName = enCode(parameter.getKey(), encoding);
				List<String> values = parameter.getValue();
				for (String value : values) {
					String encodedValue = value != null ? enCode(value, encoding) : "";
					if (result.length() > 0)
						result.append(PARAMETER_SEPARATOR);
					result.append(encodedName);
					result.append(NAME_VALUE_SEPARATOR);
					result.append(encodedValue);
				}
			}
			return result.toString();
		}

		public String mapToString(Map<String, List<String>> paramsMap) {
			return mapToString(paramsMap, DEFAULT_CHARSET);
		}

		public Map<String, List<String>> stringToMap(String params, String encoding) {
			Map<String, List<String>> result = new HashMap<String, List<String>>();
			String[] nameValues = params.split(PARAMETER_SEPARATOR);
			for (String param : nameValues) {
				String[] nameValue = param.split(NAME_VALUE_SEPARATOR);
				String name = deCode(nameValue[0], encoding);
				String value = null;
				if (nameValue.length > 1 && null != nameValue[1])
					value = deCode(nameValue[1], encoding);
				List<String> values = result.get(name);
				if (null == values) {
					values = new ArrayList<String>();
					result.put(name, values);
				}
				values.add(value);
			}
			return result;
		}

		public Map<String, List<String>> stringToMap(String params) {
			return stringToMap(params, DEFAULT_CHARSET);
		}

		/**
		 * Get方式提交
		 * 
		 * <pre>
		 * @param url 提交地址
		 * @return 响应消息
		 * </pre>
		 */
		public String get(String url) {
			return get(url, null);
		}

		/**
		 * Get方式提交
		 * 
		 * <pre>
		 * @param url 提交地址
		 * @param params 查询参数集, 键/值对
		 * @return 响应消息
		 * </pre>
		 */
		@SuppressWarnings("rawtypes")
		public String get(String url, Map params) {
			return get(url, params, DEFAULT_CHARSET);
		}

		@SuppressWarnings("rawtypes")
		public String get(String url, Map params, String charset) {
			return get(url, params, null, charset);
		}

		/**
		 * <pre>
		 * @param url 提交地址
		 * @param params 查询参数集, 键/值对
		 * @param heads 头消息
		 * @return 响应消息
		 * </pre>
		 */
		@SuppressWarnings("rawtypes")
		public String get(String url, Map params, Map<String, String> heads) {
			return get(url, params, heads, DEFAULT_CHARSET);
		}

		@SuppressWarnings("rawtypes")
		public String get(String url, Map params, Map<String, String> heads, String charset) {
			return get(url, params, heads, charset, stringResponseHandler);
		}

		@SuppressWarnings("rawtypes")
		public byte[] getBytes(String url, Map params, Map<String, String> heads, String charset) {
			return get(url, params, heads, charset, bytesResponseHandler);
		}

		/**
		 * Get方式提交
		 *
		 * <pre>
		 * @param url 提交地址
		 * @param params 查询参数集, 键/值对
		 * @param charset 参数提交编码集
		 * @return 响应消息
		 * </pre>
		 */
		@SuppressWarnings("rawtypes")
		public <T> T get(String url, Map params, Map<String, String> heads, String charset, ResponseHandler<? extends T> responseHandler) {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			List<NameValuePair> qparams = getNameValuePairs(params);
			if (qparams != null && qparams.size() > 0) {
				charset = (charset == null ? DEFAULT_CHARSET : charset);
				String formatParams = URLEncodedUtils.format(qparams, charset);
				url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url + "&" + formatParams);
			}
			HttpClientBuilder httpclient = httpClient(charset);
			HttpGet hg = new HttpGet(url);
			T response = null;
			CloseableHttpClient build = null;
			try {
				if (null != heads) {
					hg.setHeaders(getBasicHeaders(heads).toArray(new BasicHeader[0]));
				}
				build = httpclient.build();
				response = build.execute(hg, responseHandler);
			} catch (ClientProtocolException e) {
				throw new RuntimeException("ClientProtocolException", e);
			} catch (IOException e) {
				throw new RuntimeException("IOException", e);
			} finally {
				shutdown(hg, build);
			}
			return response;
		}

		/**
		 * Post方式提交
		 * 
		 * <pre>
		 * @param url 提交地址
		 * @param params 提交参数集, 键/值对
		 * @return 响应消息
		 * </pre>
		 */
		@SuppressWarnings("rawtypes")
		public String post(String url, Map params) {
			return post(url, params, DEFAULT_CHARSET);
		}

		public String post(String url, String params) {
			return post(url, params, DEFAULT_CHARSET);
		}

		@SuppressWarnings("rawtypes")
		public String post(String url, Map params, String charset) {
			return post(url, params, charset, stringResponseHandler);
		}

		public String post(String url, String params, String charset) {
			return post(url, params, charset, stringResponseHandler);
		}

		@SuppressWarnings("rawtypes")
		public byte[] postBytes(String url, Map params, String charset) {
			return post(url, params, charset, bytesResponseHandler);
		}

		/**
		 * Post方式提交
		 * 
		 * <pre>
		 * @param url 提交地址
		 * @param params 提交参数集, 键/值对
		 * @param charset 参数提交编码集
		 * @return 响应消息
		 * </pre>
		 */

		@SuppressWarnings("rawtypes")
		public <T> T post(String url, Map params, String charset, ResponseHandler<? extends T> responseHandler) {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			HttpClientBuilder httpclient = httpClient(charset);
			UrlEncodedFormEntity formEntity = null;
			try {
				if (StringUtils.isBlank(charset)) {
					formEntity = new UrlEncodedFormEntity(getNameValuePairs(params));
				} else {
					formEntity = new UrlEncodedFormEntity(getNameValuePairs(params), charset);
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("UnsupportedEncodingException", e);
			}
			HttpPost hp = new HttpPost(url);
			hp.setEntity(formEntity);
			T response = null;
			CloseableHttpClient build = null;
			try {
				build = httpclient.build();
				response = build.execute(hp, responseHandler);
			} catch (ClientProtocolException e) {
				throw new RuntimeException("client protocl exception", e);
			} catch (IOException e) {
				throw new RuntimeException("IO exception", e);
			} finally {
				shutdown(hp, build);
			}
			return response;
		}

		public <T> T post(String url, String params, String charset, ResponseHandler<? extends T> responseHandler) {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			/** 创建HttpClient实例 */
			HttpClientBuilder httpclient = httpClient(charset);
			ByteArrayEntity entity = null;
			try {
				entity = new ByteArrayEntity(params.getBytes(charset));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("unsupport encoding exception", e);
			}
			HttpPost hp = new HttpPost(url);
			hp.setEntity(entity);

			CloseableHttpClient build = null;
			/** 发送请求，得到响应 */
			T response = null;
			try {
				build = httpclient.build();
				response = build.execute(hp, responseHandler);
			} catch (ClientProtocolException e) {
				throw new RuntimeException("clientprotocol exception ", e);
			} catch (IOException e) {
				throw new RuntimeException("IO exception", e);
			} finally {
				shutdown(hp, build);
			}
			return response;
		}

	}

	public static HttpClientWrapper getHttpClientWrapper() {
		return new HttpClientWrapper();
	}

	public static HttpClientWrapper httpConnPool() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
		ConnectionSocketFactory plainsf = new PlainConnectionSocketFactory();
		registryBuilder.register(DEFAULT_SCHEME, plainsf);
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new AnyTrustStrategy()).build();
			LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			registryBuilder.register(SSL_DEFAULT_SCHEME, sslsf);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		PoolingHttpClientConnectionManager httpConnPool = new PoolingHttpClientConnectionManager(registry);
		httpConnPool.setMaxTotal(1000);
		httpConnPool.setDefaultMaxPerRoute(100);
		ConnectionConfig cconfig = ConnectionConfig.custom().setCharset(Charsets.toCharset(DEFAULT_CHARSET)).build();
		httpConnPool.setDefaultConnectionConfig(cconfig);

		return new HttpClientWrapper(httpConnPool);
	}

	public static String getCookies(Map<String, String> cookies) {
		return httpClientWrapper.getCookies(cookies);
	}

	public static String mapToString(Map<String, List<String>> paramsMap, String encoding) {
		return httpClientWrapper.mapToString(paramsMap, encoding);
	}

	public static String mapToString(Map<String, List<String>> paramsMap) {
		return httpClientWrapper.mapToString(paramsMap);
	}

	public static Map<String, List<String>> stringToMap(String params, String encoding) {
		return httpClientWrapper.stringToMap(params, encoding);
	}

	public static Map<String, List<String>> stringToMap(String params) {
		return httpClientWrapper.stringToMap(params);
	}

	/**
	 * 从给定的路径中加载此 KeyStore
	 *
	 * <pre>
	 * @param url keystore URL路径
	 * @param password keystore访问密钥
	 * @return keystore 对象
	 * </pre>
	 */
	private static KeyStore createKeyStore(final String url, final String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if (url == null) {
			throw new IllegalArgumentException("Keystore url may not be null");
		}
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream is = null;
		try {
			is = new ClassPathResource(url).getInputStream();
			keystore.load(is, password != null ? password.toCharArray() : null);
		} finally {
			if (is != null) {
				is.close();
				is = null;
			}
		}
		return keystore;
	}

	public void initKeyStore() {
		try {
			if (INIT_KEY_STORE) {
				return;
			}
			if (null == KEY_STORE) {
				KEY_STORE = createKeyStore("/public.keystore", "cnwisdomssl");
			}
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(KEY_STORE);
			TrustManager[] trustmanagers = tmf.getTrustManagers();
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, trustmanagers, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new AllowHostnameVerifier());
			INIT_KEY_STORE = true;
		} catch (Exception e) {
			log.error("load keystore failure");
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Get方式提交
	 * 
	 * <pre>
	 * @param url 提交地址
	 * @return 响应消息
	 * </pre>
	 */
	public static String get(String url) {
		return httpClientWrapper.get(url);
	}

	/**
	 * Get方式提交
	 * 
	 * <pre>
	 * @param url 提交地址
	 * @param params  查询参数集, 键/值对
	 * @return 响应消息
	 * </pre>
	 */
	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params) {
		return httpClientWrapper.get(url, params);
	}

	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params, String charset) {
		return httpClientWrapper.get(url, params, charset);
	}

	/**
	 * <pre>
	 * @param url 提交地址
	 * @param params 查询参数集, 键/值对
	 * @param heads 头消息
	 * @return 响应消息
	 * </pre>
	 */
	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params, Map<String, String> heads) {
		return httpClientWrapper.get(url, params, heads);
	}

	@SuppressWarnings("rawtypes")
	public static String get(String url, Map params, Map<String, String> heads, String charset) {
		return httpClientWrapper.get(url, params, heads, charset);
	}

	public static byte[] getBytes(String url) {
		return httpClientWrapper.getBytes(url, null, null, DEFAULT_CHARSET);
	}

	/**
	 * Get方式提交
	 *
	 * <pre>
	 * @param url 提交地址
	 * @param params 查询参数集, 键/值对
	 * @param charset 参数提交编码集
	 * @return 响应消息
	 * </pre>
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T get(String url, Map params, Map<String, String> heads, String charset, ResponseHandler<? extends T> responseHandler) {
		return httpClientWrapper.get(url, params, heads, charset, responseHandler);
	}

	/**
	 * Post方式提交
	 * 
	 * <pre>
	 * @param url 提交地址
	 * @param params 提交参数集, 键/值对
	 * @return 响应消息
	 * </pre>
	 */
	@SuppressWarnings("rawtypes")
	public static String post(String url, Map params) {
		return httpClientWrapper.post(url, params);
	}

	public static String post(String url, String params) {
		return httpClientWrapper.post(url, params);
	}

	@SuppressWarnings("rawtypes")
	public static String post(String url, Map params, String charset) {
		return httpClientWrapper.post(url, params, charset);
	}

	public static String post(String url, String params, String charset) {
		return httpClientWrapper.post(url, params, charset);
	}

	@SuppressWarnings("rawtypes")
	public static byte[] postBytes(String url, Map params) {
		return httpClientWrapper.postBytes(url, params, DEFAULT_CHARSET);
	}

	/**
	 * Post方式提交
	 *
	 * <pre>
	 * @param url 提交地址
	 * @param params 提交参数集, 键/值对
	 * @param charset 参数提交编码集
	 * @return 响应消息
	 * </pre>
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T post(String url, Map params, String charset, ResponseHandler<? extends T> responseHandler) {
		return httpClientWrapper.post(url, params, charset, responseHandler);
	}

	public static <T> T post(String url, String params, String charset, ResponseHandler<? extends T> responseHandler) {
		return httpClientWrapper.post(url, params, charset, responseHandler);
	}

	static class AnyTrustStrategy implements TrustStrategy {
		@Override
		public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			return true;
		}
	}

}