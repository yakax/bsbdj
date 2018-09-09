package com.yakax.bsbdj.mapper;

import com.yakax.bsbdj.model.Image;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface ImageMapper {
    int deleteByPrimaryKey(Long imageId);

    int insert(Image record);

    int insertSelective(Image record);

    Image selectByPrimaryKey(Long imageId);

    int updateByPrimaryKeySelective(Image record);

    int updateByPrimaryKey(Image record);

    @Select("SELECT * FROM t_image WHERE content_id=#{contentId}")
    @Results({
            @Result(property = "imageId", column = "image_id"),
            @Result(property = "bigUrl", column = "big_url"),
            @Result(property = "watermarkerUrl", column = "watermarker_url"),
            @Result(property = "rawHeight", column = "raw_height"),
            @Result(property = "rawWidth", column = "raw_width"),
            @Result(property = "thumbUrl", column = "thumb_url"),
            @Result(property = "contentId", column = "content_id"),
    })
    Image selectByContenId(@Param("contentId") Long contentId);
}