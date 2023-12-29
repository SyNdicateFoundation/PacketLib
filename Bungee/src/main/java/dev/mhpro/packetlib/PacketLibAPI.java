package dev.mhpro.packetlib;

import dev.mhpro.packetlib.objects.impl.PacketUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;
import java.util.logging.Logger;

public class PacketLibAPI {
    private static PacketLib packetLib;

    public static void callEvent(Event event) {
        packetLib.getPlugin().getProxy().getPluginManager().callEvent(event);
    }

    public static ProxiedPlayer toProxied(UUID uuid) {
        return packetLib.getPlugin().getProxy().getPlayer(uuid);
    }

    public static void init(Plugin plugin) {
        packetLib = new PacketLib(plugin);
        plugin.getProxy().getPluginManager().registerListener(plugin, packetLib);
    }

    public static PacketUser getUser(UUID uuid) {
        ProxiedPlayer proxied = PacketLibAPI.toProxied(uuid);

        if (proxied == null) {
            return null;
        }

        return packetLib.getUser(proxied);
    }

    public static Logger getLogger() {
        return packetLib.getPlugin().getLogger();
    }
}
