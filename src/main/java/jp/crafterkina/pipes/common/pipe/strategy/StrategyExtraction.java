package jp.crafterkina.pipes.common.pipe.strategy;

import com.google.common.collect.ImmutableList;
import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.api.render.ISpecialRenderer;
import jp.crafterkina.pipes.client.tesr.processor.ExtractionProcessorRenderer;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.item.ItemProcessor;
import lombok.AllArgsConstructor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.BiFunction;

/**
 * Created by Kina on 2016/12/22.
 */
public class StrategyExtraction extends StrategyDefault{
    private final TileEntityPipe te;
    private final ItemStack stack;
    private final EnumFacing from;
    private final int cycle;
    private final int amount;
    private final double speed;

    StrategyExtraction(TileEntityPipe te, ItemStack stack, EnumFacing from, int cycle, int amount, double speed){
        super(te::getWorld);
        this.te = te;
        this.stack = stack;
        this.from = from;
        this.cycle = cycle;
        this.amount = amount;
        this.speed = speed;
    }

    @Override
    public void tick(){
        if(!te.hasCapability(IItemFlowHandler.CAPABILITY, from)) return;
        World world = this.world.get();
        long l = world.getTotalWorldTime();
        if(l % cycle != 0) return;
        BlockPos pos = te.getPos().add(from.getDirectionVec());
        TileEntity rawinv = world.getTileEntity(pos);
        if(rawinv == null || !rawinv.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, from.getOpposite()))
            return;
        IItemHandler handler = rawinv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, from.getOpposite());
        ItemStack stack = ItemStack.EMPTY;
        assert handler != null;
        for(int i = 0; i < handler.getSlots(); i++){
            if(handler.extractItem(i, amount, true).isEmpty()){
                continue;
            }
            stack = handler.extractItem(i, amount, false);
            break;
        }
        IItemFlowHandler flower = te.getCapability(IItemFlowHandler.CAPABILITY, from);
        assert flower != null;
        flower.flow(new FlowItem(stack, from.getOpposite(), speed * te.getBaseSpeed()));
    }

    @Override
    public IStrategy rotate(EnumFacing axis){
        return new StrategyExtraction(te, stack, axis, cycle, amount, speed);
    }

    @AllArgsConstructor
    public enum Material{
        WOOD(50, 1, 1, 0x9F844D),
        STONE(25, 1, 1, 0x9B9B9B),
        IRON(25, 8, 1, 0x535353),
        DIAMOND(100, 16, 0.25, 0x69DFDA),
        GOLD(12, 2, 4, 0xF9D74F);
        public static final ImmutableList<Material> VALUES = ImmutableList.copyOf(values());

        private final int cycle;
        private final int amount;
        private final double speed;
        private final int color;
    }

    public static class ItemExtractionProcessor extends ItemProcessor{
        public ItemExtractionProcessor(){
            setUnlocalizedName("extraction");
            setMaxStackSize(1);
        }

        public static ItemStack createStack(ItemStack stack, Material material){
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("material", material.ordinal());
            stack.setTagCompound(compound);
            return stack;
        }

        public static int getColor(ItemStack stack, int layer){
            if(layer != 1) return Color.WHITE.getRGB();
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null) return Color.WHITE.getRGB();
            Material material = Material.VALUES.get(compound.getInteger("material"));
            return material.color;
        }

        @Override
        public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems){
            if(!isInCreativeTab(tab)) return;
            for(Material material : Material.values()){
                subItems.add(createStack(new ItemStack(this), material));
            }
        }

        @Override
        protected ItemStack attachItem(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
            if(!(worldIn.getTileEntity(pos) instanceof TileEntityPipe)) return ItemStack.EMPTY;
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null) return ItemStack.EMPTY;
            compound.setByte("from", (byte) facing.getIndex());
            return stack;
        }

        @Override
        protected BiFunction<ItemStack, TileEntity, IStrategy> getStrategy(){
            return (s, t) -> {
                if(!(t instanceof TileEntityPipe)) return null;
                NBTTagCompound compound = s.getTagCompound();
                if(compound == null) return null;
                EnumFacing from = EnumFacing.VALUES[compound.getByte("from")];
                Material material = Material.VALUES.get(compound.getInteger("material"));
                return new StrategyExtraction((TileEntityPipe) t, s, from, material.cycle, material.amount, material.speed);
            };
        }

        @SuppressWarnings("MethodCallSideOnly")
        @Override
        protected boolean hasAdditionalCapability(ItemStack stack, @Nonnull Capability<?> capability, @Nullable EnumFacing facing){
            return FMLCommonHandler.instance().getSide().isClient() && hasCapabilityClient(stack, capability, facing);
        }

        @SuppressWarnings("MethodCallSideOnly")
        @Nullable
        @Override
        protected <T> T getAdditionalCapability(ItemStack stack, @Nonnull Capability<T> capability, @Nullable EnumFacing facing){
            if(FMLCommonHandler.instance().getSide().isClient())
                return getCapabilityClient(stack, capability, facing);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private boolean hasCapabilityClient(@SuppressWarnings("unused") ItemStack stack, @Nonnull Capability<?> capability, @Nullable EnumFacing facing){
            return capability == ISpecialRenderer.CAPABILITY;
        }

        @SuppressWarnings("unchecked")
        @SideOnly(Side.CLIENT)
        private <T> T getCapabilityClient(ItemStack stack, @Nonnull Capability<T> capability, @Nullable EnumFacing facing){
            if(capability == ISpecialRenderer.CAPABILITY && stack.hasTagCompound()){
                Material material = Material.VALUES.get(stack.getTagCompound().getInteger("material"));
                return (T) new ExtractionProcessorRenderer(stack, EnumFacing.VALUES[stack.getTagCompound().getByte("from")], (200 / material.cycle) * material.speed);
            }
            return null;
        }
    }
}
