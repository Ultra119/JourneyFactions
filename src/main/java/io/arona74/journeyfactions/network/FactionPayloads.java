package io.arona74.journeyfactions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Network payloads for faction data synchronization (Minecraft 26.3 compatible)
 */
public class FactionPayloads {

    public static final String MOD_ID = "factions";

    /**
     * Chunk data for network transfer
     */
    public record ChunkData(int x, int z) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ChunkData> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ChunkData::x,
                ByteBufCodecs.VAR_INT, ChunkData::z,
                ChunkData::new
        );
    }

    /**
     * Single faction data for network transfer
     */
    public record FactionData(
            String id,
            String name,
            String displayName,
            int typeOrdinal,
            boolean hasColor,
            int colorRgb,
            List<ChunkData> chunks
    ) {
        public static final StreamCodec<RegistryFriendlyByteBuf, FactionData> CODEC = new StreamCodec<>() {
            @Override
            public FactionData decode(RegistryFriendlyByteBuf buf) {
                String id = ByteBufCodecs.STRING_UTF8.decode(buf);
                String name = ByteBufCodecs.STRING_UTF8.decode(buf);
                String displayName = ByteBufCodecs.STRING_UTF8.decode(buf);
                int typeOrdinal = ByteBufCodecs.VAR_INT.decode(buf);
                boolean hasColor = buf.readBoolean();
                int colorRgb = ByteBufCodecs.VAR_INT.decode(buf);

                int chunkCount = ByteBufCodecs.VAR_INT.decode(buf);
                List<ChunkData> chunks = new ArrayList<>(chunkCount);
                for (int i = 0; i < chunkCount; i++) {
                    chunks.add(ChunkData.CODEC.decode(buf));
                }

                return new FactionData(id, name, displayName, typeOrdinal, hasColor, colorRgb, chunks);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, FactionData data) {
                ByteBufCodecs.STRING_UTF8.encode(buf, data.id);
                ByteBufCodecs.STRING_UTF8.encode(buf, data.name);
                ByteBufCodecs.STRING_UTF8.encode(buf, data.displayName);
                ByteBufCodecs.VAR_INT.encode(buf, data.typeOrdinal);
                buf.writeBoolean(data.hasColor);
                ByteBufCodecs.VAR_INT.encode(buf, data.colorRgb);

                ByteBufCodecs.VAR_INT.encode(buf, data.chunks.size());
                for (ChunkData chunk : data.chunks) {
                    ChunkData.CODEC.encode(buf, chunk);
                }
            }
        };
    }

    /**
     * Full faction data sync payload (server -> client)
     */
    public record FactionDataSyncPayload(List<FactionData> factions) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<FactionDataSyncPayload> TYPE = 
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "faction_data_sync"));
        public static final StreamCodec<RegistryFriendlyByteBuf, FactionDataSyncPayload> CODEC = new StreamCodec<>() {
            @Override
            public FactionDataSyncPayload decode(RegistryFriendlyByteBuf buf) {
                int count = ByteBufCodecs.VAR_INT.decode(buf);
                List<FactionData> factions = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    factions.add(FactionData.CODEC.decode(buf));
                }
                return new FactionDataSyncPayload(factions);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, FactionDataSyncPayload payload) {
                ByteBufCodecs.VAR_INT.encode(buf, payload.factions.size());
                for (FactionData faction : payload.factions) {
                    FactionData.CODEC.encode(buf, faction);
                }
            }
        };

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Single faction update payload (server -> client)
     */
    public record FactionUpdatePayload(FactionData faction) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<FactionUpdatePayload> TYPE = 
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "faction_update"));
        public static final StreamCodec<RegistryFriendlyByteBuf, FactionUpdatePayload> CODEC = FactionData.CODEC.map(
                FactionUpdatePayload::new,
                FactionUpdatePayload::faction
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Chunk claim payload (server -> client)
     */
    public record ChunkClaimPayload(String factionId, int chunkX, int chunkZ) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ChunkClaimPayload> TYPE = 
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "chunk_claim"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChunkClaimPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ChunkClaimPayload::factionId,
                ByteBufCodecs.VAR_INT, ChunkClaimPayload::chunkX,
                ByteBufCodecs.VAR_INT, ChunkClaimPayload::chunkZ,
                ChunkClaimPayload::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Chunk unclaim payload (server -> client)
     */
    public record ChunkUnclaimPayload(int chunkX, int chunkZ) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ChunkUnclaimPayload> TYPE = 
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "chunk_unclaim"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChunkUnclaimPayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ChunkUnclaimPayload::chunkX,
                ByteBufCodecs.VAR_INT, ChunkUnclaimPayload::chunkZ,
                ChunkUnclaimPayload::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Faction delete payload (server -> client)
     */
    public record FactionDeletePayload(String factionId) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<FactionDeletePayload> TYPE = 
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "faction_delete"));
        public static final StreamCodec<RegistryFriendlyByteBuf, FactionDeletePayload> CODEC = new StreamCodec<>() {
            @Override
            public FactionDeletePayload decode(RegistryFriendlyByteBuf buf) {
                return new FactionDeletePayload(ByteBufCodecs.STRING_UTF8.decode(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, FactionDeletePayload payload) {
                ByteBufCodecs.STRING_UTF8.encode(buf, payload.factionId);
            }
        };

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Client request for faction data (client -> server)
     */
    public record ClientRequestDataPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ClientRequestDataPayload> TYPE = 
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "client_request_data"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientRequestDataPayload> CODEC = StreamCodec.unit(new ClientRequestDataPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
