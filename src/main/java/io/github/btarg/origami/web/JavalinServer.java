package io.github.btarg.origami.web;

import io.github.btarg.origami.OrigamiMain;
import io.github.btarg.origami.util.ContentPackHelper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JavalinServer {
    private static final File generatedZipFile = new File(OrigamiMain.getInstance().getDataFolder(), "generated/pack.zip");
    private static final String downloadEndpoint = "/dl/";
    public static Javalin javalin;
    public static String resourcePackHash;
    private static boolean isRunning = false;
    private static Integer port;

    public static void initAndServePack(ResourcePack resourcePack) {
        port = Objects.requireNonNullElse((Integer) OrigamiMain.config.get("http-port"), 8008);

        Bukkit.getScheduler().runTaskAsynchronously(OrigamiMain.getInstance(), () -> {
            // Only create new instance if not running
            if (!isRunning) {
                javalin = Javalin.create(config -> {
                    config.startup.showJavalinBanner = false;
                    config.routes.get("/api/helloworld", ctx -> ctx.result("hello world!"));
                    
                    String filenamesAsString = String.join("\n", ContentPackHelper.getAllContentPackNames());
                    config.routes.get("/api/contentpacks", ctx -> ctx.result(filenamesAsString));
                    
                    // Use a path parameter for the dynamic hash
                    config.routes.get(downloadEndpoint + "{hash}", ctx -> {
                        String requestedHash = ctx.pathParam("hash");
                        if (resourcePackHash != null && resourcePackHash.equals(requestedHash)) {
                            try {
                                ctx.result(FileUtils.readFileToByteArray(generatedZipFile)).contentType("application/zip");
                            } catch (IOException e) {
                                e.printStackTrace();
                                ctx.status(500);
                            }
                        } else {
                            ctx.status(404);
                        }
                    });
                }).start(port);
                isRunning = true;
            }

            BuiltResourcePack builtResourcePack = MinecraftResourcePackWriter.minecraft().build(resourcePack);
            resourcePackHash = builtResourcePack.hash();

            try {
                FileUtils.createParentDirectories(generatedZipFile);
                MinecraftResourcePackWriter.minecraft().writeToZipFile(generatedZipFile, resourcePack);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (javalin == null) {
                Bukkit.getLogger().severe("Javalin server not started!");
                return;
            }

            Bukkit.getLogger().info("Hosting resource pack at http://localhost:" + port + downloadEndpoint + resourcePackHash);
        });
    }

    public static void sendResourcePack(Player player) {
        String ipAddress = StringUtils.defaultIfEmpty(Bukkit.getServer().getIp(), "localhost");
        Integer port = Objects.requireNonNullElse((Integer) OrigamiMain.config.get("http-port"), 8008);

        if (resourcePackHash == null || resourcePackHash.isBlank()) {
            player.kick(Component.text("The server is still loading!\nTry rejoining in a second."));
        }

        try {
            player.setResourcePack("http://" + ipAddress + ":" + port + downloadEndpoint + resourcePackHash, resourcePackHash);
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }
}