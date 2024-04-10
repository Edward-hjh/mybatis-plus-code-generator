package org.example;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

/**
 * @author hejh
 * @date 2024/04/10
 */
@SpringBootApplication
@Slf4j
public class CodeGeneratorApplication implements CommandLineRunner {

    @Autowired
    private SystemConfig systemConfig;

    public static void main(String[] args) {
        SpringApplication.run(CodeGeneratorApplication.class, args);

    }

    @Override
    public void run(String... args) {
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(systemConfig.getGlobalConfig());
        autoGenerator.setDataSource(systemConfig.getDataSourceConfig());
        autoGenerator.setPackageInfo(systemConfig.getPackageConfig());
        autoGenerator.setTemplate(systemConfig.getTemplateConfig());
        autoGenerator.setStrategy(systemConfig.getStrategyConfig());
        autoGenerator.setCfg(injectionConfig(systemConfig.getGlobalConfig(), systemConfig.getPackageConfig(), systemConfig.getTemplateConfig()));
        autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());
        log.debug("{}", autoGenerator);
        autoGenerator.execute();
    }

    /**
     * 注入自定义参数
     */
    public InjectionConfig injectionConfig(GlobalConfig globalConfig, PackageConfig packageConfig, TemplateConfig templateConfig) {
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>(4);
                String parent = packageConfig.getParent();
                if (packageConfig.getModuleName() != null) {
                    parent = parent.replace(StringPool.DOT + packageConfig.getModuleName(), "");
                }
                map.put("parentPackage", parent);
                this.setMap(map);
            }
        };
        final String subFix = ".ftl";
        List<FileOutConfig> fileOutConfigList = new ArrayList<>();
        //controller
        fileOutConfigList.add(new FileOutConfig(templateConfig.getController() + subFix) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return generatorFilePath(globalConfig.getOutputDir(), packageConfig.getParent().replace(StringPool.DOT, StringPool.SLASH),
                        packageConfig.getController(), tableInfo.getControllerName() + StringPool.DOT_JAVA);
            }
        });
        //service
        final String prefix = "I";
        fileOutConfigList.add(new FileOutConfig(templateConfig.getService() + subFix) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String serviceName = tableInfo.getServiceName();
                if(serviceName.startsWith(prefix)){
                    tableInfo.setServiceName(serviceName.substring(1));
                }
                return generatorFilePath(globalConfig.getOutputDir(), packageConfig.getParent().replace(StringPool.DOT, StringPool.SLASH),
                        packageConfig.getService(), tableInfo.getServiceName() + StringPool.DOT_JAVA);
            }
        });
        //entity
        fileOutConfigList.add(new FileOutConfig(templateConfig.getEntity(false) + subFix) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return generatorFilePath(globalConfig.getOutputDir(), packageConfig.getParent().replace(StringPool.DOT, StringPool.SLASH),
                        packageConfig.getEntity(), tableInfo.getEntityName() + StringPool.DOT_JAVA);
            }
        });
        //form，将entity当作form来处理
        fileOutConfigList.add(new FileOutConfig(templateConfig.getEntity(false) + subFix) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return generatorFilePath(globalConfig.getOutputDir(), packageConfig.getParent().replace(StringPool.DOT, StringPool.SLASH),
                        "form", tableInfo.getEntityName() + "Form" + StringPool.DOT_JAVA);
            }
        });
        //mapper.java
        fileOutConfigList.add(new FileOutConfig(templateConfig.getMapper() + subFix) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return generatorFilePath(globalConfig.getOutputDir(), packageConfig.getParent().replace(StringPool.DOT, StringPool.SLASH),
                        packageConfig.getMapper(), tableInfo.getMapperName() + StringPool.DOT_JAVA);
            }
        });
        //mapper.xml
        fileOutConfigList.add(new FileOutConfig(templateConfig.getXml() + subFix) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return generatorFilePath(globalConfig.getOutputDir(), packageConfig.getParent().replace(StringPool.DOT, StringPool.SLASH),
                        packageConfig.getXml(), tableInfo.getXmlName() + StringPool.DOT_XML);
            }
        });

        templateConfig.setController(null).setServiceImpl(null).setXml(null)
                .setServiceImpl(null).setService(null).setEntityKt(null).setMapper(null);
        injectionConfig.setFileOutConfigList(fileOutConfigList);
        return injectionConfig;
    }

    private String generatorFilePath(String... path) {
        return join(Arrays.asList(path), StringPool.SLASH);
    }

    private String join(Collection<String> sourceList, String splitChar) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Iterator<String> var3 = sourceList.iterator(); var3.hasNext(); stringBuffer.append(var3.next())) {
            if (stringBuffer.length() != 0) {
                stringBuffer.append(splitChar);
            }
        }
        return stringBuffer.toString();
    }
}
