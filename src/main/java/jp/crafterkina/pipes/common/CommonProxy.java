package jp.crafterkina.pipes.common;

import jp.crafterkina.pipes.common.block.BlockFluidTank;
import jp.crafterkina.pipes.common.block.BlockPipe;
import jp.crafterkina.pipes.common.block.entity.TileEntityFluidTank;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.creativetab.EnumCreativeTab;
import jp.crafterkina.pipes.common.item.ItemFluidTank;
import jp.crafterkina.pipes.common.item.ItemMerchantPhone;
import jp.crafterkina.pipes.common.item.ItemPipe;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyExtraction;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyOneway;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Arrays;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;
import static org.apache.commons.lang3.tuple.Pair.of;

public class CommonProxy{
    protected static ResourceLocation getResourceLocation(String name){
        return new ResourceLocation(MOD_ID, name);
    }

    @SafeVarargs
    private static <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event, Pair<T, String>... targets){
        Arrays.stream(targets).map(t -> t.getLeft().setRegistryName(getResourceLocation(t.getRight()))).forEach(event.getRegistry()::register);
    }

    @SafeVarargs
    private static void register(Class<? extends TileEntity>... te){
        Arrays.stream(te).forEach(t -> GameRegistry.registerTileEntity(t, t.getCanonicalName()));
    }

    @OverridingMethodsMustInvokeSuper
    protected void registerItems(RegistryEvent.Register<Item> event){
        register(event,
                of(new ItemMerchantPhone(), "merchant_phone"),
                of(new ItemPipe(), "pipe"),
                of(new ItemFluidTank(), "fluid_tank"),
                of(new Item().setCreativeTab(EnumCreativeTab.PROCESSOR.tab).setUnlocalizedName(MOD_ID + ".processor_base"), "processor_base"),
                of(new StrategyAcceleration.ItemAccelerateProcessor(), "strategy_acceleration"),
                of(new StrategyExtraction.ItemExtractionProcessor(), "strategy_extraction"),
                of(new StrategyOneway.ItemOnewayProcessor(), "strategy_oneway")
        );
    }

    @OverridingMethodsMustInvokeSuper
    protected void registerBlocks(RegistryEvent.Register<Block> event){
        register(event,
                of(new BlockPipe(), "pipe"),
                of(new BlockFluidTank(), "fluid_tank")
        );
    }

    @OverridingMethodsMustInvokeSuper
    protected void registerTileEntities(){
        register(
                TileEntityPipe.class,
                TileEntityFluidTank.class
        );
    }

    protected void registerBlockColors(){
    }

    protected void registerItemColors(){
    }
}
