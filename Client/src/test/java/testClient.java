import dev.mhpro.packetlib.MinecraftClient;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.events.EventHandler;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.client.handshake.ClientHandshake;

public class testClient {
    public static void main(String[] args) {
        try (MinecraftClient client = MinecraftClient.builder()
                .name("Test1234")
                .version(ProtocolVersion.v1_8)
                .build()) {
            client.event(new EventHandler() {
                @Override
                public void onPacketReceive(PacketEvent event) {
                    System.out.println(event.getPacket());
                }

                @Override
                public void onPacketSent(PacketEvent event) {
                    Packet packet = event.getPacket();
                    if (packet instanceof ClientHandshake){
                        System.out.println(((ClientHandshake) packet).getState());
                    }
                    System.out.println(event.getPacket());
                }
            });

            System.out.println("Connecting...");

            client.connect("127.0.0.1", 25566);
        }
    }
}
