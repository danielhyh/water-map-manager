package com.water.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.water.backend.entity.CustomMapData;
import com.water.backend.mapper.CustomMapDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 自定义地图数据Service
@Service
public class CustomMapDataService {
    @Autowired
    private CustomMapDataMapper customMapDataMapper;
    
    public List<CustomMapData> listAll() {
        return customMapDataMapper.selectList(null);
    }
    
    public CustomMapData getById(Long id) {
        return customMapDataMapper.selectById(id);
    }
    
    public List<CustomMapData> getByCatalogId(String catalogId) {
        LambdaQueryWrapper<CustomMapData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomMapData::getCatalogId, catalogId);
        return customMapDataMapper.selectList(wrapper);
    }
    
    public boolean save(CustomMapData customMapData) {
        if (customMapData.getId() == null) {
            return customMapDataMapper.insert(customMapData) > 0;
        } else {
            return customMapDataMapper.updateById(customMapData) > 0;
        }
    }
    
    public boolean removeById(Long id) {
        return customMapDataMapper.deleteById(id) > 0;
    }
}