package top.furryaxw.serverswitcher.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    // 默认值，防止读取失败
    private static final String DEFAULT_TARGET = "lobby";
    // 配置文件在 JAR 包内的路径 (注意前面的 / 表示根目录)
    private static final String CONFIG_PATH = "/switcher.properties";

    public static String getTargetServer() {
        Properties props = new Properties();

        // getClass() 位于静态上下文中，或者使用 ConfigManager.class
        // getResourceAsStream 会在 JAR 包内部查找文件
        try (InputStream stream = ConfigManager.class.getResourceAsStream(CONFIG_PATH)) {
            if (stream == null) {
                System.err.println("[ClientSwitcher] 警告: 未在 JAR 内找到配置文件 " + CONFIG_PATH + "，使用默认值。");
                return DEFAULT_TARGET;
            }

            // 加载配置
            props.load(stream);

            // 获取值，如果没填则返回默认值
            String target = props.getProperty("target_server", DEFAULT_TARGET);
            return target.trim(); // 去除可能存在的首尾空格

        } catch (IOException e) {
            e.printStackTrace();
            return DEFAULT_TARGET;
        }
    }

    // 因为是只读 JAR 内资源，不需要 save() 方法，也无法写入 JAR
    public static void load() {
        // 可以在这里预热一下，或者直接在 getTargetServer 里实时读取
        // 既然逻辑简单，不做缓存也行，或者把读取结果存个 static 变量
        System.out.println("[ClientSwitcher] Target server configured as: " + getTargetServer());
    }
}