package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.entity.Config;
import cn.wujizone.coconablog.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
public class ConfigController {
    
    private final ConfigService configService;
    
    @GetMapping("/public")
    public Result<Map<String, Object>> getPublicConfig() {
        return Result.success(configService.getPublicConfig());
    }
    
    @GetMapping
    public Result<List<Config>> getAllConfig() {
        return Result.success(configService.getAllConfig());
    }
    
    @PutMapping("/{key}")
    public Result<Void> updateConfig(@PathVariable String key,
                                      @RequestBody Map<String, String> body) {
        configService.updateConfig(key, body.get("value"));
        return Result.success();
    }
}
