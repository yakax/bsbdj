package com.yakax.bsbdj.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yakax.bsbdj.common.OgnlUtils;
import com.yakax.bsbdj.mapper.*;
import com.yakax.bsbdj.model.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CrawlerService {
    @Resource
    private SourceMapper sourceMapper;
    @Resource
    private ContentMapper contentMapper;
    @Resource
    private VideoMapper videoMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ForumMapper forumMapper;

    private void crawl(Map context, String template, String np, Integer channelId) {
        String url = template.replace("{np}", np);
        //创建对象
        OkHttpClient client = new OkHttpClient();
        //构建请求
        Request.Builder builder = new Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        //发送请求
        Request request = builder.build();
        //响应
        Response response;
        String data = null;
        //增加连接超时重试10次给予容错
        for (int i = 0; i <= 10; i++) {
            try {
                response = client.newCall(request).execute();
                //得到数据
                Assert.notNull(response.body(), "数据为空");
                data = response.body().string();
                break;
            } catch (IOException e) {
                log.error("连接超时！URL:{},正在准备第{}次尝试。", url, i + 1);
            }
        }
        Assert.notNull(data, "抓取失败！连接超时：URL:" + url);

        Gson gson = new Gson();
        //序列化数据
        Map map = gson.fromJson(data, new TypeToken<Map>() {
        }.getType());
        //得到np
        Double dnp = (Double) ((Map) map.get("info")).get("np");
        //格式化np数据
        String npStr = new DecimalFormat("############").format(dnp);
        log.info("抓取数据成功！NP:{},URL:{}", np, url);
        Source sourceEntity = new Source();
        sourceEntity.setChannelId(channelId);
        sourceEntity.setCreateTime(new Date());
        sourceEntity.setResponseText(data);
        sourceEntity.setUrl(url);
        sourceEntity.setState("WAITING");
        sourceMapper.insert(sourceEntity);
        //每次抓取一百条
        int count = (int) context.get("context");
        count += 20;
        context.put("context", count);
        if (count >= 100)
            return;

        //递归调用
        crawl(context, template, npStr, channelId);
    }

    public void crawlRunner() {
        String[] url = new String[]{
                "http://c.api.budejie.com/topic/list/jingxuan/1/budejie-android-7.0.2/{np}-20.json?market=tencentyingyongbao&udid=863064010240796&appname=baisibudejie&os=4.4.2&client=android&visiting=&mac=F0%3A79%3A59%3A5D%3AA1%3A75&ver=7.0.2",
                "http://c.api.budejie.com/topic/list/jingxuan/41/budejie-android-7.0.2/{np}-20.json?market=tencentyingyongbao&udid=863064010240796&appname=baisibudejie&os=4.4.2&client=android&visiting=&mac=F0%3A79%3A59%3A5D%3AA1%3A75&ver=7.0.2",
                "http://c.api.budejie.com/topic/list/jingxuan/10/budejie-android-7.0.2/{np}-20.json?market=tencentyingyongbao&udid=863064010240796&appname=baisibudejie&os=4.4.2&client=android&visiting=&mac=F0%3A79%3A59%3A5D%3AA1%3A75&ver=7.0.2",
                "http://c.api.budejie.com/topic/list/jingxuan/29/budejie-android-7.0.2/{np}-20.json?market=tencentyingyongbao&udid=863064010240796&appname=baisibudejie&os=4.4.2&client=android&visiting=&mac=F0%3A79%3A59%3A5D%3AA1%3A75&ver=7.0.2",
                "http://s.budejie.com/topic/list/remen/1/budejie-android-7.0.2/{np}-20.json?market=tencentyingyongbao&udid=863064010240796&appname=baisibudejie&os=4.4.2&client=android&visiting=&mac=F0%3A79%3A59%3A5D%3AA1%3A75&ver=7.0.2"
        };
        for (int i = 0; i < url.length; i++) {
            log.info("正在抓取第{}个模块", i + 1);
            Map<String, Integer> context = new HashMap<>();
            context.put("context", 0);
            crawl(context, url[i], "0", (i + 1));
        }

    }

    @Transactional
    public void etl() {
        List<Source> sourceList = sourceMapper.selectByState("WAITING");
        for (Source source : sourceList) {
            //先把数据json 转换为健值对
            Map<String, Object> map = new Gson().fromJson(source.getResponseText(), new TypeToken<Map<String, Object>>() {
            }.getType());
            //通过ognl 获取健值对里面的list 每个list 有20条记录
            List<Map<String, Object>> listMap = OgnlUtils.getListMap("list", map);
            for (Map<String, Object> stringObjectMap : listMap) {
                etlInsert(source,stringObjectMap);
            }
            source.setState("PROCESSED");
            sourceMapper.updateByPrimaryKey(source);
        }
    }

    @Transactional
    public void etlInsert(Source source, Map<String, Object> stringObjectMap) {
        Long contentId = OgnlUtils.getNumber("id", stringObjectMap).longValue();
        //一个内容只允许插入一次，不允许重复
        if (contentMapper.selectByPrimaryKey(contentId) != null) {
            log.info("Content ID:{} ，内容已存在，此内容被忽略导入", contentId);
            return;
        }
        //内容
        Content content = new Content();
        content.setContentId(OgnlUtils.getNumber("id", stringObjectMap).longValue());
        content.setChannelId(source.getChannelId().longValue());
        content.setStatus(OgnlUtils.getNumber("status", stringObjectMap).intValue());
        content.setCommentCount(OgnlUtils.getNumber("comment", stringObjectMap).intValue());
        content.setBookmarkCount(OgnlUtils.getNumber("bookmark", stringObjectMap).intValue());
        content.setContentText(OgnlUtils.getString("text", stringObjectMap));
        content.setLikeCount(OgnlUtils.getNumber("up", stringObjectMap).intValue());
        content.setHateCount(OgnlUtils.getNumber("down", stringObjectMap).intValue());
        content.setShareUrl(OgnlUtils.getString("share_url", stringObjectMap));
        content.setShareCount(OgnlUtils.getNumber("forward", stringObjectMap).intValue());
        content.setPasstime(OgnlUtils.getString("passtime", stringObjectMap));
        content.setContentType(OgnlUtils.getString("type", stringObjectMap));
        content.setSourceId(source.getSourceId());
        content.setCreateTime(new Date());
        contentMapper.insert(content);

        if (content.getContentType().equals("video")) {
            //视频
            Video video = new Video();
            //统一取第一个地址
            List<String> videoUrl = OgnlUtils.getListString("video.video", stringObjectMap);
            video.setVideoUrl(videoUrl.size() > 0 ? videoUrl.get(0) : null);
            List<String> downloadUrl = OgnlUtils.getListString("video.download", stringObjectMap);
            video.setDownloadUrl(downloadUrl.size() > 0 ? videoUrl.get(0) : null);
            video.setWidth(OgnlUtils.getNumber("video.width", stringObjectMap).intValue());
            video.setHeight(OgnlUtils.getNumber("video.height", stringObjectMap).intValue());
            video.setPlayfcount(OgnlUtils.getNumber("video.playfcount", stringObjectMap).intValue());
            video.setPlaycount(OgnlUtils.getNumber("video.playcount", stringObjectMap).intValue());
            video.setDuration(OgnlUtils.getNumber("video.duration", stringObjectMap).intValue());
            List<String> thumbUrl = OgnlUtils.getListString("video.thumbnail", stringObjectMap);
            video.setThumb(thumbUrl.size() > 0 ? thumbUrl.get(0) : null);
            List<String> thumbSmallUrl = OgnlUtils.getListString("video.thumbnail_small", stringObjectMap);
            video.setThumbSmall(thumbSmallUrl.size() > 0 ? thumbSmallUrl.get(0) : null);
            video.setContentId(content.getContentId());
            videoMapper.insert(video);
        } else if (content.getContentType().equals("image")) {
            Image image = new Image();
            List<String> bigUrl = OgnlUtils.getListString("image.big", stringObjectMap);
            image.setBigUrl(bigUrl.size() > 0 ? bigUrl.get(0) : null);
            List<String> watermarkerUrl = OgnlUtils.getListString("image.download_url", stringObjectMap);
            image.setWatermarkerUrl(watermarkerUrl.size() > 0 ? watermarkerUrl.get(0) : null);
            image.setRawHeight(OgnlUtils.getNumber("image.height", stringObjectMap).intValue());
            image.setRawWidth(OgnlUtils.getNumber("image.width", stringObjectMap).intValue());
            List<String> thumbUrl = OgnlUtils.getListString("image.thumbnail_small", stringObjectMap);
            image.setThumbUrl(thumbUrl.size() > 0 ? thumbUrl.get(0) : null);
            image.setContentId(content.getContentId());
            imageMapper.insert(image);
        } else if (content.getContentType().equals("gif")) {
            Image gif = new Image();
            List<String> bigUrl = OgnlUtils.getListString("gif.images", stringObjectMap);
            gif.setBigUrl(bigUrl.size() > 0 ? bigUrl.get(0) : null);
            List<String> watermarkerUrl = OgnlUtils.getListString("gif.download_url", stringObjectMap);
            gif.setWatermarkerUrl(watermarkerUrl.size() > 0 ? watermarkerUrl.get(0) : null);
            gif.setRawWidth(OgnlUtils.getNumber("gif.width", stringObjectMap).intValue());
            gif.setRawHeight(OgnlUtils.getNumber("gif.height", stringObjectMap).intValue());
            List<String> thumbUrl = OgnlUtils.getListString("gif.gif_thumbnail", stringObjectMap);
            gif.setThumbUrl(thumbUrl.size() > 0 ? thumbUrl.get(0) : null);
            gif.setContentId(content.getContentId());
            imageMapper.insert(gif);
        }
        //用户数据
        Number userId = OgnlUtils.getNumber("u.uid", stringObjectMap);
        if (userId != null) {
            User user = userMapper.selectByPrimaryKey(userId.longValue());
            //如果用户不存在就创建
            if (user == null) {
                user = new User();
                user.setUid(OgnlUtils.getNumber("u.uid", stringObjectMap).longValue());
                List<String> headerUrl = OgnlUtils.getListString("u.header", stringObjectMap);
                user.setHeader(headerUrl.size() > 0 ? headerUrl.get(0) : null);
                user.setIsV(OgnlUtils.getBoolean("u.is_v", stringObjectMap) ? 1 : 0);
                user.setIsVip(OgnlUtils.getBoolean("u.is_vip", stringObjectMap) ? 1 : 0);
                user.setRoomUrl(OgnlUtils.getString("u.room_url", stringObjectMap));
                user.setRoomName(OgnlUtils.getString("u.room_name", stringObjectMap));
                user.setRoomRole(OgnlUtils.getString("u.room_role", stringObjectMap));
                user.setRoomIcon(OgnlUtils.getString("u.room_icon", stringObjectMap));
                user.setNickname(OgnlUtils.getString("u.name", stringObjectMap));
                userMapper.insert(user);
            }
            content.setUid(user.getUid());
            contentMapper.updateByPrimaryKey(content);
        }
        //评论数据
        List<Map<String, Object>> commentsMap = OgnlUtils.getListMap("top_comments", stringObjectMap);
        if (commentsMap != null) {
            for (Map<String, Object> map : commentsMap) {
                Comment comment = new Comment();
                comment.setCommentId(OgnlUtils.getNumber("id", map).longValue());
                comment.setPasstime(OgnlUtils.getString("passtime", map));
                comment.setCommentText(OgnlUtils.getString("content", map));
                Long uid = OgnlUtils.getNumber("u.uid", map).longValue();
                User user = userMapper.selectByPrimaryKey(uid);
                if (user == null) {
                    user = new User();
                    List<String> header = OgnlUtils.getListString("u.header", map);
                    user.setHeader(header.size() > 0 ? header.get(0) : null);
                    user.setUid(OgnlUtils.getNumber("u.uid", map).longValue());
                    user.setIsVip(OgnlUtils.getBoolean("u.is_vip", map) ? 1 : 0);
                    user.setRoomUrl(OgnlUtils.getString("u.room_url", map));
                    user.setRoomName(OgnlUtils.getString("u.room_name", map));
                    user.setRoomRole(OgnlUtils.getString("u.room_role", map));
                    user.setRoomIcon(OgnlUtils.getString("u.room_icon", map));
                    user.setNickname(OgnlUtils.getString("u.name", map));
                    userMapper.insert(user);
                }
                comment.setUid(user.getUid());
                comment.setContentId(content.getContentId());
                commentMapper.insert(comment);
            }
        }
        //圈子信息
        List<Map<String, Object>> tagsMap = OgnlUtils.getListMap("tags", stringObjectMap);
        //只获取第一个论坛
        if (tagsMap.size() > 0) {
            Map<String, Object> tag = tagsMap.get(0);
            Long forumId = OgnlUtils.getNumber("id", tagsMap.get(0)).longValue();
            Forum forum = forumMapper.selectByPrimaryKey(forumId);
            //查找论坛,有则加载,无则创建
            if (forum == null) {
                forum = new Forum();
                forum.setPostCount(OgnlUtils.getNumber("post_number", tag).intValue());
                forum.setLogo(OgnlUtils.getString("image_list", tag));
                forum.setForumSort(OgnlUtils.getNumber("forum_sort", tag).intValue());
                forum.setForumStatus(OgnlUtils.getNumber("forum_status", tag).intValue());
                forum.setForumId(forumId);
                forum.setInfo(OgnlUtils.getString("info", tag));
                forum.setName(OgnlUtils.getString("name", tag));
                forum.setUserCount(OgnlUtils.getNumber("sub_number", tag).intValue());
                forumMapper.insert(forum);
            }
            content.setForumId(forum.getForumId());//数据都是先添加在把关联上所以后做更新操作
            contentMapper.updateByPrimaryKey(content);
        }
        log.info("Content ID:{} ，内容成功导入!", contentId);

    }
}
