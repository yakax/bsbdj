package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Content {
    private Long contentId;

    private Long channelId;

    private Long forumId;

    private Long uid;

    private Integer status;

    private Integer commentCount;

    private Integer bookmarkCount;

    private String contentText;

    private Integer likeCount;

    private Integer hateCount;

    private String shareUrl;

    private Integer shareCount;

    private String passtime;

    private String contentType;

    private Long sourceId;

    private Date createTime;
}