package jp.crafterkina.pipes.common.recipe.brewing;

import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

/**
 * @author Kina
 */
public enum ProcessorAccelerationRecipe implements IBrewingRecipe{
    INSTANCE;

    public static void register(){
        BrewingRecipeRegistry.addRecipe(new ItemStack(RegistryEntries.ITEM.processor_base), new ItemStack(Items.SUGAR), StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(RegistryEntries.ITEM.strategy_acceleration), 1.25));
        BrewingRecipeRegistry.addRecipe(INSTANCE);
    }

    @Override
    public boolean isInput(@Nonnull ItemStack input){
        return input.getItem() == RegistryEntries.ITEM.strategy_acceleration;
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack ingredient){
        return ingredient.getItem() == Items.GLOWSTONE_DUST;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient){
        if(!isInput(input) || !isIngredient(ingredient)) return ItemStack.EMPTY;
        input = input.copy();
        NBTTagCompound compound = input.getTagCompound();
        if(compound == null || !compound.hasKey("acceleration", Constants.NBT.TAG_DOUBLE)) return ItemStack.EMPTY;
        compound.setDouble("acceleration", compound.getDouble("acceleration") + 0.25);
        return input;
    }
}
