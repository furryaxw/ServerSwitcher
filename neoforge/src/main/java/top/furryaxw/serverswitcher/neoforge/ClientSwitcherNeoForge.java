package top.furryaxw.serverswitcher.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.furryaxw.serverswitcher.ClientSwitcher;

@Mod(ClientSwitcher.MOD_ID)
public class ClientSwitcherNeoForge {
    public ClientSwitcherNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ClientSwitcher.init();
    }
}
