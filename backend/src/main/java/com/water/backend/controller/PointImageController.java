package com.water.backend.controller;

import com.water.backend.entity.PointImage;
import com.water.backend.service.PointImageService;
import com.water.backend.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// 图片点位关联Controller
@RestController
@RequestMapping("/api/pointImage")
public class PointImageController {
    @Autowired
    private PointImageService pointImageService;
    
    @GetMapping("/list")
    public R<List<PointImage>> list() {
        return R.ok(pointImageService.listAll());
    }
    
    @GetMapping("/{id}")
    public R<PointImage> getById(@PathVariable Long id) {
        return R.ok(pointImageService.getById(id));
    }
    
    @GetMapping("/findByPoint")
    public R<List<PointImage>> findByPoint(@RequestParam Double lat, @RequestParam Double lng) {
        return R.ok(pointImageService.findByPoint(lat, lng));
    }
    
    @GetMapping("/search")
    public R<List<PointImage>> search(@RequestParam String keyword) {
        return R.ok(pointImageService.findByKeyword(keyword));
    }
    
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody PointImage pointImage) {
        return R.ok(pointImageService.save(pointImage));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> remove(@PathVariable Long id) {
        return R.ok(pointImageService.removeById(id));
    }
    
    @PostMapping("/upload")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            return R.ok(pointImageService.uploadImage(file));
        } catch (IOException e) {
            return R.fail("图片上传失败：" + e.getMessage());
        }
    }
}