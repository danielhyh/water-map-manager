package com.water.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.water.backend.entity.CustomMapData;
import org.apache.ibatis.annotations.Mapper;

// 自定义地图数据Mapper
@Mapper
public interface CustomMapDataMapper extends BaseMapper<CustomMapData> {
}