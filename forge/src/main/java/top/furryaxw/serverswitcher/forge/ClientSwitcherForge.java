package top.furryaxw.serverswitcher.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.furryaxw.serverswitcher.ClientSwitcher;

@Mod(ClientSwitcher.MOD_ID)
public class ClientSwitcherForge {
    public ClientSwitcherForge() {
        // Architectury 需要挂载事件总线
        EventBuses.registerModEventBus(ClientSwitcher.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // 注册客户端初始化
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // 初始化 Common 逻辑
        ClientSwitcher.init();
    }
}