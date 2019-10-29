package me.zhengjie.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.zhengjie.vo.ZqtongResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;


/**
 * @Description : HttpClient工具类
 *
 * @Author : maodw
 * @Date : 2019/2/26
 */
public class HttpClientUtil {
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

   static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    public static ZqtongResult doGet(String url, Map<String, Object> headers) {
        ZqtongResult result = new ZqtongResult();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }

            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(requestConfig);

            // 设置请求头
            if (null != headers && !headers.isEmpty()) {
                List<Header> headerList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    Header header = new BasicHeader(entry.getKey(), entry.getValue().toString());
                    headerList.add(header);
                }
                httpGet.setHeaders(headerList.toArray(new Header[headers.size()]));
            }

            // 请求
            response = httpClient.execute(httpGet);
            result.setStatus(response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            result.setData(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(500);
            result.setData(e.getMessage());
        } finally {
            // 关闭连接,释放资源
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static ZqtongResult doPost(String url, String bodyJson, Map<String, Object> headers) {
        ZqtongResult result = new ZqtongResult();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);

            // 设置请求头
            if (null != headers && !headers.isEmpty()) {
                List<Header> headerList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    Header header = new BasicHeader(entry.getKey(), entry.getValue().toString());
                    headerList.add(header);
                }
                httpPost.setHeaders(headerList.toArray(new Header[headers.size()]));
            }

            // 设置body
            if (StringUtils.isNotEmpty(bodyJson)) {
                StringEntity stringEntity = new StringEntity(bodyJson, "UTF-8");
                stringEntity.setContentEncoding("UTF-8");
                stringEntity.setContentType("application/json");
                httpPost.setEntity(stringEntity);
            }

            // 请求
            response = httpClient.execute(httpPost);
            result.setStatus(response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            result.setData(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus(500);
            result.setData(e.getMessage());
        } finally {
            // 关闭连接,释放资源
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
