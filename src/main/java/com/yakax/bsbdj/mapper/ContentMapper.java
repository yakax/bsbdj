package com.yakax.bsbdj.mapper;

import com.yakax.bsbdj.model.Content;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ContentMapper {
    int deleteByPrimaryKey(Long contentId);

    int insert(Content record);

    int insertSelective(Content record);

    Content selectByPrimaryKey(Long contentId);

    int updateByPrimaryKeySelective(Content record);

    int updateByPrimaryKey(Content record);

    @Select("<script> " +
            "SELECT c.*,tc.channel_name,u.nickname FROM  t_content c,t_channel tc,t_user u " +
            " WHERE c.channel_id=tc.channel_id and c.uid=u.uid " +
            "<if test=\"channel!=null\">and c.channel_id=#{channel} </if> " +
            "<if test=\"contentType!=null\">and c.content_type=#{contentType} </if>" +
            "<if test=\"keyword!=null\">and c.content_text like #{keyword} </if> </script>")
    List<Map<String,Object>> selectAll(Map parms);
}