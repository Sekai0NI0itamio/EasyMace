package com.easymace.mixin;

import com.easymace.config.EasyMaceConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Expands the TARGETING margin (getTargetingMargin / getPickRadius), which is the
 * extra radius added to an entity's bounding box during the crosshair raycast.
 * This is the same hook Meteor Client's Hitboxes module uses, and it does NOT
 * touch the collision box, so blocks/movement stay normal.
 */
@Mixin(Entity.class)
public class EntityHitboxMixin {
    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void easymace$expandTargetingMargin(CallbackInfoReturnable<Float> cir) {
        EasyMaceConfig cfg = EasyMaceConfig.get();
        if (!cfg.hitboxEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        ItemStack mainHand = client.player.getMainHandStack();
        if (mainHand.getItem() != Items.MACE) return;

        Entity self = (Entity) (Object) this;
        if (self instanceof PlayerEntity pe && pe.getUuid().equals(client.player.getUuid())) return;

        if (!cfg.hitboxAllEntities) {
            Identifier typeId = Registries.ENTITY_TYPE.getId(self.getType());
            if (!cfg.hitboxEntities.contains(typeId.toString())) return;
        }

        cir.setReturnValue((float) cfg.hitboxExpand);
    }
}
