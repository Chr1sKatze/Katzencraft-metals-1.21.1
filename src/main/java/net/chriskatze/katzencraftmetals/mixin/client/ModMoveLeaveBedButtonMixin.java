package net.chriskatze.katzencraftmetals.mixin.client;

import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SleepingChatScreen.class)
public class ModMoveLeaveBedButtonMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void moveLeaveBedButton(CallbackInfo ci) {
        SleepingChatScreen screen = (SleepingChatScreen) (Object) this;

        for (var child : screen.children()) {
            if (child instanceof ButtonWidget button) {
                // You can debug the button label to find the exact text if needed:
                // System.out.println("Button text: " + button.getMessage().getString());

                // Usually the "Leave Bed" button text is "Leave Bed" (or localized) THIS ONLY WORKS IN ENGLISH FOR NOW
                if (button.getMessage().getString().equals("Leave Bed")) {
                    button.setY(192); // Move it higher, adjust '20' as needed
                }
            }
        }
    }
}