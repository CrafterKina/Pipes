package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.api.render.SpecialRendererSupplier;
import jp.crafterkina.pipes.client.tesr.processor.AccelerationProcessorRenderer;
import jp.crafterkina.pipes.common.item.ItemProcessor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by Kina on 2016/12/20.
 */
public class StrategyAcceleration extends StrategyDefault implements SpecialRendererSupplier{
    private final double acceleration;
    @SideOnly(Side.CLIENT)
    private AccelerationProcessorRenderer render;

    StrategyAcceleration(Supplier<World> world, ItemStack stack, double acceleration){
        super(world);
        this.acceleration = acceleration;
        if(FMLCommonHandler.instance().getSide().isClient()){
            //noinspection NewExpressionSideOnly,VariableUseSideOnly
            this.render = new AccelerationProcessorRenderer(stack, acceleration);
        }
    }

    @Override
    public FlowItem turn(FlowItem item, Vec3d... connecting){
        return super.turn(new FlowItem(item.getStack(), item.getVelocity().scale(acceleration)), connecting);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<TileEntity> getSpecialRenderer(){
        return render;
    }

    public static class ItemAccelerateProcessor extends ItemProcessor{
        public ItemAccelerateProcessor(){
            setUnlocalizedName("accelerator");
            setMaxStackSize(1);
        }

        public static ItemStack createStack(ItemStack stack, double acceleration){
            NBTTagCompound compound = Optional.ofNullable(stack.getTagCompound()).orElseGet(() -> {
                NBTTagCompound c = new NBTTagCompound();
                stack.setTagCompound(c);
                return c;
            });
            compound.setDouble("acceleration", acceleration);
            return stack;
        }

        public static int getColor(ItemStack stack, int layer){
            if(layer == 1) return Color.WHITE.getRGB();
            float[] hsb = Color.RGBtoHSB(0, 255, 255, null);
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null || !compound.hasKey("acceleration", Constants.NBT.TAG_DOUBLE))
                return Color.WHITE.getRGB();
            hsb[0] -= 0.1d * (compound.getDouble("acceleration") - 1d);
            return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        }

        public static double acceleration(ItemStack stack){
            return stack.getTagCompound() == null || !stack.getTagCompound().hasKey("acceleration", Constants.NBT.TAG_DOUBLE) ? 1 : stack.getTagCompound().getDouble("acceleration");
        }

        @Override
        protected BiFunction<ItemStack, TileEntity, IStrategy> getStrategy(){
            return (s, t) -> new StrategyAcceleration(t::getWorld, s, acceleration(s));
        }

        @Override
        public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems){
            for(int i = 1; i <= 6; i++){
                for(int j = 0; 4 > j; j++){
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
        @SuppressWarnings("deprecation")
        public String getItemStackDisplayName(@Nonnull ItemStack stack){
            return I18n.translateToLocalFormatted(getUnlocalizedNameInefficiently(stack) + ".name", acceleration(stack)).trim();
        }
    }
}