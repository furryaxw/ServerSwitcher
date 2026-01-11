package top.furryaxw.serverswitcher;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
// 注意: 1.16.5 下如果是 Yarn 映射可能需要 net.minecraft.util.Identifier
// 这里假设使用 Mojmap 或者 Architectury 的自动重映射

import top.furryaxw.serverswitcher.config.ConfigManager;

public class ClientSwitcher {
    public static final String MOD_ID = "clientswitcher";
    // 定义通道名称
    public static final ResourceLocation CHANNEL = new ResourceLocation("furryaxw", "switcher");

    public static void init() {
        // 1. 加载配置
        ConfigManager.load();

        // 2. 注册网络通道 (关键：这会发送 REGISTER 包给代理，让代理知道我们在监听/发送此通道)
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL, (buf, context) -> {
            // 我们只发不收，这里留空或者是打印调试信息
        });

        // 3. 注册玩家加入服务器事件
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            sendSwitchPacket();
        });
    }

    private static void sendSwitchPacket() {
        String target = ConfigManager.getTargetServer();
        if (target == null || target.isEmpty()) return;

        // 构建数据包
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(target); // 写入 UTF-8 字符串

        // 发送数据包到服务端 (Velocity 会拦截)
        NetworkManager.sendToServer(CHANNEL, buf);

        System.out.println("[ClientSwitcher] Requesting switch to server: " + target);
    }
}