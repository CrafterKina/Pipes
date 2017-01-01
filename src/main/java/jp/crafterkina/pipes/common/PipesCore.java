package jp.crafterkina.pipes.common;

import jp.crafterkina.pipes.common.achievement.EnumAchievement;
import jp.crafterkina.pipes.common.capability.CapabilityRegister;
import jp.crafterkina.pipes.common.recipe.vanilla.CraftManager;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

@Mod(modid = MOD_ID, dependencies = "required-after:forge@[1.11-13.19.1.2189,);after:jei@[4.1.1.208,)", acceptedMinecraftVersions = "[1.11],[1.11.2,)")
public enum PipesCore {
    INSTANCE;

    @SuppressWarnings("NullableProblems")
    @SidedProxy(clientSide = "jp.crafterkina.pipes.client.ClientProxy", serverSide = "jp.crafterkina.pipes.server.ServerProxy")
    @Getter
    private static CommonProxy proxy;

    @InstanceFactory
    public static PipesCore getInstance(){
        return INSTANCE;
    }

    @EventHandler
    private void construct(FMLConstructionEvent event){
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @EventHandler
    private void preInit(FMLPreInitializationEvent event){
        CraftManager.INSTANCE.register();
        proxy.registerTileEntities();
        PacketHandler.init();
        CapabilityRegister.register();
    }

    @EventHandler
    private void postInit(FMLPostInitializationEvent event){
        proxy.registerBlockColors();
        proxy.registerItemColors();
        EnumAchievement.registerPage();
    }
}
