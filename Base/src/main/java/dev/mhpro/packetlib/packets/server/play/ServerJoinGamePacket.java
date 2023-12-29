package dev.mhpro.packetlib.packets.server.play;

import dev.mhpro.packetlib.data.Position;
import dev.mhpro.packetlib.enums.ProtocolVersion;
import dev.mhpro.packetlib.mapping.ProtocolMapping;
import dev.mhpro.packetlib.mapping.VersionMapping;
import dev.mhpro.packetlib.objects.world.Dimension;
import dev.mhpro.packetlib.objects.world.DimensionRegistry;
import dev.mhpro.packetlib.objects.world.World;
import dev.mhpro.packetlib.objects.world.enums.Difficulty;
import dev.mhpro.packetlib.objects.world.enums.DimensionType;
import dev.mhpro.packetlib.objects.world.enums.GameMode;
import dev.mhpro.packetlib.objects.world.enums.LevelType;
import dev.mhpro.packetlib.packets.Packet;
import dev.mhpro.packetlib.packets.PacketBuffer;
import lombok.*;


@Data
@With
@Builder
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@NoArgsConstructor
public class ServerJoinGamePacket implements Packet {
    @ToString.Exclude
    private final ProtocolMapping mapping = new ProtocolMapping(
            new VersionMapping<>(0x01, ProtocolVersion.v1_8),
            new VersionMapping<>(0x23, new VersionMapping<>(ProtocolVersion.v1_9, ProtocolVersion.v1_12_2), ProtocolVersion.v1_19),
            new VersionMapping<>(0x25, new VersionMapping<>(ProtocolVersion.v1_13, ProtocolVersion.v1_16_1), ProtocolVersion.v1_19_2),
            new VersionMapping<>(0x26, VersionMapping.v1_15, VersionMapping.v1_17, VersionMapping.v1_18),
            new VersionMapping<>(0x24, ProtocolVersion.v1_16_2, ProtocolVersion.v1_19_3),
            new VersionMapping<>(0x28, ProtocolVersion.v1_19_4, ProtocolVersion.v1_20)
    );
    private int entityID;
    @Builder.Default
    private GameMode gameMode = GameMode.Survival, previousGameMode = GameMode.Undefined;
    private World[] worlds;
    @Builder.Default
    private int viewDistance = 16;
    @Builder.Default
    private int simulationDistance = 16;
    private boolean reducedDebugInfo;
    @Builder.Default
    private boolean enableRespawnScreen = true;
    private boolean debug;
    private World deathWorld;
    @Builder.Default
    private Position deathLocation = null;
    private World currentWorld;
    private int portalCooldown;

    @Override
    public Packet clone() throws CloneNotSupportedException {
        return (Packet) super.clone();
    }

    @Override
    public void read(PacketBuffer input, ProtocolVersion version, int packetId) {
        this.entityID = input.readVarInt();

        this.currentWorld = new World();

        switch (version) {
            case v1_8:
            case v1_9:
                this.gameMode = this.previousGameMode = GameMode.getById(input.readUnsignedByte());
                this.currentWorld.getSettings().setType(DimensionType.getById(input.readByte()));
                this.currentWorld.getSettings().setDifficulty(Difficulty.values()[input.readUnsignedByte()]);
                this.currentWorld.setMaxPlayers(input.readUnsignedByte());
                this.currentWorld.getSettings().setLevelType(LevelType.valueOf(input.readString().toUpperCase()));
                this.reducedDebugInfo = input.readBoolean();
                this.currentWorld.getSettings().setName(this.currentWorld.getSettings().getType().name().toLowerCase());
                break;

            case v1_12_2:
            case v1_9_1:
            case v1_12_1:
            case v1_13:
            case v1_10:
            case v1_13_2:
            case v1_11:
            case v1_9_2:
            case v1_12:
            case v1_13_1:
            case v1_9_4:
                this.gameMode = this.previousGameMode = GameMode.getById(input.readUnsignedByte());
                this.currentWorld.getSettings().setType(DimensionType.getById(input.readByte()));
                this.currentWorld.getSettings().setDifficulty(Difficulty.values()[input.readUnsignedByte()]);
                this.currentWorld.setMaxPlayers(input.readUnsignedByte());
                this.currentWorld.getSettings().setLevelType(LevelType.valueOf(input.readString().toUpperCase()));
                this.reducedDebugInfo = input.readBoolean();

            case v1_14:
            case v1_14_4:
            case v1_14_1:
            case v1_14_3:
                this.gameMode = this.previousGameMode = GameMode.getById(input.readUnsignedByte());
                this.currentWorld.getSettings().setType(DimensionType.getById(input.readInt()));
                this.currentWorld.setMaxPlayers(input.readUnsignedByte());
                this.currentWorld.getSettings().setLevelType(LevelType.valueOf(input.readString().toUpperCase()));
                this.viewDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                break;

            case v1_15_2:
            case v1_15_1:
            case v1_15:
                this.gameMode = this.previousGameMode = GameMode.getById(input.readUnsignedByte());
                this.currentWorld.getSettings().setType(DimensionType.getById(input.readInt()));
                this.currentWorld.setHashedSeed(input.readLong());
                this.currentWorld.getSettings().setDifficulty(Difficulty.values()[input.readUnsignedByte()]);
                this.currentWorld.getSettings().setLevelType(LevelType.valueOf(input.readString().toUpperCase()));
                this.viewDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                this.enableRespawnScreen = input.readBoolean();
                break;

            case v1_16:
            case v1_16_1:
                this.gameMode = GameMode.getById(input.readUnsignedByte());
                this.previousGameMode = GameMode.getById(input.readUnsignedByte());

                this.worlds = new World[input.readVarInt()];
                for (int i = 0; i < this.worlds.length; i++) {
                    this.worlds[i] = new World();
                    this.worlds[i].getSettings().setName(input.readString());
                }

                this.currentWorld.setRegistry(new DimensionRegistry(version, input.readTag()));
                this.currentWorld.getSettings().setType(DimensionType.getByName(input.readString()));
                this.currentWorld.getSettings().setName(input.readString());
                this.currentWorld.setHashedSeed(input.readLong());
                this.currentWorld.setMaxPlayers(input.readUnsignedByte());
                this.viewDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                this.enableRespawnScreen = input.readBoolean();
                this.debug = input.readBoolean();
                this.currentWorld.getSettings().setLevelType(input.readBoolean() ? LevelType.FLAT : LevelType.DEFAULT);
                break;

            case v1_16_2:
                this.currentWorld.setHardcore(input.readBoolean());
                this.gameMode = GameMode.getById(input.readUnsignedByte());
                this.previousGameMode = GameMode.getById(input.readByte());

                this.worlds = new World[input.readVarInt()];
                for (int i = 0; i < this.worlds.length; i++) {
                    this.worlds[i] = new World();
                    this.worlds[i].getSettings().setName(input.readString());
                }


                this.currentWorld.setRegistry(new DimensionRegistry(version, input.readTag()));
                this.currentWorld.setData(new Dimension(input.readTag()));
                this.currentWorld.getSettings().setName(input.readString());
                this.currentWorld.setHashedSeed(input.readLong());
                this.currentWorld.setMaxPlayers(input.readVarInt());
                this.viewDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                this.enableRespawnScreen = input.readBoolean();
                this.debug = input.readBoolean();
                this.currentWorld.getSettings().setLevelType(input.readBoolean() ? LevelType.FLAT : LevelType.DEFAULT);
                break;

            case v1_17_1:
            case v1_17:
                this.currentWorld.setHardcore(input.readBoolean());
                this.gameMode = GameMode.getById(input.readUnsignedByte());
                this.previousGameMode = GameMode.getById(input.readUnsignedByte());


                this.worlds = new World[input.readVarInt()];
                for (int i = 0; i < this.worlds.length; i++) {
                    this.worlds[i] = new World();
                    this.worlds[i].getSettings().setName(input.readString());
                }


                this.currentWorld.setRegistry(new DimensionRegistry(version, input.readTag()));
                this.currentWorld.setData(new Dimension(input.readTag()));
                this.currentWorld.getSettings().setName(input.readString());
                this.currentWorld.setHashedSeed(input.readLong());
                this.currentWorld.setMaxPlayers(input.readVarInt());
                this.viewDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                this.enableRespawnScreen = input.readBoolean();
                this.debug = input.readBoolean();
                this.currentWorld.getSettings().setLevelType(input.readBoolean() ? LevelType.FLAT : LevelType.DEFAULT);

                for (World world : worlds) {
                    world.setRegistry(this.currentWorld.getRegistry());
                    world.setMaxPlayers(this.currentWorld.getMaxPlayers());
                    world.setSettings(world.getSettings());
                }

                break;


            case v1_18_2:
            case v1_18:
                this.currentWorld.setHardcore(input.readBoolean());
                this.gameMode = GameMode.getById(input.readUnsignedByte());
                this.previousGameMode = GameMode.getById(input.readUnsignedByte());

                this.worlds = new World[input.readVarInt()];
                for (int i = 0; i < this.worlds.length; i++) {
                    this.worlds[i] = new World();
                    this.worlds[i].getSettings().setName(input.readString());
                }

                this.currentWorld.setRegistry(new DimensionRegistry(version, input.readTag()));
                this.currentWorld.setData(new Dimension(input.readTag()));
                this.currentWorld.getSettings().setName(input.readString());
                this.currentWorld.setHashedSeed(input.readLong());
                this.currentWorld.setMaxPlayers(input.readVarInt());
                this.viewDistance = input.readVarInt();
                this.simulationDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                this.enableRespawnScreen = input.readBoolean();
                this.debug = input.readBoolean();
                this.currentWorld.getSettings().setLevelType(input.readBoolean() ? LevelType.FLAT : LevelType.DEFAULT);
                break;

            case v1_19_4:
            case v1_19_3:
            case v1_19_2:
            case v1_19:
            case v1_20:
                this.currentWorld.setHardcore(input.readBoolean());
                this.gameMode = GameMode.getById(input.readUnsignedByte());
                this.previousGameMode = GameMode.getById(input.readByte());

                this.worlds = new World[input.readVarInt()];
                for (int i = 0; i < this.worlds.length; i++) {
                    this.worlds[i] = new World();
                    this.worlds[i].getSettings().setName(input.readString());
                }

                this.currentWorld.setRegistry(new DimensionRegistry(version, input.readTag()));
                this.currentWorld.getSettings().setType(DimensionType.getByName(input.readString()));
                this.currentWorld.getSettings().setName(input.readString());
                this.currentWorld.setHashedSeed(input.readLong());
                this.currentWorld.setMaxPlayers(input.readVarInt());
                this.viewDistance = input.readVarInt();
                this.simulationDistance = input.readVarInt();
                this.reducedDebugInfo = input.readBoolean();
                this.enableRespawnScreen = input.readBoolean();
                this.debug = input.readBoolean();
                this.currentWorld.getSettings().setLevelType(input.readBoolean() ? LevelType.FLAT : LevelType.DEFAULT);

                if (input.readBoolean()) {
                    this.deathWorld = new World();
                    this.deathWorld.getSettings().setName(input.readString());
                    this.deathLocation = input.readPosition();
                }

                if (version == ProtocolVersion.v1_20) {
                    this.portalCooldown = input.readVarInt();
                }

                break;
        }


        for (World world : worlds) {
            world.setHardcore(this.currentWorld.isHardcore());
            world.setRegistry(this.currentWorld.getRegistry());
            world.setSettings(this.currentWorld.getSettings());
            world.setMaxPlayers(this.currentWorld.getMaxPlayers());
        }
    }

    @Override
    public void write(PacketBuffer output, ProtocolVersion version, int packetId) {
        output.writeInt(this.entityID);

        switch (version) {
            case v1_8:
            case v1_9:
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeByte(this.currentWorld.getSettings().getType().getId());
                output.writeUnsignedByte(this.currentWorld.getSettings().getDifficulty().ordinal());
                output.writeUnsignedByte(this.currentWorld.getMaxPlayers());
                output.writeString(this.currentWorld.getSettings().getLevelType().name());
                output.writeBoolean(this.reducedDebugInfo);

                break;

            case v1_13_1:
            case v1_13_2:
            case v1_12:
            case v1_9_1:
            case v1_12_2:
            case v1_11:
            case v1_9_4:
            case v1_12_1:
            case v1_9_2:
            case v1_13:
            case v1_10:
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeInt(currentWorld.getSettings().getType().getId());
                output.writeUnsignedByte(this.currentWorld.getSettings().getDifficulty().ordinal());
                output.writeUnsignedByte(this.currentWorld.getMaxPlayers());
                output.writeString(this.currentWorld.getSettings().getLevelType().name());
                output.writeBoolean(this.reducedDebugInfo);

                break;

            case v1_14_1:
            case v1_14:
            case v1_14_4:
            case v1_14_3:
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeInt(currentWorld.getSettings().getType().getId());
                output.writeUnsignedByte(this.currentWorld.getMaxPlayers());
                output.writeString(this.currentWorld.getSettings().getLevelType().name());
                output.writeVarInt(this.viewDistance);
                output.writeBoolean(this.reducedDebugInfo);

                break;

            case v1_15_1:
            case v1_15:
            case v1_15_2:
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeInt(currentWorld.getSettings().getType().getId());
                output.writeLong(currentWorld.getHashSeed());
                output.writeUnsignedByte(this.currentWorld.getMaxPlayers());
                output.writeString(this.currentWorld.getSettings().getLevelType().name());
                output.writeVarInt(this.viewDistance);
                output.writeBoolean(this.reducedDebugInfo);
                output.writeBoolean(this.enableRespawnScreen);

                break;

            case v1_16_1:
            case v1_16:
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeUnsignedByte(this.previousGameMode.getId());

                output.writeVarInt(this.worlds.length);
                for (World w : this.worlds) {
                    output.writeString(w.getName());
                }

                output.writeTag(this.currentWorld.getRegistry().get(version));
                output.writeString(currentWorld.getSettings().getType().toString());// TODO fix this
                output.writeString(this.currentWorld.getName());
                output.writeLong(currentWorld.getHashSeed());
                output.writeUnsignedByte(this.currentWorld.getMaxPlayers());
                output.writeVarInt(this.viewDistance);
                output.writeBoolean(this.reducedDebugInfo);
                output.writeBoolean(this.enableRespawnScreen);
                output.writeBoolean(this.debug);
                output.writeBoolean(this.currentWorld.getSettings().getLevelType() == LevelType.FLAT);

                break;

            case v1_16_2:
                output.writeBoolean(this.getCurrentWorld().isHardcore());
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeUnsignedByte(this.previousGameMode.getId());

                output.writeVarInt(this.worlds.length);
                for (World w : this.worlds) {
                    output.writeString(w.getName());
                }

                output.writeTag(this.currentWorld.getRegistry().get(version));
                output.writeTag(this.currentWorld.getData(version).getEntries());
                output.writeString(this.currentWorld.getName());
                output.writeLong(currentWorld.getHashSeed());
                output.writeVarInt(this.currentWorld.getMaxPlayers());
                output.writeVarInt(this.viewDistance);
                output.writeBoolean(this.reducedDebugInfo);
                output.writeBoolean(this.enableRespawnScreen);
                output.writeBoolean(this.debug);
                output.writeBoolean(this.currentWorld.getSettings().getLevelType() == LevelType.FLAT);

                break;

            case v1_17:
            case v1_17_1:
                output.writeBoolean(this.getCurrentWorld().isHardcore());
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeByte(this.previousGameMode.getId());

                output.writeVarInt(this.worlds.length);
                for (World w : this.worlds) {
                    output.writeString(w.getName());
                }

                output.writeTag(this.currentWorld.getRegistry().get(version));
                output.writeTag(this.currentWorld.getData(version).getEntries());
                output.writeString(this.currentWorld.getName());
                output.writeLong(currentWorld.getHashSeed());
                output.writeVarInt(this.currentWorld.getMaxPlayers());
                output.writeVarInt(this.viewDistance);
                output.writeBoolean(this.reducedDebugInfo);
                output.writeBoolean(this.enableRespawnScreen);
                output.writeBoolean(this.debug);
                output.writeBoolean(this.currentWorld.getSettings().getLevelType() == LevelType.FLAT);

                break;

            case v1_18_2:
            case v1_18:
                output.writeBoolean(this.getCurrentWorld().isHardcore());
                output.writeUnsignedByte(this.gameMode.getId());
                output.writeByte(this.previousGameMode.getId());

                output.writeVarInt(this.worlds.length);
                for (World w : this.worlds) {
                    output.writeString(w.getName());
                }

                output.writeTag(this.currentWorld.getRegistry().get(version));
                output.writeTag(this.currentWorld.getData(version).getEntries());
                output.writeString(this.currentWorld.getName());
                output.writeLong(currentWorld.getHashSeed());
                output.writeVarInt(this.currentWorld.getMaxPlayers());
                output.writeVarInt(this.viewDistance);
                output.writeVarInt(this.simulationDistance);
                output.writeBoolean(this.reducedDebugInfo);
                output.writeBoolean(this.enableRespawnScreen);
                output.writeBoolean(this.debug);
                output.writeBoolean(this.currentWorld.getSettings().getLevelType() == LevelType.FLAT);

                break;

            case v1_19_3:
            case v1_19_4:
            case v1_19:
            case v1_20:
            case v1_19_2:
                output.writeBoolean(this.getCurrentWorld().isHardcore());
                output.writeByte(this.gameMode.getId());
                output.writeByte(this.previousGameMode.getId());

                output.writeVarInt(this.worlds.length);
                for (World w : this.worlds) {
                    output.writeString(w.getName());
                }

                output.writeTag(this.currentWorld.getRegistry().get(version));

                output.writeString(this.currentWorld.getSettings().getType().toString());
                output.writeString(this.currentWorld.getName());
                output.writeLong(this.currentWorld.getHashSeed());
                output.writeVarInt(this.currentWorld.getMaxPlayers());
                output.writeVarInt(this.viewDistance);
                output.writeVarInt(this.simulationDistance);
                output.writeBoolean(this.reducedDebugInfo);
                output.writeBoolean(this.enableRespawnScreen);
                output.writeBoolean(this.debug);
                output.writeBoolean(this.currentWorld.getSettings().getLevelType() == LevelType.FLAT);
                output.writeBoolean(deathLocation != null);

                if (deathLocation != null) {
                    output.writeString(this.deathWorld.getName());
                    output.writePosition(this.deathLocation);
                }

                if (version == ProtocolVersion.v1_20){
                    output.writeVarInt(this.portalCooldown);
                }

                break;

        }


    }

}
