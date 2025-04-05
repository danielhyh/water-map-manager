package com.water.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

// 区域描述实体类
@Data
@TableName("area_description")
public class AreaDescription {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String catalogId;
    private String areaBounds;
    private String description;
    private Date createTime;
    private Date updateTime;
}