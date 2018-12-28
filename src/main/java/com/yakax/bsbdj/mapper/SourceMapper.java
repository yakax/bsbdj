package com.yakax.bsbdj.mapper;

import com.yakax.bsbdj.model.Source;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

public interface SourceMapper {
    int deleteByPrimaryKey(Long sourceId);

    int insert(Source record);

    int insertSelective(Source record);

    Source selectByPrimaryKey(Long sourceId);

    int updateByPrimaryKeySelective(Source record);

    int updateByPrimaryKey(Source record);

    List<Source> selectByState(String state);

    @Delete("delete from t_source")
    void delAll();
}