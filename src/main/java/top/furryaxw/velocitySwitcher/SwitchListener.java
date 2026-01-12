package top.furryaxw.velocitySwitcher;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.Optional;

public class SwitchListener {

    private final ProxyServer proxy;
    private final Logger logger;
    private final ConfigManager configManager;

    public SwitchListener(ProxyServer proxy, Logger logger, ConfigManager configManager) {
        this.proxy = proxy;
        this.logger = logger;
        this.configManager = configManager;
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        Optional<InetSocketAddress> virtualHost = player.getVirtualHost();

        // 1. 获取玩家连接时的 Hostname
        if (virtualHost.isEmpty()) {
            return;
        }

        String rawHostname = virtualHost.get().getHostString();
        String separator = "$";

        // 2. 检查是否包含分隔符
        if (!rawHostname.contains(separator)) {
            logger.info("Detected vanilla client connection: {} (Hostname: {})", player.getUsername(), rawHostname);
            return;
        }

        // 3. 提取 Key
        // 假设格式为: mc.example.com<SEP>survival
        // 我们取分隔符后的最后一部分
        String requestKey;
        try {
            int lastIndex = rawHostname.lastIndexOf(separator);
            if (lastIndex == -1 || lastIndex + separator.length() >= rawHostname.length()) {
                return;
            }
            requestKey = rawHostname.substring(lastIndex + separator.length()).trim();
        } catch (Exception e) {
            logger.warn("Failed to parse hostname spoofing from {}: {}", player.getUsername(), e.getMessage());
            return;
        }

        if (requestKey.isEmpty()) {
            return;
        }

        logger.info("Player {} connecting with routing key: {}", player.getUsername(), requestKey);

        // 4. 获取目标服务器
        String targetServerName = configManager.getTargetServer(requestKey);
        Optional<RegisteredServer> targetServerOpt = proxy.getServer(targetServerName);

        if (targetServerOpt.isPresent()) {
            // 5. 设置初始服务器 (0-Click Routing)
            event.setInitialServer(targetServerOpt.get());
            logger.info("Route {} -> {}", player.getUsername(), targetServerName);
        } else {
            logger.warn("Player {} requested unknown server via hostname: {} (Mapped from: {})",
                    player.getUsername(), targetServerName, requestKey);
            // 可以在这里决定是否踢出玩家，或者让他们回落到默认服务器(什么都不做即回落)
        }
    }
}