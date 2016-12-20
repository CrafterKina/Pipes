package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.common.item.ItemProcessor;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by Kina on 2016/12/20.
 */
public class StrategyAcceleration extends StrategyDefault{
    private final double acceleration;

    public StrategyAcceleration(Supplier<World> world, double acceleration){
        super(world);
        this.acceleration = acceleration;
    }

    @Override
    public FlowItem turn(FlowItem item, Vec3d... connecting){
        return super.turn(new FlowItem(item.getStack(), item.getVelocity().scale(acceleration)), connecting);
    }

    public static class ItemAccelerateProcessor extends ItemProcessor{
        public ItemAccelerateProcessor(){
            setUnlocalizedName("accelerator");
        }

        @Override
        public IStrategy getStrategy(TileEntity entity, ItemStack stack){
            return new StrategyAcceleration(entity::getWorld, acceleration(stack));
        }

        @Override
        public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems){
            super.getSubItems(itemIn, tab, subItems);
            for(int i = 1; i <= 6; i++){
                for(int j = 1; 4 > j; j++){
                    double l = i + j / 4d;
                    ItemStack stack = new ItemStack(this);
                    NBTTagCompound compound = Optional.ofNullable(stack.getTagCompound()).orElseGet(() -> {
                        NBTTagCompound c = new NBTTagCompound();
                        stack.setTagCompound(c);
                        return c;
                    });
                    compound.setDouble("acceleration", l);
                    subItems.add(stack);
                }
            }
        }

        @Override
        @Nonnull
        public String getItemStackDisplayName(@Nonnull ItemStack stack){
            return I18n.format(getUnlocalizedNameInefficiently(stack), acceleration(stack));
        }

        private double acceleration(ItemStack stack){
            return stack.getTagCompound() == null || !stack.getTagCompound().hasKey("acceleration", Constants.NBT.TAG_DOUBLE) ? 1 : stack.getTagCompound().getDouble("acceleration");
        }
    }
}