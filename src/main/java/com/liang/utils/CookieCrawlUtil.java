package com.liang.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangyehao
 * @version 1.0
 * @date 2020/3/20 22:58
 * @content
 */
public class CookieCrawlUtil {

    public static void main(String[] args) throws Exception {

        String browserPath = "D:\\MySoftware\\360FastBrowser\\360Chrome\\Chrome\\Application\\360chrome.exe";
        String urlPath = "http://sinobest.yunxuetang.cn/plan/ed331dec3fd9437abb89b73e4e7625a6.html";
        String cookie = "ELEARNING_00008=8dde0c50-8e39-4aca-9ada-3e3788846f10; ELEARNING_00002=liangyehao; ELEARNING_00017=e5370ec7-33c6-4f70-8f64-ee81f95bcb36; XXTOWN_COOKIE_00018=672e723c-83b0-4596-8616-835ac18abcfb; COOKIE_LANGAGES=zh; ELEARNING_00999=y3xd4fgntsxio5eqwqgfcvg5; route=797bf68c9b8c7c6fd5358384c396cf6e; TY_SESSION_ID=f983d62c-e05c-4894-929b-427a2063299e; ELEARNING_00003=StudyMenuGroup; ELEARNING_00006=658BF0B4E7179839EB784A644792EB4288A85B184A919042593BE2ACDF7914001B754F6479C9B2DFF38127C4E5BD54DC9C40934693F6929C799D061C47B94AF70DEB50ADE0BD17C5B14A6B6AC94723F946C9563E988B7C83AE1619CB5C59F9CD3ADB63A9B9065549FAF318596174EDA147D829FF9AF1B9416940C7B13A7C5A391CFF7A463A092C218E07119189A1EE73837D46CC27B010444DD06D96E00659FF04FD3BCEC3DC7DC1CC6747A51AAC635FDD3B57FE77483910C5F25597; ELEARNING_00018=kEWTaJi27uTd9D18eKs0p7Cmt3m1PN1mhxwL8zllJCRKYlSD6TSuMxbgUEUqk9ixBYBSL1iEXAQZ+zu/XszCtyqwU4NSTvEd5pjYrUhbJNe5+ecq9Wqet91vp94kY26U+EnLupF0PC+zgqKTtjU0m1oMd4RFTSOSEqdjcYybDT2zUxyAQlweCcO6HJuI0BeTgQVYVOLel1mPxbA3BWpqDB9pQtxcDEex6HcuG3/qzpg=; ELEARNING_00024=ucloud--cluster--AAAAAORR7Ojm4gXTXOXiXGnndf9F-t_57EkFvl42cceVSEd6ZSdLHX_n2v1V2uYluyW4BAyVhWFQT9OUiLnarqo8QldJSVG0iVzgBPqvDVTe4QSizGH20DATRhZxMCIw2QxM169qejOsdhc-SGWkRVHReKM";

        List<Map> taskList = getTaskList(urlPath, cookie);
        Map map = taskList.get(4);
        System.out.println("共有{ "+taskList.size()+" }个视频:::");
        System.out.println("处理第[1]个视频::::{"+map.get("title")+"}");
        if (!"100%".equals(map.get("progress"))) {
            BrowserUtil.browse(browserPath,map.get("videoUrl").toString());
        }

    }

    /**
     * 获取任务列表
     *
     * @param urlPath url路径
     * @param cookie  cookie
     * @return {@link List<Map>}
     */
    public static List<Map> getTaskList(String urlPath,String cookie){
        String html = getHtmlByCookie(urlPath, cookie);
        Document doc = Jsoup.parse(html);
        Elements select = doc.select("tr[class=hand]");

        List<Map> result = new ArrayList<>();
        for (Element element : select) {
            //标题
            String title = getTitleFromElement(element);
            //进度
            String propress = getProgressFromElement(element);
            //视频路径
            String videoUrl = getVideoUrlFromElement(element);
            //时长
            String minute = getTimeFromElement(element);

            Map<String,String> map = new HashMap<>(4);
            map.put("title",title);
            map.put("progress",propress);
            map.put("videoUrl",videoUrl);
            map.put("minute",minute);

            result.add(map);
        }

        return result;
    }

    /**
     * 从元素得到时间
     *
     * @param element 元素
     * @return {@link String}
     */
    private static String getTimeFromElement(Element element) {

        try {
            return element.select("span[class=times font-size-12 text-grey]").text().replace("：","");
        } catch (NumberFormatException e) {
            System.out.println("获取视频时长出错::::::::::");
            e.printStackTrace();
            return "0";
        }
    }


    /**
     * 从元素得到视频的Url
     *
     * @param element 元素
     * @return {@link String}
     */
    private static String getVideoUrlFromElement(Element element) {
        try {
            String onclick = element.attributes().getIgnoreCase("onclick");
            String frondString = onclick.substring(0, onclick.indexOf("/plan/"));
            String urlWithPrefix = onclick.substring(0,onclick.indexOf(".html"));
            String prefix = "http://sinobest.yunxuetang.cn/kng/";
            String suffix = ".html";
            return prefix + urlWithPrefix.replace(frondString,"") + suffix;
        } catch (Exception e) {
            System.out.println("获取视频地址出错::::::::::");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 从元素得到进度
     *
     * @param element 元素
     * @return {@link String}
     */
    private static String getProgressFromElement(Element element) {
        try {
            return element.select("span[class=font-size-12 text-grey]").get(3).text();
        } catch (Exception e) {
            System.out.println("获取进度出错::::::::::");
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 从元素获得标题
     *
     * @param element 元素
     * @return {@link String}
     */
    private static String getTitleFromElement(Element element) {
        try {
            String[] split = StringUtils.split(element.select("td[class=text-left]").select("span").text(), " ");
            return split[1];
        } catch (Exception e) {
            System.out.println("获取标题出错::::::::::");
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 传入Cookie得到Html
     *
     * @param urlPath url路径
     * @param cookie  cookie
     * @return {@link String}
     */
    public static String getHtmlByCookie(String urlPath,String cookie){
        StringBuilder sb = null;
        try {
            URL url = new URL(urlPath);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Cookie", cookie);
            conn.setDoInput(true);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }


}
