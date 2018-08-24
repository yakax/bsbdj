package com.yakax.bsbdj.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private Long uid;

    private String header;

    private Integer isVip;

    private Integer isV;

    private String roomUrl;

    private String roomName;

    private String roomRole;

    private String roomIcon;

    private String nickname;
}