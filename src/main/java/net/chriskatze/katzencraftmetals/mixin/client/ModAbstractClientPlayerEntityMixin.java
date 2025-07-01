package net.chriskatze.katzencraftmetals.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayerEntity.class)
public class ModAbstractClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void allowCameraWhileSleeping(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.player.isSleeping()) {
            if (client.mouse.isCursorLocked()) {
                client.mouse.unlockCursor();
            }

            // Optionally, force camera entity to player (usually it is)
            client.setCameraEntity(client.player);

            // Clamp head rotation (optional, adjust as needed)
            AbstractClientPlayerEntity player = client.player;
            float baseYaw = player.prevYaw;
            float basePitch = player.prevPitch;

            float maxYawTurn = 30f;
            float maxPitchTurn = 20f;

            float clampedYaw = MathHelper.clamp(player.getYaw(), baseYaw - maxYawTurn, baseYaw + maxYawTurn);
            float clampedPitch = MathHelper.clamp(player.getPitch(), basePitch - maxPitchTurn, basePitch + maxPitchTurn);

            player.setYaw(clampedYaw);
            player.setPitch(clampedPitch);
        }
    }
}