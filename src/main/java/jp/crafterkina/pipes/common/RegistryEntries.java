package jp.crafterkina.pipes.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

public class RegistryEntries {
    @ObjectHolder(MOD_ID)
    public static class ITEM{
        public static final Item merchant_phone = null;
        public static final Item pipe = null;
        public static final Item strategy_acceleration = null;
        public static final Item strategy_extraction = null;
    }

    @ObjectHolder(MOD_ID)
    public static class BLOCK{
        public static final Block pipe = null;
    }
}