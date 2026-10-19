package io.arona74.journeyfactions.journeymap;

import com.mojang.blaze3d.platform.InputConstants;
import io.arona74.journeyfactions.JourneyFactions;
import journeymap.api.v2.client.IClientAPI;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/**
 * Creates JourneyMap GUI integration for faction controls
 */
public class FactionToggleButton {
    
    private static KeyMapping toggleKeyBinding;
    private static boolean isInitialized = false;
    
    /**
     * Initialize the faction controls
     */
    public static void initialize(IClientAPI jmAPI) {
        try {
            setupKeybinding();
            isInitialized = true;
            JourneyFactions.debugLog("Faction controls initialized successfully - Keybinding: 'G' key");
        } catch (Exception e) {
            JourneyFactions.LOGGER.error("Failed to initialize faction controls: {}", e.getMessage());
            isInitialized = false;
        }
    }
    
    /**
     * Set up keybinding with improved error handling
     */
    private static void setupKeybinding() {
        try {
            JourneyFactions.debugLog("Setting up faction toggle keybinding...");
            
            // Create a keybinding for toggling faction display
            toggleKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.journeyfactions.toggle", // Translation key
                InputConstants.KEY_G, // Default to 'G' key
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("journeyfactions", "category")) // Category
            ));
            
            JourneyFactions.debugLog("Keybinding registered: {}", toggleKeyBinding != null ? "SUCCESS" : "FAILED");
            
            if (toggleKeyBinding != null) {
                JourneyFactions.debugLog("Keybinding details: {}", toggleKeyBinding.getTranslatedKeyMessage().getString());
            }
            
            // Register tick event to check for key presses
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                try {
                    if (toggleKeyBinding != null && toggleKeyBinding.consumeClick()) {
                        JourneyFactions.debugLog("'G' key pressed - toggling faction display");
                        toggleFactionDisplay();
                    }
                } catch (Exception e) {
                    JourneyFactions.LOGGER.error("Error in keybinding tick event: {}", e.getMessage());
                }
            });
            
            JourneyFactions.debugLog("Client tick event registered for keybinding monitoring");
            
        } catch (Exception e) {
            JourneyFactions.LOGGER.error("Failed to setup keybinding: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Toggle faction display with comprehensive feedback
     */
    private static void toggleFactionDisplay() {
        try {
            JourneyFactions.debugLog("=== FACTION TOGGLE TRIGGERED ===");
            
            boolean wasEnabled = FactionDisplayManager.isFactionDisplayEnabled();
            JourneyFactions.debugLog("Previous state: {}", wasEnabled ? "VISIBLE" : "HIDDEN");
            
            // Perform the toggle
            FactionDisplayManager.toggleFactionDisplay();
            
            boolean isNowEnabled = FactionDisplayManager.isFactionDisplayEnabled();
            JourneyFactions.debugLog("New state: {}", isNowEnabled ? "VISIBLE" : "HIDDEN");
            
            // Show message to player
            try {
                net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
                if (client != null && client.player != null) {
                    String message = String.format("§eFaction territories: %s", 
                        isNowEnabled ? "§aShown" : "§cHidden");
                    
                    client.player.sendOverlayMessage(
                        net.minecraft.network.chat.Component.literal(message)
                    );
                    
                    client.player.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("[JourneyFactions] " + message)
                    );
                    
                    JourneyFactions.debugLog("Sent toggle message to player");
                } else {
                    JourneyFactions.debugLog("No player available for toggle message");
                }
            } catch (Exception e) {
                JourneyFactions.LOGGER.error("Could not send player message: {}", e.getMessage());
            }
            
            JourneyFactions.debugLog("=== FACTION TOGGLE COMPLETE ===");
            
        } catch (Exception e) {
            JourneyFactions.LOGGER.error("Error during faction toggle: {}", e.getMessage());
        }
    }
    
    /**
     * Check if keybinding is working
     */
    public static void testKeybinding() {
        JourneyFactions.debugLog("=== KEYBINDING TEST ===");
        JourneyFactions.debugLog("Initialized: {}", isInitialized);
        JourneyFactions.debugLog("Keybinding object: {}", toggleKeyBinding != null ? "EXISTS" : "NULL");
        
        if (toggleKeyBinding != null) {
            JourneyFactions.debugLog("Bound key: {}", toggleKeyBinding.getTranslatedKeyMessage().getString());
            JourneyFactions.debugLog("Category: {}", toggleKeyBinding.getCategory());
        }
        
        JourneyFactions.debugLog("Try pressing the key now and watch the logs");
        JourneyFactions.debugLog("=== END KEYBINDING TEST ===");
    }
    
    /**
     * Force trigger toggle (for testing)
     */
    public static void forceTrigger() {
        JourneyFactions.debugLog("=== FORCE TRIGGERING TOGGLE ===");
        toggleFactionDisplay();
    }
    
    /**
     * Manual toggle method for debug commands
     */
    public static void manualToggle() {
        JourneyFactions.debugLog("=== MANUAL TOGGLE CALLED ===");
        toggleFactionDisplay();
    }
    
    /**
     * Get status information
     */
    public static String getStatus() {
        String keybind = toggleKeyBinding != null 
            ? toggleKeyBinding.getTranslatedKeyMessage().getString() 
            : "Not available";
        
        return String.format("Faction Toggle: [%s] | Initialized: %s | Status: %s", 
            keybind,
            isInitialized,
            FactionDisplayManager.isFactionDisplayEnabled() ? "VISIBLE" : "HIDDEN");
    }
    
    /**
     * Cleanup method
     */
    public static void cleanup() {
        JourneyFactions.debugLog("FactionToggleButton cleanup completed");
        isInitialized = false;
    }
}