package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Source {
    private Long sourceId;

    private Integer channelId;

    private String responseText;

    private Date createTime;

    private String url;

    private String state;

}