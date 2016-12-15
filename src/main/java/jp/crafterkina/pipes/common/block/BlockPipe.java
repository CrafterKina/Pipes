package jp.crafterkina.pipes.common.block;

import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public static final PropertyBool[] GATE = Arrays.stream(EnumFacing.VALUES).map(f -> PropertyBool.create("g_" + f.getName())).toArray(PropertyBool[]::new);
    private static final AxisAlignedBB CORE = new AxisAlignedBB(5.5 / 16d, 5.5 / 16d, 5.5 / 16d, 10.5 / 16d, 10.5 / 16d, 10.5 / 16d);
    private static final AxisAlignedBB[] PIPE = {new AxisAlignedBB(6 / 16d, 0d, 6 / 16d, 10 / 16d, 6 / 16d, 10 / 16d), new AxisAlignedBB(6 / 16d, 10 / 16d, 6 / 16d, 10 / 16d, 1d, 10 / 16d), new AxisAlignedBB(6 / 16d, 6 / 16d, 0d, 10 / 16d, 10 / 16d, 6 / 16d), new AxisAlignedBB(6 / 16d, 6 / 16d, 10 / 16d, 10 / 16d, 10 / 16d, 1d), new AxisAlignedBB(0d, 6 / 16d, 6 / 16d, 6 / 16d, 10 / 16d, 10 / 16d), new AxisAlignedBB(10 / 16d, 6 / 16d, 6 / 16d, 1d, 6 / 16d, 6 / 16d)};

    public BlockPipe(){
        super(Material.GLASS);
        setUnlocalizedName(MOD_ID + ".pipe");
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setHardness(0.2f);
        setResistance(0.1f);
        IBlockState state = getBlockState().getBaseState();
        for(EnumFacing f : EnumFacing.VALUES){
            state = state.withProperty(CONNECT[f.getIndex()], false);
            //state.withProperty(GATE[f.getIndex()],false);
        }
        setDefaultState(state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta){
        return new TileEntityPipe();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return CORE;
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos){
        return CORE;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        return CORE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CORE);
        Arrays.stream(EnumFacing.VALUES).filter(f -> worldIn.getBlockState(pos).getBlock().getActualState(stateIn, worldIn, pos).getValue(CONNECT[f.getIndex()])).forEach(f -> addCollisionBoxToList(pos, entityBox, collidingBoxes, PIPE[f.getIndex()]));
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end){
        return Arrays.stream(EnumFacing.VALUES).filter(f -> worldIn.getBlockState(pos).getBlock().getActualState(state, worldIn, pos).getValue(CONNECT[f.getIndex()])).map(f -> rayTrace(pos, start, end, PIPE[f.getIndex()])).filter(Objects::nonNull).filter(r -> r.typeOfHit != RayTraceResult.Type.MISS).findFirst().orElseGet(() -> rayTrace(pos, start, end, CORE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
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

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos){
        return state.withProperty(CONNECT[0], canBeConnectedTo(worldIn, pos, EnumFacing.DOWN)).withProperty(CONNECT[1], canBeConnectedTo(worldIn, pos, EnumFacing.UP)).withProperty(CONNECT[2], canBeConnectedTo(worldIn, pos, EnumFacing.NORTH)).withProperty(CONNECT[3], canBeConnectedTo(worldIn, pos, EnumFacing.SOUTH)).withProperty(CONNECT[4], canBeConnectedTo(worldIn, pos, EnumFacing.WEST)).withProperty(CONNECT[5], canBeConnectedTo(worldIn, pos, EnumFacing.EAST));
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing){
        return world.getBlockState(pos.add(facing.getDirectionVec())).getBlock() == this;
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return 0;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, CONNECT[0], CONNECT[1], CONNECT[2], CONNECT[3], CONNECT[4], CONNECT[5]);
    }
}