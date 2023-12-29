import dev.mhpro.packetlib.MinecraftServer;
import dev.mhpro.packetlib.data.Favicon;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.objects.Player;
import dev.mhpro.packetlib.packets.client.play.ClientChatPacket;
import dev.mhpro.packetlib.packets.client.status.ClientLegacyStatusPing;
import dev.mhpro.packetlib.packets.client.status.ClientStatusRequest;
import dev.mhpro.packetlib.packets.server.status.ServerStatusResponse;
import dev.mhpro.packetlib.utils.Logger;
import lombok.SneakyThrows;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testServer {
    public static void main(String[] args) {
        Logger.info(String.format("Supported Versions: [\"%s\"]",
                Arrays.stream(ProtocolVersion.getVersions())
                        .map(ProtocolVersion::getName).filter(x -> x.contains("."))
                        .collect(Collectors.joining("\", \""))));

        try (MinecraftServer server = MinecraftServer.builder()
                .port(9999)
                .build()) {

            server.loadWorlds(Paths.get("."));

            server.event(new EventHandler() {
                @Override
                public void onPlayerJoin(Player player) {
                    System.out.println(player.getName() + " has been joined!");

//                    player.sendPacket(ServerTitlePacket.builder()
//                            .message(Component.text("kos"))
//                            .action(ServerTitlePacket.Action.SET_TITLE)
//                            .build());
//
////                    player.sendPacket(ServerTitlePacket.builder()
////                            .times(ServerTitlePacket.TitleTimes.builder().fadeIn(20).stay(80).fadeOut(20).build())
////                            .action(ServerTitlePacket.Action.SET_TIMES_AND_DISPLAY)
////                            .build());
                }

                @SneakyThrows
                @Override
                public void onPacketReceive(PacketEvent event) {
                    if (event.getPacket() instanceof ClientStatusRequest || event.getPacket() instanceof ClientLegacyStatusPing) {
                        List<ServerStatusResponse.PlayerList.PingPlayer> playerList = server.getClientList().stream()
                                .filter(Player::isPlaying)
                                .map((p) -> new ServerStatusResponse.PlayerList.PingPlayer(p.getName(), p.getUuid()))
                                .collect(Collectors.toList());

                        event.getPlayer().sendPacket(ServerStatusResponse.builder()
                                .description("xd")
                                .playerList(new ServerStatusResponse.PlayerList(playerList, playerList.size(), playerList.size() + 1))
                                .supportAll(true)
                                .favicon(new Favicon(Paths.get("favicon.png")))
                                .enforcesSecureChat(true)
                                .version(ProtocolVersion.ALL)
                                .build());
                    }
                    if (event.getPacket() instanceof ClientChatPacket) {
                        ClientChatPacket packet = (ClientChatPacket) event.getPacket();
                        System.out.println(packet.getMessage());
                    }

                    System.out.println(event.getPacket());
                }
            });

            server.run();
        }
    }
}
