package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.common.creativetab.EnumCreativeTab;
import net.minecraft.item.Item;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * @author Kina
 */
public class ItemProcessorBase extends Item{
    public ItemProcessorBase(){
        setCreativeTab(EnumCreativeTab.PROCESSOR.tab);
        setUnlocalizedName(MOD_ID + ".processor_base");
    }
}
