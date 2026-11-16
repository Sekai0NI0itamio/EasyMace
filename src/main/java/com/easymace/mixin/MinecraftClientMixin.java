package com.easymace.mixin;

import com.easymace.EasyMaceMod;
import com.easymace.config.EasyMaceConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    private static int easymace$originalSlot = -1;
    private static boolean easymace$swapping = false;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void easymace$beforeAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!EasyMaceConfig.get().enabled) return;
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player == null || client.targetedEntity == null) return;
        if (easymace$swapping) return;

        ClientPlayerEntity player = client.player;
        boolean isSlam = player.fallDistance >= (float) EasyMaceConfig.get().slamHeightThreshold;
        String preferred = isSlam
                ? EasyMaceConfig.get().preferredEnchantForSlam
                : EasyMaceConfig.get().preferredEnchantForNormal;

        int maceSlot = easymace$findMaceSlot(player, preferred);
        if (maceSlot == -1) return;

        int currentSlot = player.getInventory().selectedSlot;
        if (currentSlot == maceSlot) return;

        easymace$originalSlot = currentSlot;
        easymace$swapping = true;

        // Swap on client
        player.getInventory().selectedSlot = maceSlot;
        // Send packet to server so server tracks the swap
        if (player.networkHandler != null) {
            player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(maceSlot));
        }
    }

    @Inject(method = "doAttack", at = @At("RETURN"))
    private void easymace$afterAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!easymace$swapping) return;
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player != null && easymace$originalSlot >= 0) {
            client.player.getInventory().selectedSlot = easymace$originalSlot;
            if (client.player.networkHandler != null) {
                client.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(easymace$originalSlot));
            }
        }
        easymace$originalSlot = -1;
        easymace$swapping = false;
    }

    private int easymace$findMaceSlot(ClientPlayerEntity player, String preferredEnchant) {
        PlayerInventory inv = player.getInventory();
        int firstMace = -1;
        int preferredMace = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.getItem() == Items.MACE) {
                if (firstMace == -1) firstMace = i;
                if (preferredMace == -1 && easymace$hasEnchantment(stack, preferredEnchant)) {
                    preferredMace = i;
                }
            }
        }

        if (preferredMace != -1) return preferredMace;
        return firstMace;
    }

    private boolean easymace$hasEnchantment(ItemStack stack, String enchantName) {
        if (enchantName == null || enchantName.isEmpty()) return false;
        ItemEnchantmentsComponent enchantments = stack.get(DataComponentTypes.ENCHANTMENTS);
        if (enchantments == null) return false;
        String lower = enchantName.toLowerCase();
        for (var entry : enchantments.getEnchantmentEntries()) {
            String name = entry.getKey().value().toString().toLowerCase();
            if (name.contains(lower)) return true;
        }
        return false;
    }
}
