package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Image {
    private Long imageId;

    private String bigUrl;

    private String watermarkerUrl;

    private Integer rawHeight;

    private Integer rawWidth;

    private String thumbUrl;

    private Long contentId;

}