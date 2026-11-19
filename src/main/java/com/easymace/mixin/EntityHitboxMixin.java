package com.easymace.mixin;

import com.easymace.config.EasyMaceConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityHitboxMixin {
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void easymace$expandHitbox(CallbackInfoReturnable<Box> cir) {
        EasyMaceConfig cfg = EasyMaceConfig.get();
        if (!cfg.hitboxEnabled) return;
        if (cfg.hitboxMultiplier <= 1.0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        ItemStack mainHand = client.player.getMainHandStack();
        if (mainHand.getItem() != Items.MACE) return;

        Entity self = (Entity) (Object) this;

        // Never expand the local player's hitbox. The integrated server uses a
        // separate ServerPlayerEntity instance, so an identity check against
        // client.player is not enough - compare UUIDs instead. Expanding the
        // player's box makes block collision use a larger box (stand on air).
        if (self.getUuid().equals(client.player.getUuid())) return;

        // Only expand entities within attack range (6 blocks)
        double distSq = client.player.squaredDistanceTo(self);
        if (distSq > 36.0) return;

        // Check if this entity type should be expanded
        if (!cfg.hitboxAllEntities) {
            Identifier typeId = Registries.ENTITY_TYPE.getId(self.getType());
            String idStr = typeId.toString();
            if (!cfg.hitboxEntities.contains(idStr)) return;
        }

        Box box = cir.getReturnValue();
        if (box == null) return;

        double mult = cfg.hitboxMultiplier;
        double expandX = (box.maxX - box.minX) * (mult - 1.0) / 2.0;
        double expandY = (box.maxY - box.minY) * (mult - 1.0) / 2.0;
        double expandZ = (box.maxZ - box.minZ) * (mult - 1.0) / 2.0;

        cir.setReturnValue(box.expand(expandX, expandY, expandZ));
    }
}
