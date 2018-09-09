package com.yakax.bsbdj.mapper;

import com.yakax.bsbdj.model.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CommentMapper {
    int deleteByPrimaryKey(Long commentId);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Long commentId);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    @Select("SELECT c.*,u.nickname,u.header FROM t_comment c,t_user u WHERE c.uid=u.uid  and c.content_id=#{contentId}")
    List<Map> selectCommentOrUser(@Param("contentId") Long contentId);
}