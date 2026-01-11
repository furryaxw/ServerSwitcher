package top.furryaxw.serverswitcher.fabric;

import net.fabricmc.api.ClientModInitializer;
import top.furryaxw.serverswitcher.ClientSwitcher;

public class ClientSwitcherFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 初始化 Common 逻辑
        ClientSwitcher.init();
    }
}