package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import cn.wujizone.coconablog.entity.Config;
import cn.wujizone.coconablog.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
public class ConfigController {
    
    private final ConfigService configService;
    
    @GetMapping("/public")
    public Result<Map<String, Object>> getPublicConfig() {
        log.info("获取公开配置");
        return Result.success(configService.getPublicConfig());
    }
    
    @GetMapping
    public Result<List<Config>> getAllConfig() {
        log.info("获取所有配置");
        return Result.success(configService.getAllConfig());
    }
    
    @PutMapping("/{key}")
    public Result<Void> updateConfig(@PathVariable String key,
                                      @RequestBody Map<String, String> body) {
        log.info("更新配置, key={}", key);
        configService.updateConfig(key, body.get("value"));
        return Result.success();
    }
}
