package top.furryaxw.serverswitcher;

import top.furryaxw.serverswitcher.config.ConfigManager;

public class ClientSwitcher {
    public static final String MOD_ID = "serverswitcher";

    public static void init() {
        // 1. 加载配置
        ConfigManager.load();

        System.out.println("[ClientSwitcher] 初始化完成。");
    }
}
