package com.water.backend.entity;

import lombok.Data;

import java.util.List;

// 目录树结构
@Data
public class CatalogTreeNode {
    private String id;
    private String label;
    private String pid;
    private String description;
    private List<CatalogTreeNode> children;
}