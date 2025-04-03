package com.water.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.water.backend.entity.AreaDescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 区域描述Mapper
@Mapper
public interface AreaDescriptionMapper extends BaseMapper<AreaDescription> {
    // 使用空间查询查找区域
    @Select("SELECT * FROM area_description WHERE ST_Intersects(area_bounds, ST_SetSRID(ST_Point(#{lng}, #{lat}), 4326))")
    List<AreaDescription> findByPoint(@Param("lng") double lng, @Param("lat") double lat);
}