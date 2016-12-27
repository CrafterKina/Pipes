package jp.crafterkina.pipes.common.pipe.strategy;

import jp.crafterkina.pipes.api.pipe.FlowItem;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.api.render.SpecialRendererSupplier;
import jp.crafterkina.pipes.client.tesr.processor.ExtractionProcessorRenderer;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.item.ItemProcessor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Kina on 2016/12/23.
 */
public class StrategyOneway implements IStrategy, SpecialRendererSupplier{
    private final ItemStack stack;
    private final EnumFacing to;
    @SideOnly(Side.CLIENT)
    private TileEntitySpecialRenderer<TileEntity> RENDER;

    private StrategyOneway(ItemStack stack, EnumFacing to){
        this.stack = stack;
        this.to = to;
        if(FMLCommonHandler.instance().getSide().isClient()){
            //noinspection NewExpressionSideOnly,VariableUseSideOnly
            RENDER = new ExtractionProcessorRenderer(stack, to, 1);
        }
    }

    @Override
    public FlowItem turn(FlowItem item, Vec3d... connecting){
        return new FlowItem(item.getStack(), to, item.getSpeed());
    }

    @Override
    public IStrategy rotate(EnumFacing axis){
        return new StrategyOneway(stack, to);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<TileEntity> getSpecialRenderer(){
        return RENDER;
    }

    public static class ItemOnewayProcessor extends ItemProcessor{
        public ItemOnewayProcessor(){
            setUnlocalizedName("oneway");
            setMaxStackSize(1);
        }

        public static int getColor(ItemStack stack, int layer){
            return layer == 1 ? 0x535353 : 0xffffff;
        }

        @Override
        protected ItemStack attachItem(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
            if(!(worldIn.getTileEntity(pos) instanceof TileEntityPipe)) return ItemStack.EMPTY;
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null){
                compound = new NBTTagCompound();
                stack.setTagCompound(compound);
            }
            compound.setByte("to", (byte) facing.getIndex());
            return stack;
        }

        @Override
        public IStrategy getStrategy(TileEntity entity, ItemStack stack){
            if(!(entity instanceof TileEntityPipe)) return null;
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null) return null;
            EnumFacing to = EnumFacing.VALUES[compound.getByte("to")];
            return new StrategyOneway(stack, to);
        }
    }
}
