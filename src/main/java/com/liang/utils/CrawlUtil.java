package com.liang.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author liangyehao
 * @version 1.0
 * @date 2020/3/16 11:28
 * @content
 */
public class CrawlUtil {

    public static String crawlHtml(String url) throws IOException {
        //建立一个新的请求客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //使用HttpGet方式请求网址
        HttpGet httpGet = new HttpGet(url);
        //获取网址的返回结果
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            //获取返回结果中的实体
            assert response != null;
            HttpEntity entity = response.getEntity();

            return EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }finally {
            httpClient.close();
            assert response != null;
            response.close();
        }

    }
}
