package com.yakax.bsbdj.mapper;

import com.yakax.bsbdj.model.Content;

public interface ContentMapper {
    int deleteByPrimaryKey(Long contentId);

    int insert(Content record);

    int insertSelective(Content record);

    Content selectByPrimaryKey(Long contentId);

    int updateByPrimaryKeySelective(Content record);

    int updateByPrimaryKey(Content record);
}