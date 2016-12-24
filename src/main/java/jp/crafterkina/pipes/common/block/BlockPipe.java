package jp.crafterkina.pipes.common.block;

import jp.crafterkina.pipes.api.pipe.IItemFlowHandler;
import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/14.
 */
public class BlockPipe extends BlockContainer{
    public static final PropertyBool[] CONNECT = Arrays.stream(EnumFacing.VALUES).map(f -> PropertyBool.create("c_" + f.getName())).toArray(PropertyBool[]::new);
    public static final PropertyBool COVERED = PropertyBool.create("covered");
    private static final AxisAlignedBB CORE = new AxisAlignedBB(5.5 / 16d, 5.5 / 16d, 5.5 / 16d, 10.5 / 16d, 10.5 / 16d, 10.5 / 16d);
    private static final AxisAlignedBB[] PIPE = {new AxisAlignedBB(6 / 16d, 0d, 6 / 16d, 10 / 16d, 5.5 / 16d, 10 / 16d), new AxisAlignedBB(6 / 16d, 10.5 / 16d, 6 / 16d, 10 / 16d, 1d, 10 / 16d), new AxisAlignedBB(6 / 16d, 6 / 16d, 0d, 10 / 16d, 10 / 16d, 5.5 / 16d), new AxisAlignedBB(6 / 16d, 6 / 16d, 10.5 / 16d, 10 / 16d, 10 / 16d, 1d), new AxisAlignedBB(0d, 6 / 16d, 6 / 16d, 5.5 / 16d, 10 / 16d, 10 / 16d), new AxisAlignedBB(10.5 / 16d, 6 / 16d, 6 / 16d, 1d, 6 / 16d, 6 / 16d)};

    @SuppressWarnings("deprecation")
    public BlockPipe(){
        super(Material.GLASS);
        setUnlocalizedName(MOD_ID + ".pipe");
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setHardness(0.2f);
        setResistance(0.1f);
        IBlockState state = getBlockState().getBaseState();
        for(EnumFacing f : EnumFacing.VALUES){
            state = state.withProperty(CONNECT[f.getIndex()], false);
        }
        state = state.withProperty(COVERED, false);
        setDefaultState(state);
    }

    public static int getColor(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex){
        if(worldIn == null || pos == null) return 0xFFFFFF;
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return 0xFFFFFF;
        TileEntityPipe pipe = (TileEntityPipe) te;
        return tintIndex == 0 ? 0x9F844D : pipe.coverColor;
    }

    @Override
    public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems){
        ItemStack stack = new ItemStack(itemIn);
        NBTTagCompound compound = new NBTTagCompound();
        {
            stack.setTagCompound(compound);
            compound.setBoolean("covered", false);
            subItems.add(stack);
        }
        for(EnumDyeColor color : EnumDyeColor.values()){
            stack = new ItemStack(itemIn);
            compound = new NBTTagCompound();
            {
                stack.setTagCompound(compound);
                compound.setBoolean("covered", true);
                compound.setInteger("color", ItemDye.DYE_COLORS[color.getDyeDamage()]);
            }
            subItems.add(stack);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null) compound = new NBTTagCompound();
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return;
        TileEntityPipe pipe = (TileEntityPipe) te;
        pipe.coverColor = compound.getBoolean("covered") ? compound.getInteger("color") : -1;
        worldIn.notifyBlockUpdate(pos, state, state, 8);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(playerIn.getHeldItem(hand).isEmpty()){
            TileEntity te = worldIn.getTileEntity(pos);
            if(te == null || !te.hasCapability(IStrategy.IStrategyHandler.CAPABILITY, facing)) return false;
            IStrategy.IStrategyHandler handler = te.getCapability(IStrategy.IStrategyHandler.CAPABILITY, facing);
            assert handler != null;
            ItemStack removed = handler.remove();
            Block.spawnAsEntity(worldIn, pos, removed);
            worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), getActualState(worldIn.getBlockState(pos), worldIn, pos), 8);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state){
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if(tileentity instanceof TileEntityPipe){
            TileEntityPipe pipe = (TileEntityPipe) tileentity;
            pipe.dropItems();
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(IBlockState state){
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return 0;
        TileEntityPipe pipe = (TileEntityPipe) te;
        return pipe.getComparatorPower();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side){
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return 0;
        TileEntityPipe pipe = (TileEntityPipe) te;
        return pipe.getWeakPower(side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return;
        TileEntityPipe pipe = (TileEntityPipe) te;
        pipe.onRedstonePowered(worldIn.isBlockIndirectlyGettingPowered(pos));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta){
        return new TileEntityPipe();
    }

    @Nonnull
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return CORE;
    }

    @Nullable
    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos){
        return CORE;
    }

    @Nonnull
    @Override
    @Deprecated
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        return CORE;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Deprecated
    public void addCollisionBoxToList(IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CORE);
        Arrays.stream(EnumFacing.VALUES).filter(f -> worldIn.getBlockState(pos).getBlock().getActualState(stateIn, worldIn, pos).getValue(CONNECT[f.getIndex()])).forEach(f -> addCollisionBoxToList(pos, entityBox, collidingBoxes, PIPE[f.getIndex()]));
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end){
        return Arrays.stream(EnumFacing.VALUES).filter(f -> worldIn.getBlockState(pos).getBlock().getActualState(state, worldIn, pos).getValue(CONNECT[f.getIndex()])).map(f -> rayTrace(pos, start, end, PIPE[f.getIndex()])).filter(Objects::nonNull).filter(r -> r.typeOfHit != RayTraceResult.Type.MISS).findFirst().orElseGet(() -> rayTrace(pos, start, end, CORE));
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

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Nonnull
    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos){
        TileEntity te = worldIn.getTileEntity(pos);
        TileEntityPipe pipe = te instanceof TileEntityPipe ? (TileEntityPipe) te : null;
        for(EnumFacing face : EnumFacing.VALUES){
            state = state.withProperty(CONNECT[face.getIndex()], canBeConnectedTo(worldIn, pos, face));
        }
        if(pipe == null) return state;
        state = state.withProperty(COVERED, pipe.covered());
        return state;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing){
        BlockPos p = pos.add(facing.getDirectionVec());
        IBlockState s = world.getBlockState(p);
        TileEntity te = world.getTileEntity(p);
        return s.getBlock() == this || (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) || (te != null && te.hasCapability(IItemFlowHandler.CAPABILITY, facing.getOpposite()));
    }

    @Override
    public boolean rotateBlock(World world, @Nonnull BlockPos pos, EnumFacing axis){
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return false;
        TileEntityPipe pipe = (TileEntityPipe) te;
        return pipe.rotateProcessor(axis);
    }

    @Nullable
    @Override
    public EnumFacing[] getValidRotations(World world, @Nonnull BlockPos pos){
        return EnumFacing.VALUES;
    }


    @Override
    public boolean recolorBlock(World world, @Nonnull BlockPos pos, EnumFacing side, @Nonnull EnumDyeColor color){
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return false;
        TileEntityPipe pipe = (TileEntityPipe) te;
        pipe.recolor(ItemDye.DYE_COLORS[color.getDyeDamage()]);
        return true;
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
        return new BlockStateContainer(this, Arrays.stream(new IProperty<?>[][]{CONNECT, {COVERED}}).flatMap(Arrays::stream).toArray(IProperty[]::new));
    }
}
