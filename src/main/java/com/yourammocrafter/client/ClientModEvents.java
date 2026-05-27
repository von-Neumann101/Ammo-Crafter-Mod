package com.yourammocrafter.client;

import com.yourammocrafter.YourAmmoCrafterMod;
import com.yourammocrafter.client.screen.AmmoCrafterScreen;
import com.yourammocrafter.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = YourAmmoCrafterMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.AMMO_CRAFTER.get(), AmmoCrafterScreen::new);
    }
}
