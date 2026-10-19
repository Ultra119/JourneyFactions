package io.arona74.journeyfactions;

import io.arona74.journeyfactions.network.FactionPayloads.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JourneyFactionsMain implements ModInitializer {
    public static final String MOD_ID = "journeyfactions";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("JourneyFactions initializing...");

        // Register payload types unconditionally (required for both client and server)
        registerNetworkPayloads();

        // Check if Factions mod is loaded
        if (FabricLoader.getInstance().isModLoaded("factions")) {
            LOGGER.info("Factions mod detected - initializing server-side integration");

            try {
                // Initialize the factions integration for sending data to clients
                io.arona74.journeyfactions.server.JourneyFactionsIntegration.initialize();
                LOGGER.info("JourneyFactions server integration initialized successfully");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize JourneyFactions server integration", e);
            }
        } else {
            LOGGER.info("Factions mod not detected - server-side integration disabled");
            LOGGER.info("Install the Factions mod on the server to enable faction territory syncing");
        }

        LOGGER.info("JourneyFactions initialization complete");
    }

    /**
     * Register all network payload types for Minecraft 26.3.
     */
    private void registerNetworkPayloads() {
        LOGGER.debug("Registering network payload types...");

        PayloadTypeRegistry.clientboundPlay().register(FactionDataSyncPayload.TYPE, FactionDataSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(FactionUpdatePayload.TYPE, FactionUpdatePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ChunkClaimPayload.TYPE, ChunkClaimPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ChunkUnclaimPayload.TYPE, ChunkUnclaimPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(FactionDeletePayload.TYPE, FactionDeletePayload.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(ClientRequestDataPayload.TYPE, ClientRequestDataPayload.CODEC);

        LOGGER.debug("Network payload types registered successfully");
    }
}
