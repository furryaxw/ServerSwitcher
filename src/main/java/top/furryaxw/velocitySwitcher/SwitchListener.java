package top.furryaxw.velocitySwitcher;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SwitchListener {

    private final ProxyServer proxy;
    private final Logger logger;
    private final ConfigManager configManager;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("furryaxw:switcher");

    public SwitchListener(ProxyServer proxy, Logger logger, ConfigManager configManager) {
        this.proxy = proxy;
        this.logger = logger;
        this.configManager = configManager;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        // 1. 验证通道是否匹配
        if (!event.getIdentifier().equals(IDENTIFIER)) {
            return;
        }

        // 2. 验证发送源是否为玩家 (客户端 -> 代理)
        if (!(event.getSource() instanceof Player player)) {
            return;
        }

        // 3. 解析数据 (UTF-8 String)
        byte[] data = event.getData();
        String requestKey = new String(data, StandardCharsets.UTF_8).trim();

        logger.info("Player {} requested switch to: {}", player.getUsername(), requestKey);

        // 4. 获取映射后的服务器名称
        String targetServerName = configManager.getTargetServer(requestKey);
        Optional<RegisteredServer> targetServerOpt = proxy.getServer(targetServerName);

        if (targetServerOpt.isEmpty()) {
            logger.warn("Player {} requested unknown server: {} (Mapped from: {})",
                player.getUsername(), targetServerName, requestKey);
            return;
        }

        RegisteredServer targetServer = targetServerOpt.get();

        // 5. 检查玩家是否已经在该服务器
        Optional<ServerConnection> currentServer = player.getCurrentServer();
        if (currentServer.isPresent() && currentServer.get().getServerInfo().equals(targetServer.getServerInfo())) {
            // 玩家已经在目标服务器，忽略
            return;
        }

        // 6. 执行跨服操作
        player.createConnectionRequest(targetServer).connect().thenAccept(result -> {
            if (result.isSuccessful()) {
                logger.info("Successfully switched {} to {}", player.getUsername(), targetServerName);
            } else {
                logger.warn("Failed to switch {} to {}: {}", player.getUsername(), targetServerName, result.getReasonComponent());
            }
        });
    }
}