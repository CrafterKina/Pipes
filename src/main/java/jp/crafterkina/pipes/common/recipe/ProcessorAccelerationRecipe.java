package jp.crafterkina.pipes.common.recipe;

import jp.crafterkina.pipes.common.pipe.strategy.StrategyAcceleration;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static jp.crafterkina.pipes.common.RegistryEntries.ITEM.strategy_acceleration;

/**
 * Created by Kina on 2016/12/25.
 */
public class ProcessorAccelerationRecipe implements IRecipe{
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn){
        Set<ItemStack> stacks = IntStream.range(0, inv.getSizeInventory()).mapToObj(inv::getStackInSlot).filter(i -> !i.isEmpty()).collect(Collectors.toSet());
        return !stacks.isEmpty() && stacks.stream().allMatch(i -> i.getItem() == strategy_acceleration);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv){
        double sum = IntStream.range(0, inv.getSizeInventory()).mapToObj(inv::getStackInSlot).filter(i -> !i.isEmpty()).mapToDouble(StrategyAcceleration.ItemAccelerateProcessor::acceleration).sum();
        StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(strategy_acceleration, 1), sum);
        return StrategyAcceleration.ItemAccelerateProcessor.createStack(new ItemStack(strategy_acceleration, 1), sum);
    }

    @Override
    public int getRecipeSize(){
        return 9;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput(){
        return new ItemStack(strategy_acceleration);
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv){
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
