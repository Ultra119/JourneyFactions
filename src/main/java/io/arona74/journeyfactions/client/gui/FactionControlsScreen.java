package io.arona74.journeyfactions.client.gui;

import io.arona74.journeyfactions.JourneyFactions;
import io.arona74.journeyfactions.data.ClientFaction;
import io.arona74.journeyfactions.journeymap.FactionDisplayManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collection;

/**
 * Custom GUI screen for faction controls
 */
public class FactionControlsScreen extends Screen {
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 25;
    
    private Button toggleButton;
    private Button closeButton;
    
    private int factionCount = 0;
    private int totalChunks = 0;
    
    public FactionControlsScreen() {
        super(Component.literal("Faction Controls"));
        updateFactionStats();
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        
        // Toggle display button
        this.toggleButton = Button.builder(
            getToggleButtonText(),
            button -> toggleFactionDisplay()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        
        // Close button
        this.closeButton = Button.builder(
            Component.literal("Close"),
            button -> this.onClose()
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + BUTTON_SPACING * 3, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        
        this.addRenderableWidget(this.toggleButton);
        this.addRenderableWidget(this.closeButton);
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
        // Draw background
        this.extractMenuBackground(extractor);
        
        // Draw title
        extractor.centeredText(
            this.font,
            this.title,
            this.width / 2,
            20,
            0xFFFFFF
        );
        
        // Draw faction information
        int infoY = 50;
        String statusLine = String.format("Factions: %d | Claimed Chunks: %d", factionCount, totalChunks);
        extractor.centeredText(
            this.font,
            statusLine,
            this.width / 2,
            infoY,
            0xCCCCCC
        );
        
        // Draw current display status
        String displayStatus = FactionDisplayManager.isFactionDisplayEnabled() ? "Visible" : "Hidden";
        ChatFormatting statusColor = FactionDisplayManager.isFactionDisplayEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED;
        extractor.centeredText(
            this.font,
            Component.literal("Faction Territories: ").append(Component.literal(displayStatus).withStyle(statusColor)),
            this.width / 2,
            infoY + 15,
            0xCCCCCC
        );
        
        // Draw keybinding hint
        extractor.centeredText(
            this.font,
            Component.literal("Tip: Press 'G' key to toggle quickly").withStyle(ChatFormatting.GRAY),
            this.width / 2,
            this.height - 30,
            0x888888
        );
        
        super.extractRenderState(extractor, mouseX, mouseY, delta);
    }
    
    private Component getToggleButtonText() {
        if (FactionDisplayManager.isFactionDisplayEnabled()) {
            return Component.literal("Hide Faction Territories").withStyle(ChatFormatting.RED);
        } else {
            return Component.literal("Show Faction Territories").withStyle(ChatFormatting.GREEN);
        }
    }
    
    private void toggleFactionDisplay() {
        FactionDisplayManager.toggleFactionDisplay();
        
        // Update button text
        this.toggleButton.setMessage(getToggleButtonText());
        
        // Show feedback message
        if (this.minecraft != null && this.minecraft.player != null) {
            String message = FactionDisplayManager.isFactionDisplayEnabled() 
                ? "§eFaction territories: §aShown" 
                : "§eFaction territories: §cHidden";
            
            this.minecraft.player.sendOverlayMessage(Component.literal(message));
        }
    }
    
    private void updateFactionStats() {
        try {
            Collection<ClientFaction> factions = JourneyFactions.getFactionManager().getAllFactions();
            factionCount = 0;
            totalChunks = 0;
            
            for (ClientFaction faction : factions) {
                if (faction.getType() == ClientFaction.FactionType.PLAYER) {
                    factionCount++;
                }
                totalChunks += faction.getClaimedChunks().size();
            }
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}