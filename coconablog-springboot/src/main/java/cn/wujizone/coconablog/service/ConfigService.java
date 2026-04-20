package cn.wujizone.coconablog.service;

import cn.wujizone.coconablog.entity.Config;
import cn.wujizone.coconablog.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {
    
    private final ConfigMapper configMapper;
    
    public Map<String, Object> getPublicConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("siteName", getConfigValue("site_name", "Cocona Blog"));
        config.put("siteDescription", getConfigValue("site_description", "一个充满二次元风格的博客系统"));
        config.put("siteKeywords", getConfigValue("site_keywords", "博客,技术,二次元"));
        config.put("footerText", getConfigValue("footer_text", "© 2024 Cocona Blog"));
        
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("github", getConfigValue("social_github", ""));
        socialLinks.put("twitter", getConfigValue("social_twitter", ""));
        socialLinks.put("email", getConfigValue("social_email", ""));
        config.put("socialLinks", socialLinks);
        
        return config;
    }
    
    public List<Config> getAllConfig() {
        return configMapper.findAll();
    }
    
    @Transactional
    public void updateConfig(String key, String value) {
        configMapper.updateByKey(key, value);
    }
    
    private String getConfigValue(String key, String defaultValue) {
        Config config = configMapper.findByKey(key);
        return config != null && config.getValue() != null ? config.getValue() : defaultValue;
    }
}
