package com.yakax.bsbdj.controller;

import com.yakax.bsbdj.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


@Slf4j
@Controller
@RequestMapping("/crawl")
public class CrawlerController {

    @Resource
    private CrawlerService crawlerService;

    @RequestMapping("/test")
    @ResponseBody
    public void crawl() {
        crawlerService.crawlRunner();
    }
}
