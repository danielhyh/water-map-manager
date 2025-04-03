package com.water.backend.controller;

import com.water.backend.entity.CatalogTreeNode;
import com.water.backend.service.CatalogService;
import com.water.backend.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    @Autowired
    private CatalogService catalogService;
    
    @GetMapping("/tree")
    public R<List<CatalogTreeNode>> getTreeData() {
        return R.ok(catalogService.getTreeData());
    }
}