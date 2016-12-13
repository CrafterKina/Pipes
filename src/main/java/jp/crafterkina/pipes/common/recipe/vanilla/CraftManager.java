package jp.crafterkina.pipes.common.recipe.vanilla;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static jp.crafterkina.pipes.common.RegistryEntries.merchant_phone;

public enum CraftManager{
    INSTANCE;

    public void register(){
        GameRegistry.addShapelessRecipe(new ItemStack(merchant_phone), merchant_phone);
    }
}
