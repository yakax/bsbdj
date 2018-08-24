package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Comment {
    private Long commentId;

    private String commentText;

    private Long uid;

    private String passtime;

    private Long contentId;

}