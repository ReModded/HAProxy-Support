package net.remodded.haproxy.mixin;

import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import net.remodded.haproxy.HAProxySupport;
import net.remodded.haproxy.handler.ProxyProtocolHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener$1")
public class ProxyProtocolImplementation {

    /**
     * @author DEv0on
     * @reason Implement HAProxy
     */
    @Inject(method = "initChannel", at = @At("TAIL"), remap = false)
    public void initChannel(Channel channel, CallbackInfo ci) {
        if (!HAProxySupport.enableProxyProtocol)
            return;

        channel.pipeline()
                .addAfter("timeout", "haproxy-decoder", new HAProxyMessageDecoder())
                .addAfter("haproxy-decoder", "haproxy-handler", new ProxyProtocolHandler());
    }
}