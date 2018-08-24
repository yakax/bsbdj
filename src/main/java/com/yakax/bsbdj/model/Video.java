package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Video {
    private Long videoId;

    private String videoUrl;

    private String downloadUrl;

    private Integer width;

    private Integer height;

    private Integer playfcount;

    private Integer duration;

    private Integer playcount;

    private String thumb;

    private String thumbSmall;

    private Long contentId;

}