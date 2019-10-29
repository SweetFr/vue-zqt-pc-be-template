package me.zhengjie.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cesar
 * @date 2018/5/24.
 */
public class HttpUtil {

    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

    private static PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
    private static RequestConfig requestConfig;

    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList(params.size());
            Iterator iterator = params.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)iterator.next();
                NameValuePair pair = new BasicNameValuePair((String)entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);

            log.info(response.toString());

            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return httpStr;
    }

    static {
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(7000);
        configBuilder.setSocketTimeout(7000);
        configBuilder.setConnectionRequestTimeout(7000);
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }


    /**
     * POST形式发送请求
     *
     * @param urlconn
     *            请求地址
     * @param params
     *            参数
     * @return
     * @throws IOException
     */
    public static String post(String urlconn, Map<String,String> params) {
        return invokePostOrPut(urlconn, buildParams(params), "POST");
    }

    public static String put(String urlconn, Map<String,String> params) {
        return invokePostOrPut(urlconn, buildParams(params), "PUT");
    }

    private static String invokePostOrPut(String urlconn, String params, String type) {
        String result = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        OutputStream out = null;

        log.info("请求地址： {}", urlconn);
        log.info("请求参数：  {}", params);
        log.info("请求方式：  {}", type);

        try {
            byte[] data = params.getBytes();
            URL url = new URL(urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            // 设置可以读取数据
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod(type);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.connect();
            out = urlConnection.getOutputStream();
            out.write(data);
            out.flush();

            int statusCode = urlConnection.getResponseCode();
            log.info("响应状态码：{}", statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                log.error("服务器错误");
            }
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = getStrFromInputSteam(in);

            log.info("响应内容：  {}", "\n" +result);

        } catch (ConnectException e) {
            e.printStackTrace();
            log.error("连接出错，请检查您的网络");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            log.error("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询出错");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * GET形式发送请求
     *
     * @param urlconn
     * @param params
     * @return
     * @throws IOException
     */
    public static String get(String urlconn, Map<String,String> params) {
        return invokeGetOrDelete(urlconn, buildParams(params), "GET");
    }

    public static String delete(String urlconn, Map<String,String> params) {
        return invokeGetOrDelete(urlconn, buildParams(params), "DELETE");
    }


    private static String invokeGetOrDelete(String urlconn, String params, String type)  {
        String result = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;

        urlconn = urlconn + "?" + params;

        log.info("请求地址： {}", urlconn);
        log.info("请求参数：  {}", params);
        log.info("请求方式：  {}", type);

        try {
            URL url = new URL(urlconn);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod(type);

            int statusCode = urlConnection.getResponseCode();
            log.info("响应状态码：{}", statusCode);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                log.error("服务器错误");
            }

            in = urlConnection.getInputStream();
            result = getStrFromInputSteam(in);

            log.info("响应内容：  {}", "\n" +result);
        } catch (ConnectException e) {
            e.printStackTrace();
            log.error("连接出错，请检查您的网络");
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            log.error("服务器响应超时...");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询出错");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public static String getStrFromInputSteam(InputStream in) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        // 最好在将字节流转换为字符流的时候 进行转码
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = bf.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    public static String buildParams(Map<String, String> params) {
        return buildParams(params, "UTF-8");
    }

    public static String buildParams(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
        try {
            if(!params.isEmpty()){
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
                }
                stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
