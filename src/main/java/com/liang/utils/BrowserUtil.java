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
        System.out.println("调用360极速浏览器播放视频:::");
        ProcessBuilder proc =
                new ProcessBuilder(browserPath,url);
        proc.start();
    }

}
