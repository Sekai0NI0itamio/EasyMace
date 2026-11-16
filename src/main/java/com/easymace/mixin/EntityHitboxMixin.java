package com.easymace.mixin;

import com.easymace.config.EasyMaceConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
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

        // Only expand hitboxes when the player is holding a mace
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        ItemStack mainHand = client.player.getMainHandStack();
        if (mainHand.getItem() != Items.MACE) return;

        // Only expand the hitbox of the entity the player is currently targeting
        Entity self = (Entity) (Object) this;
        HitResult crosshair = client.crosshairTarget;
        if (!(crosshair instanceof EntityHitResult entityHit)) return;
        if (entityHit.getEntity() != self) return;

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
