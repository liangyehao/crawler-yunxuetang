package com.liang.utils;

/**
 * @author liangyehao
 * @version 1.0
 * @date 2020/3/20 23:33
 * @content
 */
public class BrowserUtil {


    /**
     * 浏览
     *
     * @param browserPath 浏览器的路径
     * @param url         url
     * @throws Exception 异常
     */
    public static void browse(String browserPath,String url) throws Exception {
        String newWindow = "-new-window";
        ProcessBuilder proc =
                new ProcessBuilder(browserPath,newWindow,url);
        proc.start();
    }

    public static void browse(String browserPath,String url,String command) throws Exception {
        ProcessBuilder proc =
                new ProcessBuilder(browserPath,url,command);
        proc.start();
    }


    public static void main(String[] args) throws Exception {
        String browserPath = "D:\\MySoftware\\360FastBrowser\\360Chrome\\Chrome\\Application\\360chrome.exe";
        String newWindow = "-new-window";
        String url = "http://baidu.com";
        ProcessBuilder proc =
                new ProcessBuilder(browserPath,newWindow,url);
        proc.start();
//        browse(browserPath,url);
    }

}
