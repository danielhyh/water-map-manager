package com.water.backend.controller;

import com.water.backend.entity.CustomMapData;
import com.water.backend.service.CustomMapDataService;
import com.water.backend.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

// 自定义地图数据Controller
@RestController
@RequestMapping("/api/map")
public class CustomMapDataController {
    @Autowired
    private CustomMapDataService customMapDataService;
    
    @GetMapping("/list")
    public R<List<CustomMapData>> list() {
        return R.ok(customMapDataService.listAll());
    }
    
    @GetMapping("/getByCatalog/{catalogId}")
    public R<List<CustomMapData>> getByCatalogId(@PathVariable String catalogId) {
        return R.ok(customMapDataService.getByCatalogId(catalogId));
    }
    
    @GetMapping("/{id}")
    public R<CustomMapData> getById(@PathVariable Long id) {
        return R.ok(customMapDataService.getById(id));
    }
    
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody CustomMapData customMapData) {
        return R.ok(customMapDataService.save(customMapData));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> remove(@PathVariable Long id) {
        return R.ok(customMapDataService.removeById(id));
    }
    
    @PostMapping("/upload")
    public R<String> uploadMapData(@RequestParam("file") MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            // 验证GeoJSON或KML格式
            // ...省略验证逻辑
            return R.ok(content);
        } catch (Exception e) {
            return R.fail("文件解析失败：" + e.getMessage());
        }
    }
}