package net.chriskatze.katzencraftmetals.mixin.client;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public class ModClientPlayerEntityMixin {

    private boolean wasSleeping = false; // track previous tick sleep state

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;

        boolean isSleepingNow = player.isSleeping();

        if (isSleepingNow) {
            // Player is sleeping — unlock cursor and clamp camera rotation
            if (client.mouse.isCursorLocked()) {
                client.mouse.unlockCursor();
            }

            float baseYaw = player.prevYaw;
            float basePitch = player.prevPitch;
            float maxYawTurn = 30f;
            float maxPitchTurn = 20f;

            float clampedYaw = MathHelper.clamp(player.getYaw(), baseYaw - maxYawTurn, baseYaw + maxYawTurn);
            float clampedPitch = MathHelper.clamp(player.getPitch(), basePitch - maxPitchTurn, basePitch + maxPitchTurn);

            player.setYaw(clampedYaw);
            player.setPitch(clampedPitch);
        } else if (wasSleeping) {
            // Player just woke up this tick — fix bed and unlock cursor
            client.mouse.unlockCursor();
            client.setCameraEntity(player);

            Optional<BlockPos> sleepingPosOpt = player.getSleepingPosition();
            if (sleepingPosOpt.isPresent()) {
                BlockPos bedPos = sleepingPosOpt.get();
                if (client.world != null && client.world.getBlockState(bedPos).getBlock() instanceof BedBlock) {
                    client.world.setBlockState(bedPos, client.world.getBlockState(bedPos).with(Properties.OCCUPIED, false), 3);

                    // Teleport player one block away from bed to avoid getting stuck
                    BlockPos safePos = bedPos.add(1, 0, 0);
                    player.updatePositionAndAngles(
                            safePos.getX() + 0.5,
                            safePos.getY(),
                            safePos.getZ() + 0.5,
                            player.getYaw(),
                            player.getPitch()
                    );
                }
            }
        }

        wasSleeping = isSleepingNow; // update for next tick
    }
}