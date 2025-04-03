package com.water.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.water.backend.entity.PointImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 图片点位关联Mapper
@Mapper
public interface PointImageMapper extends BaseMapper<PointImage> {
    // 根据点位搜索
    @Select("SELECT * FROM point_image WHERE points @> ANY(ARRAY[jsonb_build_object('lat', #{lat}, 'lng', #{lng})]::jsonb[])")
    List<PointImage> findByPoint(@Param("lat") double lat, @Param("lng") double lng);
    
    // 根据关键词搜索
    @Select("SELECT * FROM point_image WHERE title ILIKE CONCAT('%', #{keyword}, '%') OR description ILIKE CONCAT('%', #{keyword}, '%')")
    List<PointImage> findByKeyword(@Param("keyword") String keyword);
}