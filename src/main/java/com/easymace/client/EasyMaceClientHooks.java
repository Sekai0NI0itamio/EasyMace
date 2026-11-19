package com.easymace.client;

import com.easymace.EasyMaceMod;
import com.easymace.config.EasyMaceConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class EasyMaceClientHooks {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("easymace")
                .then(ClientCommandManager.literal("reload").executes(ctx -> {
                    EasyMaceConfig.load();
                    ctx.getSource().sendFeedback(Text.literal("[EasyMace] Config reloaded."));
                    return 1;
                }))
                .then(ClientCommandManager.literal("status").executes(ctx -> {
                    EasyMaceConfig cfg = EasyMaceConfig.get();
                    ctx.getSource().sendFeedback(Text.literal(
                        "[EasyMace] enabled=" + cfg.enabled +
                        " slamThreshold=" + cfg.slamHeightThreshold +
                        " hitboxExpand=" + cfg.hitboxExpand +
                        " hitboxAll=" + cfg.hitboxAllEntities));
                    return 1;
                }))
            );
        });
    }
}
