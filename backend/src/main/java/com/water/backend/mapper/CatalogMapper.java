package com.water.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.water.backend.entity.Catalog;
import org.apache.ibatis.annotations.Mapper;

// Mapper
@Mapper
public interface CatalogMapper extends BaseMapper<Catalog> {
}