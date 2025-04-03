package com.water.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.water.backend.entity.Catalog;
import com.water.backend.entity.CatalogTreeNode;
import com.water.backend.mapper.CatalogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Service
@Service
public class CatalogService {
    @Autowired
    private CatalogMapper catalogMapper;
    
    public List<Catalog> listAll() {
        LambdaQueryWrapper<Catalog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Catalog::getLayerindex);
        return catalogMapper.selectList(wrapper);
    }
    
    public List<CatalogTreeNode> getTreeData() {
        List<Catalog> catalogList = this.listAll();
        
        // 构建树形结构
        Map<String, CatalogTreeNode> nodeMap = new HashMap<>();
        List<CatalogTreeNode> rootNodes = new ArrayList<>();
        
        // 先将所有节点构建出来
        for (Catalog catalog : catalogList) {
            CatalogTreeNode node = new CatalogTreeNode();
            node.setId(catalog.getId());
            node.setLabel(catalog.getName());
            node.setPid(catalog.getPid());
            node.setDescription(catalog.getDescription());
            node.setChildren(new ArrayList<>());
            
            nodeMap.put(catalog.getId(), node);
        }
        
        // 构建树形结构
        for (Catalog catalog : catalogList) {
            CatalogTreeNode current = nodeMap.get(catalog.getId());

            if (catalog.getPid() == null || catalog.getPid().isEmpty()) {
                // 根节点
                rootNodes.add(current);
            } else {
                // 子节点
                CatalogTreeNode parent = nodeMap.get(catalog.getPid());
                if (parent != null) {
                    parent.getChildren().add(current);
                } else {
                    // 父节点不存在，作为根节点处理
                    rootNodes.add(current);
                }
            }
        }
        
        return rootNodes;
    }
}