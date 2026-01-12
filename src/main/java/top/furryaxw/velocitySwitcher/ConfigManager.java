package top.furryaxw.velocitySwitcher;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Path dataDirectory;
    private final Logger logger;
    private final Map<String, String> serverMapping = new HashMap<>();

    public ConfigManager(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    public void load() {
        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            Path configPath = dataDirectory.resolve("config.yml");

            // 如果配置不存在，创建默认配置
            if (Files.notExists(configPath)) {
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    } else {
                        // 创建一个空的默认文件
                        Files.createFile(configPath);
                    }
                }
            }

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(configPath)
                    .build();

            CommentedConfigurationNode root = loader.load();

            // 获取 servers 节点下的所有子节点 map
            Map<Object, ? extends CommentedConfigurationNode> rawMap = root.node("servers").childrenMap();
            serverMapping.clear();

            for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : rawMap.entrySet()) {
                String key = entry.getKey().toString();
                // 错误代码: entry.getValue().toString() -> 返回对象结构
                // 修正代码: entry.getValue().getString() -> 返回配置的文本值
                String value = entry.getValue().getString();

                if (value != null) {
                    serverMapping.put(key, value);
                }
            }

            logger.info("Loaded " + serverMapping.size() + " server mappings: " + serverMapping);

        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
        }
    }

    /**
     * 根据客户端发来的 key 获取目标服务器名称
     * @param key 客户端发来的字符串
     * @return 目标服务器名称，如果没有映射则返回 key 本身 (默认行为)
     */
    public String getTargetServer(String key) {
        return serverMapping.getOrDefault(key, key);
    }
}