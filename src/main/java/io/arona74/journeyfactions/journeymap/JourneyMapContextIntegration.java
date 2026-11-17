package io.arona74.journeyfactions.journeymap;

import io.arona74.journeyfactions.JourneyFactions;
import io.arona74.journeyfactions.client.gui.FactionControlsScreen;
import journeymap.api.v2.client.fullscreen.ModPopupMenu;
import journeymap.api.v2.client.fullscreen.ThemeButtonDisplay;
import journeymap.api.v2.common.event.FullscreenEventRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Integrates faction controls with JourneyMap's fullscreen map UI
 * using JourneyMap API v2 FullscreenEventRegistry for toolbar buttons and context menu.
 */
public class JourneyMapContextIntegration {

    private static final Identifier FACTION_TOGGLE_ICON =
            Identifier.fromNamespaceAndPath(JourneyFactions.MOD_ID, "textures/gui/faction_toggle.png");
    private static final Identifier FACTION_SETTINGS_ICON =
            Identifier.fromNamespaceAndPath(JourneyFactions.MOD_ID, "textures/gui/faction_settings.png");

    /**
     * Initialize the context integration by subscribing to JourneyMap fullscreen events.
     */
    public static void initialize() {
        FullscreenEventRegistry.ADDON_BUTTON_DISPLAY_EVENT.subscribe(
                JourneyMapContextIntegration.class,
                JourneyFactions.MOD_ID,
                event -> addButtons(event.getThemeButtonDisplay())
        );

        FullscreenEventRegistry.FULLSCREEN_POPUP_MENU_EVENT.subscribe(
                JourneyMapContextIntegration.class,
                JourneyFactions.MOD_ID,
                event -> addPopupMenu(event.getPopupMenu())
        );

        JourneyFactions.debugLog("JourneyMap context integration initialized");
    }

    /**
     * Add faction buttons to JourneyMap's fullscreen map toolbar.
     */
    private static void addButtons(ThemeButtonDisplay buttonDisplay) {
        try {
            if (buttonDisplay == null) {
                return;
            }

            buttonDisplay.addThemeToggleButton(
                    "Hide Factions",
                    "Show Factions",
                    FACTION_TOGGLE_ICON,
                    FactionDisplayManager.isFactionDisplayEnabled(),
                    button -> {
                        FactionDisplayManager.toggleFactionDisplay();
                        button.setToggled(FactionDisplayManager.isFactionDisplayEnabled());
                        showToggleFeedback();
                    }
            );

            buttonDisplay.addThemeButton(
                    "Faction Settings",
                    FACTION_SETTINGS_ICON,
                    button -> openFactionControlsScreen()
            );

            JourneyFactions.debugLog("Added faction buttons to JourneyMap toolbar");
        } catch (Exception e) {
            JourneyFactions.LOGGER.error("Error adding faction buttons: {}", e.getMessage());
        }
    }

    /**
     * Add faction items to JourneyMap's right-click context menu.
     */
    private static void addPopupMenu(ModPopupMenu popupMenu) {
        try {
            if (popupMenu == null) {
                return;
            }

            ModPopupMenu subMenu = popupMenu.createSubItemList("Faction Controls");

            subMenu.addMenuItem(
                    FactionDisplayManager.isFactionDisplayEnabled() ? "Hide Factions" : "Show Factions",
                    blockPos -> {
                        FactionDisplayManager.toggleFactionDisplay();
                        showToggleFeedback();
                    }
            );

            subMenu.addMenuItem(
                    "Faction Settings",
                    blockPos -> openFactionControlsScreen()
            );

            JourneyFactions.debugLog("Added faction items to JourneyMap context menu");
        } catch (Exception e) {
            JourneyFactions.LOGGER.error("Error adding faction menu items: {}", e.getMessage());
        }
    }

    /**
     * Open the faction controls screen.
     */
    private static void openFactionControlsScreen() {
        Minecraft client = Minecraft.getInstance();
        if (client != null) {
            client.execute(() -> client.setScreenAndShow(new FactionControlsScreen()));
        }
    }

    /**
     * Show feedback for toggle action on the action bar.
     */
    private static void showToggleFeedback() {
        Minecraft client = Minecraft.getInstance();
        if (client != null && client.player != null) {
            String message = FactionDisplayManager.isFactionDisplayEnabled()
                    ? "§eFaction territories: §aShown"
                    : "§eFaction territories: §cHidden";
            client.player.sendOverlayMessage(Component.literal(message));
        }
    }

    /**
     * Cleanup method.
     */
    public static void cleanup() {
        FullscreenEventRegistry.ADDON_BUTTON_DISPLAY_EVENT.unsubscribe(
                JourneyMapContextIntegration.class, JourneyFactions.MOD_ID
        );
        FullscreenEventRegistry.FULLSCREEN_POPUP_MENU_EVENT.unsubscribe(
                JourneyMapContextIntegration.class, JourneyFactions.MOD_ID
        );
        JourneyFactions.debugLog("JourneyMap context integration cleanup completed");
    }
}
