package jp.crafterkina.pipes.common.recipe.vanilla;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static jp.crafterkina.pipes.common.RegistryEntries.merchant_phone;

public enum CraftManager{
    INSTANCE;

    public void register(){
        GameRegistry.addRecipe(new ItemStack(merchant_phone), "IRI", "IGI", "IEI", 'G', Items.EMERALD, 'R', Blocks.REDSTONE_TORCH, 'E', Items.ENDER_PEARL, 'I', Items.IRON_INGOT);
        GameRegistry.addShapelessRecipe(new ItemStack(merchant_phone), merchant_phone);
    }
}
