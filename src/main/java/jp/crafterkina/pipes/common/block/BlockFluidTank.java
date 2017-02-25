package jp.crafterkina.pipes.common.block;

import jp.crafterkina.pipes.common.block.entity.TileEntityFluidTank;
import jp.crafterkina.pipes.common.creativetab.EnumCreativeTab;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/27.
 */
public class BlockFluidTank extends BlockContainer{
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyBool BOTTOM = PropertyBool.create("bottom");

    public BlockFluidTank(){
        super(Material.ROCK);
        setUnlocalizedName(MOD_ID + ".fluid_tank");
        EnumCreativeTab.UTILITY.setCreativeTab(this);
        setHardness(0.2f);
        setResistance(0.1f);
        setDefaultState(getBlockState().getBaseState().withProperty(TOP, true).withProperty(BOTTOM, true));
    }

    public static int getColor(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex){
        return 0xFFFFFF;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        return ((TileEntityFluidTank) worldIn.getTileEntity(pos)).onActivated(state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
        ((TileEntityFluidTank) worldIn.getTileEntity(pos)).onPlaced(state, placer, stack);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta){
        return new TileEntityFluidTank();
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side){
        return true;
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer(){
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
        return EnumBlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos){
        return state.withProperty(BOTTOM, !canBeConnectedTo(worldIn, pos, EnumFacing.DOWN)).withProperty(TOP, !canBeConnectedTo(worldIn, pos, EnumFacing.UP));
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing){
        if(!isTank(world, pos.add(facing.getDirectionVec()))) return false;
        TileEntityFluidTank faced = (TileEntityFluidTank) world.getTileEntity(pos.add(facing.getDirectionVec()));
        TileEntityFluidTank tank = (TileEntityFluidTank) world.getTileEntity(pos);
        FluidStack c1 = tank.getContainingFluid();
        FluidStack c2 = faced.getContainingFluid();
        return c1 == null || c2 == null || c1.isFluidEqual(c2);
    }

    private boolean isTank(IBlockAccess world, BlockPos pos){
        return world.getBlockState(pos).getBlock() == this;
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return 0;
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, TOP, BOTTOM);
    }
}
