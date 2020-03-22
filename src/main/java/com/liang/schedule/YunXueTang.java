package com.liang.schedule;

import com.liang.utils.BrowserUtil;
import com.liang.utils.CookieCrawlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 * @author liangyehao
 * @version 1.0
 * @date 2020/3/22 18:58
 * @content 定时任务播放云学堂视频
 */
@Slf4j
@Configuration
@EnableScheduling
public class YunXueTang implements ApplicationContextAware {
    /**
     * spring容器
     */
    private ConfigurableApplicationContext context;

    /**
     * 用户
     */
    @Value("${yunxuetang.userName}")
    private String userName;

    /**
     * 云学堂视频列表url
     */
    @Value("${yunxuetang.urlPath}")
    private String url;


    /**
     * 用户登录cookie
     */
    @Value("${yunxuetang.cookies}")
    private String cookies;

    /**
     * 第几个视频开始
     */
    @Value("${yunxuetang.videoIndex}")
    private Integer i;

    /**
     * 浏览器的路径
     */
    @Value("${yunxuetang.browserPath}")
    private String browserPath;

    /**
     * 任务列表
     */
    private List<Map> taskList;

    /**
     * 下次
     */
    private LocalDateTime nextTime = LocalDateTime.now();

    /**
     * 固定利率的字符串
     */
    @Value("${yunxuetang.fixedRate}")
    private String fixedRateString;

    /**
     * 伪装登录用户访问频率
     */
    @Value("${yunxuetang.disguiseRate}")
    private String disguiseRateString;


    @PostConstruct
    public void initTask() {
        LocalDateTime startTime = LocalDateTime.now();
        log.warn("首个任务开始时间: {}", startTime);
        taskList = CookieCrawlUtil.getTaskList(url, cookies);
        log.warn("当前总任务数: {}", taskList.size());
        log.warn("当前用户[{}]", userName);
        log.warn("执行频率 [{}] 毫秒", fixedRateString);
        log.warn("伪装访问频率 [{}] 毫秒", disguiseRateString);
    }

    /**
     * 3.添加定时任务
     *
     * @Scheduled(cron = "0/5 * * * * ?")
     * @Scheduled(fixedRate=5000) 或直接指定时间间隔，例如：5秒
     * 20分钟执行一次
     */
    @Scheduled(fixedRateString = "${yunxuetang.fixedRate}")
    private void learning() throws Exception {
        if (taskList.size() == 0) {
            log.error("程序关闭!未爬取到任何课程信息,cookie信息可能已过期,请更新cookie信息后重启!");
            context.close();
        } else {

            if (i > taskList.size()) {
                log.error("学习完毕,程序关闭!");
                context.close();
                return;
            }

            if (LocalDateTime.now().compareTo(nextTime) > 0) {
                if ((i - 1) <= taskList.size()) {
                    Map map = taskList.get(i - 1);
                    //视频已经看完
                    if ("100%".equals(map.get("progress"))) {
                        log.error("第[{}]个视频已看过,跳过此视频,[{}] 毫秒后播放下一个,共有[{}]个视频 ", i, fixedRateString, taskList.size());
                    } else {
                        int duration = Integer.parseInt(map.get("minute").toString());
                        BrowserUtil.browse(browserPath, map.get("videoUrl").toString());
                        log.warn("共有[{}]个视频," +
                                "开始学习第 [{}] 个视频," +
                                "视频标题 [{}]," +
                                "视频时长 [ {} ] 分钟," +
                                "开始时间 [ {} ]", taskList.size(), i, map.get("title"), duration, LocalDateTime.now());
                        nextTime = LocalDateTime.now().plusMinutes(duration + 2);
                        log.warn("下个视频播放时间标记[{}]", nextTime);
                    }
                    i++;
                }
            } else {
                log.info("第 [{}] 个视频还未播放完毕,不执行此次定时任务,[{}]毫秒后检查时间是否超过[{}]",  (i-1), fixedRateString, nextTime);
            }
        }
    }

    /**
     * 每隔一段时间伪装访问一次任务列表防止cookie过期
     */
    @Scheduled(fixedRateString = "${yunxuetang.disguiseRate}")
    private void checkCookies() {
        log.info("每隔[{}]毫秒伪装访问一次任务列表,防止cookie过期", disguiseRateString);
        taskList =
                CookieCrawlUtil.getTaskList(url, cookies);
        if (taskList.size() == 0) {
            log.error("程序关闭!未爬取到任何课程信息,cookie信息可能已过期,请更新cookie信息后重启!");
            context.close();
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = (ConfigurableApplicationContext) applicationContext;
    }
}
