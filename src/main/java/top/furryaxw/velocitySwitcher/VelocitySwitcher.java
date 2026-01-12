package top.furryaxw.velocitySwitcher;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "velocityswitcher",
        name = "VelocitySwitcher",
        version = "0.1.0",
        description = "Auto-routes players based on client modpack signals",
        authors = {"FurryAxw"}
)
public class VelocitySwitcher {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public VelocitySwitcher(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 1. 加载配置
        ConfigManager configManager = new ConfigManager(dataDirectory, logger);
        configManager.load();

        // 2. 注册 Plugin Message Channel
        ChannelRegistrar registrar = proxy.getChannelRegistrar();
        registrar.register(SwitchListener.IDENTIFIER);
        logger.info("Registered plugin messaging channel: {}", SwitchListener.IDENTIFIER.getId());

        // 3. 注册事件监听器
        proxy.getEventManager().register(this, new SwitchListener(proxy, logger, configManager));

        logger.info("VelocitySwitcher has been enabled!");
    }
}