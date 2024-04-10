package org.example;

import com.baomidou.mybatisplus.generator.config.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hejh
 * @date 2024/04/10
 */
@Data
@Slf4j
@Component
@ConfigurationProperties("generator")
public class SystemConfig {

    /**
     * 全局配置
     */
    public GlobalConfig globalConfig;

    /**
     * 数据源配置
     */
    public DataSourceConfig dataSourceConfig;

    /**
     * 包配置
     */
    public PackageConfig packageConfig;

    /**
     * 模板配置
     */
    public TemplateConfig templateConfig = new TemplateConfig();

    /**
     * 策略配置
     */
    public StrategyConfig strategyConfig;
}
