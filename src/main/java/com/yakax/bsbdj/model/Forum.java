package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Forum {
    private Long forumId;

    private Integer postCount;

    private String logo;

    private Integer forumSort;

    private Integer forumStatus;

    private String info;

    private String name;

    private Integer userCount;

}