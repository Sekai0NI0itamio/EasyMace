package com.easymace;

import com.easymace.client.EasyMaceClientHooks;
import com.easymace.config.EasyMaceConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyMaceMod implements ClientModInitializer {
    public static final String MOD_ID = "easymace";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        EasyMaceConfig.load();
        EasyMaceClientHooks.register();
        EasyMaceConfig cfg = EasyMaceConfig.get();
        LOG.info("[EasyMace] initialized: enabled={} slamThreshold={} hitboxExpand={} hitboxAll={}",
                cfg.enabled, cfg.slamHeightThreshold, cfg.hitboxExpand, cfg.hitboxAllEntities);
    }
}
