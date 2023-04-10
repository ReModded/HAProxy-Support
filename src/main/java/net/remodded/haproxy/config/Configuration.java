package net.remodded.haproxy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.remodded.haproxy.HAProxySupport;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Configuration {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Config loadConfig(File configDir) throws IOException {
        final File file = new File(configDir, HAProxySupport.MODID + ".json");

        if (file.exists()) {
            return saveConfig(file, loadConfigFile(file));
        }

        return saveDefaultConfig(file);
    }

    private static Config loadConfigFile(File configFile) throws IOException {
        final String string = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

        return GSON.fromJson(string, Config.class);
    }

    private static Config saveDefaultConfig(File configFile) throws IOException {
        return saveConfig(configFile, new Config());
    }

    private static Config saveConfig(File configFile, Config config) throws IOException {
        final String string = GSON.toJson(config);

        FileUtils.writeStringToFile(configFile, string, StandardCharsets.UTF_8);

        return config;
    }
}
