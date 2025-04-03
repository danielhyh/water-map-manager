package com.water.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

// 图片点位关联实体类
@Data
@TableName("point_image")
public class PointImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    // JSON数据存储为字符串
    private String points;
    private Date createTime;
    private Date updateTime;
}