package com.water.backend.service;

import com.water.backend.entity.PointImage;
import com.water.backend.mapper.PointImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

// 图片点位关联Service
@Service
public class PointImageService {
    @Autowired
    private PointImageMapper pointImageMapper;
    
    @Value("${app.upload-path}")
    private String uploadPath;
    
    public List<PointImage> listAll() {
        return pointImageMapper.selectList(null);
    }
    
    public PointImage getById(Long id) {
        return pointImageMapper.selectById(id);
    }
    
    public List<PointImage> findByPoint(double lat, double lng) {
        return pointImageMapper.findByPoint(lat, lng);
    }
    
    public List<PointImage> findByKeyword(String keyword) {
        return pointImageMapper.findByKeyword(keyword);
    }
    
    public boolean save(PointImage pointImage) {
        if (pointImage.getId() == null) {
            return pointImageMapper.insert(pointImage) > 0;
        } else {
            return pointImageMapper.updateById(pointImage) > 0;
        }
    }
    
    public boolean removeById(Long id) {
        PointImage pointImage = pointImageMapper.selectById(id);
        if (pointImage != null) {
            // 删除关联图片
            String imagePath = uploadPath + "/" + pointImage.getImageUrl().substring(pointImage.getImageUrl().lastIndexOf("/") + 1);
            try {
                Files.deleteIfExists(Paths.get(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pointImageMapper.deleteById(id) > 0;
        }
        return false;
    }
    
    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = uploadPath + "/" + fileName;
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return "/water-map-api/images/" + fileName;
    }
}