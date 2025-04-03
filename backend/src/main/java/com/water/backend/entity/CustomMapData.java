package com.water.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

// 自定义地图数据实体类
@Data
@TableName("custom_map_data")
public class CustomMapData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String catalogId;
    private String mapType;
    // JSON数据存储为字符串
    private String mapData;
    private Boolean visible;
    private Date createTime;
    private Date updateTime;
}