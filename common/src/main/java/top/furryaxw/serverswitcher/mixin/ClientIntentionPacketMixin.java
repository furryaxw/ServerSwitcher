package top.furryaxw.serverswitcher.mixin;

import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.furryaxw.serverswitcher.config.ConfigManager;

@Mixin(ClientIntentionPacket.class)
public class ClientIntentionPacketMixin {

    /**
     * 拦截 ClientIntentionPacket 的构造参数 hostName。
     */
    @ModifyVariable(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/protocol/handshake/ClientIntent;)V", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static String modifyHostName(String hostName) {
        String target = ConfigManager.getTargetServer();
        // 只有当配置了 target 且不为空时才追加
        if (target != null && !target.isEmpty()) {
            return hostName + "$" + target;
        }
        return hostName;
    }
}