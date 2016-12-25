package jp.crafterkina.pipes.common.creativetab;

import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.item.ItemPipe;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/26.
 */
public enum EnumCreativeTab{
    PIPE(new CreativeTabs(MOD_ID + ".pipe"){
        @Nonnull
        @Override
        public ItemStack getTabIconItem(){
            return ItemPipe.createPipeStack(new ItemStack(RegistryEntries.ITEM.pipe), EnumPipeMaterial.WOOD);
        }
    }),
    PROCESSOR(new CreativeTabs(MOD_ID + ".processor"){
        @Nonnull
        @Override
        public ItemStack getTabIconItem(){
            return new ItemStack(RegistryEntries.ITEM.processor_base);
        }
    }),
    UTILITY(new CreativeTabs(MOD_ID + ".utility"){
        @Nonnull
        @Override
        public ItemStack getTabIconItem(){
            return new ItemStack(RegistryEntries.ITEM.merchant_phone);
        }
    });
    public final CreativeTabs tab;

    EnumCreativeTab(CreativeTabs tab){
        this.tab = tab;
    }

    public Item setCreativeTab(Item item){
        return item.setCreativeTab(tab);
    }

    public Block setCreativeTab(Block block){
        return block.setCreativeTab(tab);
    }
}
