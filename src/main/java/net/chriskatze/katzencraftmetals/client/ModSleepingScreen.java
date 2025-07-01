package net.chriskatze.katzencraftmetals.client;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ModSleepingScreen extends Screen {

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    private final MinecraftClient client;

    public ModSleepingScreen() {
        super(Text.literal("Sleeping"));
        this.client = MinecraftClient.getInstance();
    }

    @Override
    protected void init() {
        int x = (this.width - BUTTON_WIDTH) / 2;
        int y = this.height / 2 + 40;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Leave Bed"), button -> {
            if (client.player != null) {
                // Wake up player (vanilla handles internal state)
                client.player.wakeUp(true, true);

                // Clear bed occupancy manually
                clearBedOccupied(client);

                // Teleport player to safe location near bed
                teleportPlayerOutOfBed(client);

                // Reset camera and unlock cursor on wake-up
                client.setCameraEntity(client.player);
                client.mouse.unlockCursor();
            }
            client.setScreen(null);
        }).dimensions(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private void clearBedOccupied(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        BlockPos bedPos = client.player.getSleepingPosition().orElse(null);
        if (bedPos == null) return;

        BlockState state = client.world.getBlockState(bedPos);
        if (state.getBlock() instanceof BedBlock bedBlock) {
            // Update block state to unoccupied by setting "occupied" property to false
            if (state.contains(BedBlock.OCCUPIED)) {
                client.world.setBlockState(bedPos, state.with(BedBlock.OCCUPIED, false), 3);
            }
        }
    }

    private void teleportPlayerOutOfBed(MinecraftClient client) {
        if (client.player == null) return;

        Optional<BlockPos> sleepingPos = client.player.getSleepingPosition();
        if (sleepingPos.isEmpty()) return;

        BlockPos bedPos = sleepingPos.get();
        BlockPos safePos = bedPos.add(1, 0, 0);

        client.player.updatePositionAndAngles(
                safePos.getX() + 0.5,
                safePos.getY(),
                safePos.getZ() + 0.5,
                client.player.getYaw(),
                client.player.getPitch()
        );
    }

    @Override
    public void removed() {
        // Unlock cursor if screen closes unexpectedly
        client.mouse.unlockCursor();
        super.removed();
    }

    @Override
    public boolean shouldPause() {
        return false; // Don’t pause game when sleeping screen is open
    }
}