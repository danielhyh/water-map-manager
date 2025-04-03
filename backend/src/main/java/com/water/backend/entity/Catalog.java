package com.water.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

// 目录实体类
@Data
@TableName("catalog")
public class Catalog {
    @TableId
    private String id;
    private String name;
    private String serverType;
    private String serverUrl;
    private String pid;
    private BigDecimal layerindex;
    private String tid;
    private Boolean checked;
    private Boolean open;
    private String customFunction;
    private String description;
    private String bounds;
    private Boolean nocheck;
    private BigDecimal opacity;
}