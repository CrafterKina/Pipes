package jp.crafterkina.pipes.common;

import net.minecraft.util.ResourceLocation;

import static jp.crafterkina.pipes.common.PipesCore.MOD_ID;

public class CommonProxy {
    protected static ResourceLocation getResourceLocation(String name){
        return new ResourceLocation(MOD_ID, name);
    }

    protected void registerItemModels(){
    }
}
