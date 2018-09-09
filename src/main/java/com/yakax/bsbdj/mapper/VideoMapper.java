package com.yakax.bsbdj.mapper;

import com.yakax.bsbdj.model.Video;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface VideoMapper {
    int deleteByPrimaryKey(Long videoId);

    int insert(Video record);

    int insertSelective(Video record);

    Video selectByPrimaryKey(Long videoId);

    int updateByPrimaryKeySelective(Video record);

    int updateByPrimaryKey(Video record);

    @Select("SELECT * FROM t_video WHERE content_id=#{contentId}")
    @Results({
            @Result(property = "videoId", column = "video_id"),
            @Result(property = "videoUrl", column = "video_url"),
            @Result(property = "downloadUrl", column = "download_url"),
            @Result(property = "thumbSmall", column = "thumb_small"),
            @Result(property = "contentId", column = "content_id"),
    })
    Video selectByContenId(@Param("contentId") Long contentId);
}