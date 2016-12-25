package jp.crafterkina.pipes.common.item;

import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.common.creativetab.EnumCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/20.
 */
public abstract class ItemProcessor extends Item implements IStrategy.StrategySupplier{
    private String unlocalizedName;

    protected ItemProcessor(){
        EnumCreativeTab.PROCESSOR.setCreativeTab(this);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        TileEntity te = worldIn.getTileEntity(pos);
        if(te == null || !te.hasCapability(IStrategy.IStrategyHandler.CAPABILITY, facing)) return EnumActionResult.PASS;
        IStrategy.IStrategyHandler handler = te.getCapability(IStrategy.IStrategyHandler.CAPABILITY, facing);
        assert handler != null;
        ItemStack removed = handler.remove();
        Block.spawnAsEntity(worldIn, pos, removed);
        ItemStack held = attachItem(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        if(handler.attach(held)){
            //noinspection deprecation
            worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), te.getBlockType().getActualState(worldIn.getBlockState(pos), worldIn, pos), 8);
            return EnumActionResult.SUCCESS;
        }else return EnumActionResult.PASS;
    }

    protected ItemStack attachItem(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        return player.getHeldItem(hand);
    }

    @Override
    @Nonnull
    public Item setUnlocalizedName(@Nonnull String unlocalizedName){
        this.unlocalizedName = unlocalizedName;
        return this;
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(){
        return this.unlocalizedName;
    }


    @Override
    @Nonnull
    public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack){
        return "strategy." + MOD_ID + '.' + getUnlocalizedName();
    }
}
