package net.remodded.haproxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.remodded.haproxy.config.CIDRMatcher;
import net.remodded.haproxy.config.Config;
import net.remodded.haproxy.config.Configuration;
import net.remodded.haproxy.config.TCPShieldIntegration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod("haproxy_support")
public class HAProxySupport {

    public static final String MODID = "haproxy_support";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean enableProxyProtocol = false;
    public static Collection<CIDRMatcher> whitelistedIPs = new ArrayList<>();

    public HAProxySupport() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        try {
            Config config = Configuration.loadConfig(FMLPaths.CONFIGDIR.get().toFile());
            if (!config.enableProxyProtocol) {
                LOGGER.info("Proxy Protocol disabled!");
                return;
            }

            LOGGER.info("Proxy Protocol enabled!");

            enableProxyProtocol = config.enableProxyProtocol;
            whitelistedIPs = config.whitelistedIPs
                    .stream()
                    .map(CIDRMatcher::new)
                    .collect(Collectors.toSet());

            if (config.whitelistTCPShieldServers) {
                LOGGER.info("TCPShield integration enabled!");
                whitelistedIPs = Stream
                        .concat(whitelistedIPs.stream(), TCPShieldIntegration.getWhitelistedIPs().stream())
                        .collect(Collectors.toSet());
            }

            LOGGER.info("Using " + whitelistedIPs.size() + " whitelisted IPs: " + whitelistedIPs);
        } catch (IOException e) {
            LOGGER.info("Error loading config file:");
            throw new RuntimeException(e);
        }


    }
}
