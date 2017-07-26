package jp.crafterkina.pipes.common;

import jp.crafterkina.pipes.common.capability.CapabilityRegister;
import jp.crafterkina.pipes.common.recipe.brewing.ProcessorAccelerationRecipe;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

@Mod(modid = MOD_ID, dependencies = "required-after:forge@[14.21.1.2404,)", acceptedMinecraftVersions = "[1.12,)")
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
        proxy.registerTileEntities();
        PacketHandler.init();
        CapabilityRegister.register();
    }

    @EventHandler
    private void init(FMLInitializationEvent event){
        ProcessorAccelerationRecipe.register();
        //MinecraftForge.EVENT_BUS.register(AnvilUpdateEventHandler.INSTANCE);
    }

    @EventHandler
    private void postInit(FMLPostInitializationEvent event){
        proxy.registerBlockColors();
        proxy.registerItemColors();
    }
}
