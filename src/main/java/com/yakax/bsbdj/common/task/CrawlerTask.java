package com.yakax.bsbdj.common.task;

import com.yakax.bsbdj.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yakax
 */
@Slf4j
@Component
public class CrawlerTask {
    @Resource
    private CrawlerService crawlerService;

    @Scheduled(cron = "0 0 0 1/1 * ? ")
    public void crawlerRunner() {
        crawlerService.crawlRunner();//先抓取
        crawlerService.etl();//然后etl 分析存取
    }
}
