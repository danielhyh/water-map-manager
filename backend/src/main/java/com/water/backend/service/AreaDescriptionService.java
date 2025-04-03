package com.water.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.water.backend.entity.AreaDescription;
import com.water.backend.mapper.AreaDescriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 区域描述Service
@Service
public class AreaDescriptionService {
    @Autowired
    private AreaDescriptionMapper areaDescriptionMapper;
    
    public List<AreaDescription> listAll() {
        return areaDescriptionMapper.selectList(null);
    }
    
    public AreaDescription getById(Long id) {
        return areaDescriptionMapper.selectById(id);
    }
    
    public List<AreaDescription> getByCatalogId(String catalogId) {
        LambdaQueryWrapper<AreaDescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AreaDescription::getCatalogId, catalogId);
        return areaDescriptionMapper.selectList(wrapper);
    }
    
    public List<AreaDescription> getByPoint(double lng, double lat) {
        return areaDescriptionMapper.findByPoint(lng, lat);
    }
    
    public boolean save(AreaDescription areaDescription) {
        if (areaDescription.getId() == null) {
            return areaDescriptionMapper.insert(areaDescription) > 0;
        } else {
            return areaDescriptionMapper.updateById(areaDescription) > 0;
        }
    }
    
    public boolean removeById(Long id) {
        return areaDescriptionMapper.deleteById(id) > 0;
    }
}