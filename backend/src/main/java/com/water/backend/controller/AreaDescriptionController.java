package com.water.backend.controller;

import com.water.backend.entity.AreaDescription;
import com.water.backend.service.AreaDescriptionService;
import com.water.backend.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 区域描述Controller
@RestController
@RequestMapping("/api/area")
public class AreaDescriptionController {
    @Autowired
    private AreaDescriptionService areaDescriptionService;
    
    @GetMapping("/list")
    public R<List<AreaDescription>> list() {
        return R.ok(areaDescriptionService.listAll());
    }
    
    @GetMapping("/getByCatalog/{catalogId}")
    public R<List<AreaDescription>> getByCatalogId(@PathVariable String catalogId) {
        return R.ok(areaDescriptionService.getByCatalogId(catalogId));
    }
    
    @GetMapping("/getByPoint")
    public R<List<AreaDescription>> getByPoint(@RequestParam Double lng, @RequestParam Double lat) {
        return R.ok(areaDescriptionService.getByPoint(lng, lat));
    }
    
    @GetMapping("/{id}")
    public R<AreaDescription> getById(@PathVariable Long id) {
        return R.ok(areaDescriptionService.getById(id));
    }
    
    @PostMapping("/save")
    public R<Boolean> save(@RequestBody AreaDescription areaDescription) {
        return R.ok(areaDescriptionService.save(areaDescription));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> remove(@PathVariable Long id) {
        return R.ok(areaDescriptionService.removeById(id));
    }
}