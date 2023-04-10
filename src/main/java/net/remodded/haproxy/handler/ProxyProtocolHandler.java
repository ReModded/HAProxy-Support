package net.remodded.haproxy.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import net.minecraft.network.Connection;
import net.remodded.haproxy.HAProxySupport;
import net.remodded.haproxy.config.CIDRMatcher;
import net.remodded.haproxy.mixin.ConnectionAccessor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ProxyProtocolHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage message = ((HAProxyMessage) msg);
            if (message.command() == HAProxyCommand.PROXY) {
                final String realAddress = message.sourceAddress();
                final int realPort = message.sourcePort();

                final InetSocketAddress socketAddr = new InetSocketAddress(realAddress, realPort);

                Connection connection = ((Connection) ctx.channel().pipeline().get("packet_handler"));
                SocketAddress proxyAddress = connection.getRemoteAddress();

                if (!HAProxySupport.whitelistedIPs.isEmpty()) {
                    if (proxyAddress instanceof InetSocketAddress) {
                        InetSocketAddress proxySocketAddress = ((InetSocketAddress) proxyAddress);
                        boolean isWhitelistedIP = false;

                        for (CIDRMatcher matcher : HAProxySupport.whitelistedIPs) {
                            if (matcher.matches(proxySocketAddress.getAddress())) {
                                isWhitelistedIP = true;
                                break;
                            }
                        }

                        if (!isWhitelistedIP) {
                            if (ctx.channel().isOpen()) {
                                ctx.disconnect();
                                HAProxySupport.LOGGER.warn("Blocked proxy IP: " + proxySocketAddress + " when tried to connect!");
                            }
                            return;
                        }
                    } else {
                        HAProxySupport.LOGGER.warn("**********************************************************************");
                        HAProxySupport.LOGGER.warn("* Detected other SocketAddress than InetSocketAddress!               *");
                        HAProxySupport.LOGGER.warn("* Please report it with logs to mod author to provide compatibility! *");
                        HAProxySupport.LOGGER.warn("* https://github.com/ReModded/HAProxy-Support/issues                 *");
                        HAProxySupport.LOGGER.warn("**********************************************************************");
                        HAProxySupport.LOGGER.warn(proxyAddress.getClass().toString());
                        HAProxySupport.LOGGER.warn(proxyAddress.toString());
                    }
                }

                ((ConnectionAccessor) connection).setAddress(socketAddr);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
