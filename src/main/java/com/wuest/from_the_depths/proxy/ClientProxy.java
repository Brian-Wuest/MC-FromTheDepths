package com.wuest.from_the_depths.proxy;

import com.wuest.from_the_depths.ClientModRegistry;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.config.ModConfiguration;
import com.wuest.from_the_depths.events.ClientEventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author WuestMan
 */
public class ClientProxy extends CommonProxy {
    public static ClientEventHandler clientEventHandler = new ClientEventHandler();
    public ModConfiguration serverConfiguration = null;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ModRegistry.AddGuis();

        // After all items have been registered and all recipes loaded, register any necessary renderer.
        FromTheDepths.proxy.registerRenderers();

        FromTheDepths.proxy.registerCustomTextureModels();
    }

    @Override
    public void registerRenderers() {
        ClientModRegistry.RegisterSpecialRenderers();
    }

    @Override
    public void registerCustomTextureModels() {
        ClientModRegistry.generateItemModels();
    }

    @Override
    public ModConfiguration getServerConfiguration() {
        if (this.serverConfiguration == null) {
            // Get the server configuration.
            return CommonProxy.proxyConfiguration;
        } else {
            return this.serverConfiguration;
        }
    }
}