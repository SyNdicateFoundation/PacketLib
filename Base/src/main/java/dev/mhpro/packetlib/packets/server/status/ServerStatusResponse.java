package dev.mhpro.packetlib.packets.server.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mhpro.packetlib.data.Favicon;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import dev.mhpro.packetlib.util.ComponentColorize;
import lombok.*;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerStatusResponse implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = ProtocolMapping.all(0x0);
    private ProtocolVersion version;
    @Builder.Default
    private ModInfo modInfo = new ModInfo("FML", Collections.emptyList());
    private String description;
    @ToString.Exclude
    private Favicon favicon;
    private PlayerList playerList;
    private boolean enforcesSecureChat;
    private boolean supportAll;

    @Override
    public void write(PacketBuffer output, ProtocolVersion protocolVersion, int packetId) {
        String json = GsonComponentSerializer.gson().serializer().toJson(this.toJson(protocolVersion));
        output.writeString(json);
    }

    public JsonObject toJson(ProtocolVersion protocolVersion) {
        JsonObject object = new JsonObject();
        JsonObject version = new JsonObject();

        if (this.version != null) {
            version.addProperty("name", this.version.getName());
            version.addProperty("protocol", isSupportAll() ? protocolVersion.getProtocol() : this.version.getProtocol());
            object.add("version", version);
        } else {
            version.addProperty("name", protocolVersion.getName());
            version.addProperty("protocol", protocolVersion.getProtocol());
            object.add("version", version);
        }

        if (this.playerList != null) {
            JsonObject players = new JsonObject();

            players.addProperty("max", this.playerList.max);
            players.addProperty("online", this.playerList.online);

            if (this.playerList.getPlayerList() != null){
                JsonArray elements = new JsonArray();

                for (PlayerList.PingPlayer player : this.playerList.getPlayerList()) {
                    JsonObject element = new JsonObject();
                    element.addProperty("name", player.getName());
                    element.addProperty("id", player.getUuid().toString());
                    elements.add(element);
                }


                if (!elements.isEmpty()) {
                    players.add("sample", elements);
                }
            }

            object.add("players", players);
        }

        if (this.description != null && !this.description.isEmpty()) {
            object.add("description",
                    GsonComponentSerializer.gson()
                            .serializeToTree(ComponentColorize.toComponent(this.description)));
        }

        if (favicon != null) {
            object.addProperty("favicon", favicon.getEncoded());
        }

        object.addProperty("status", "success");

        if (modInfo != null) {
            JsonObject modinfo = new JsonObject();
            modinfo.addProperty("type", modInfo.getType());

            JsonArray mods = new JsonArray();
            modInfo.getMods().forEach(mods::add);

            modinfo.add("modList", mods);
            object.add("modinfo", modinfo);

        }

        if (enforcesSecureChat) {
            object.addProperty("enforcesSecureChat", true);
        }

        return object;
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion protocolVersion, int packetId) {
        fromJson(input.readString());
    }

    public void fromJson(String json) {
        JsonObject element = JsonParser.parseString(json).getAsJsonObject();

        if (element.has("version")) {
            JsonObject version = element.get("version").getAsJsonObject();
            this.version = ProtocolVersion.fromProtocol(version.get("protocol").getAsInt());
        }

        if (element.has("players")) {
            JsonObject players = element.get("players").getAsJsonObject();
            this.playerList = new PlayerList(new ArrayList<>(), players.get("online").getAsInt(), players.get("max").getAsInt());

            if (players.has("players")) {
                for (JsonElement sample : players.getAsJsonArray("sample")) {
                    JsonObject object = sample.getAsJsonObject();
                    this.playerList.getPlayerList().add(new PlayerList.PingPlayer(object.get("name").getAsString(), UUID.fromString(object.get("id").getAsString())));
                }
            }

        }

        if (element.has("description")) {
            this.description = LegacyComponentSerializer
                    .legacyAmpersand()
                    .serialize(GsonComponentSerializer.gson()
                            .deserializeFromTree(element.get("description"))
                    );
        }

        if (element.has("favicon")) {
            this.favicon = new Favicon(element.get("favicon").getAsString());
        }

        if (element.has("modinfo")) {
            JsonObject object = element.get("modinfo").getAsJsonObject();
            JsonArray modList = object.get("modList").getAsJsonArray();

            ArrayList<String> arrayList = new ArrayList<>();
            for (JsonElement jsonElement : modList) {
                arrayList.add(jsonElement.getAsString());
            }

            this.modInfo = new ModInfo(object.has("type") ? object.get("type").getAsString() : "FML", arrayList);
        }

        if (element.has("enforcesSecureChat")) {
            this.enforcesSecureChat = element.get("enforcesSecureChat").getAsBoolean();
        }
    }

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class PlayerList {
        private final List<PingPlayer> playerList;
        private final int online, max;

        @Data
        public static class PingPlayer {
            private final String name;
            private final UUID uuid;
        }
    }

    @Data
    public static class ModInfo {
        private final String type;
        private final List<String> mods;
    }
}
