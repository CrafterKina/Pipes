package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.api.render.SpecialRendererSupplier;
import jp.crafterkina.pipes.client.tesr.processor.ExtractionProcessorRenderer;
import jp.crafterkina.pipes.common.RegistryEntries;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.item.ItemProcessor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by Kina on 2016/12/22.
 */
public class StrategyExtraction extends StrategyDefault implements SpecialRendererSupplier{
    private final TileEntityPipe te;
    private final ItemStack stack;
    private final EnumFacing from;
    private final int cycle;
    private final int amount;
    private final double speed;
    @SideOnly(Side.CLIENT)
    private ExtractionProcessorRenderer RENDER;

    StrategyExtraction(TileEntityPipe te, ItemStack stack, EnumFacing from, int cycle, int amount, double speed){
        super(te::getWorld);
        this.te = te;
        this.stack = stack;
        this.from = from;
        this.cycle = cycle;
        this.amount = amount;
        this.speed = speed;
        if(FMLCommonHandler.instance().getSide().isClient()){
            //noinspection NewExpressionSideOnly,VariableUseSideOnly
            RENDER = new ExtractionProcessorRenderer(stack, from, (200 / cycle) * (speed));
        }
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

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<TileEntity> getSpecialRenderer(){
        return RENDER;
    }

    public enum Material{
        WOOD(50, 1, 1, 0x9F844D),
        STONE(25, 1, 1, 0x9B9B9B),
        IRON(25, 8, 1, 0x535353),
        DIAMOND(100, 16, 0.25, 0x69DFDA),
        GOLD(12, 2, 4, 0xF9D74F);
        private final ItemStack stack;

        Material(int cycle, int amount, double speed, int color){
            stack = ItemExtractionProcessor.createStack(new ItemStack(RegistryEntries.ITEM.strategy_extraction), cycle, amount, speed, color);
        }

        public ItemStack getStack(){
            return stack.copy();
        }
    }

    public static class ItemExtractionProcessor extends ItemProcessor{
        public ItemExtractionProcessor(){
            setUnlocalizedName("extraction");
            setMaxStackSize(1);
        }

        public static ItemStack createStack(ItemStack stack, int cycle, int amount, double speed, int color){
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("cycle", cycle);
            compound.setInteger("amount", amount);
            compound.setDouble("speed", speed);
            compound.setInteger("color", color);
            stack.setTagCompound(compound);
            return stack;
        }

        public static int getColor(ItemStack stack, int layer){
            if(layer != 1) return Color.WHITE.getRGB();
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null || !compound.hasKey("color", Constants.NBT.TAG_INT)) return Color.WHITE.getRGB();
            return compound.getInteger("color");
        }

        @Override
        public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems){
            for(Material material : Material.values()){
                subItems.add(material.getStack());
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
        public IStrategy getStrategy(TileEntity entity, ItemStack stack){
            if(!(entity instanceof TileEntityPipe)) return null;
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null) return null;
            EnumFacing from = EnumFacing.VALUES[compound.getByte("from")];
            int cycle = compound.getInteger("cycle");
            int amount = compound.getInteger("amount");
            double speed = compound.getDouble("speed");
            return new StrategyExtraction((TileEntityPipe) entity, stack, from, cycle, amount, speed);
        }
    }
}
