package com.yakax.bsbdj.controller;

import com.github.pagehelper.Page;
import com.yakax.bsbdj.model.Res;
import com.yakax.bsbdj.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


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

    @RequestMapping("/etl")
    @ResponseBody
    public void etl() {
        crawlerService.etl();
    }

    @RequestMapping("/")
    public String manager() {
        return "manager";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Res selectAll(Integer page, Integer limit, Integer channel, String contentType, String keyword) {
        Map<String, Object> parms = new HashMap<>();
        if (channel != null && channel != -1)
            parms.put("channel", channel);
        if (contentType != null && !"-1".equals(contentType))
            parms.put("contentType", contentType);
        if (keyword != null)
            parms.put("keyword", "%" + keyword + "%");
        Page<Map> mapPage = crawlerService.selectAll(page, limit, parms);
        Res res = new Res();
        res.setCode(0);
        res.setCount((int) mapPage.getTotal());
        res.setData(mapPage.getResult());
        return res;
    }

    @RequestMapping("/del")
    @ResponseBody
    public Res del(int contentId) {
        Assert.isTrue(crawlerService.delContent(contentId) > 0, "删除失败");
        Res res = new Res();
        res.setCode(0);
        return res;
    }

    @RequestMapping("/view/{cid}.html")
    public ModelAndView view(@PathVariable("cid") Long contentId) {
        Map<String, Object> view = crawlerService.getView(contentId);
        ModelAndView modelAndView = new ModelAndView("preview");
        modelAndView.addAllObjects(view);
        return modelAndView;
    }
}
