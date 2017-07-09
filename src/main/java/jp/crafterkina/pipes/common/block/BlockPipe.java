package jp.crafterkina.pipes.common.block;

import jp.crafterkina.pipes.api.pipe.IStrategy;
import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import jp.crafterkina.pipes.common.creativetab.EnumCreativeTab;
import jp.crafterkina.pipes.common.item.ItemPipe;
import jp.crafterkina.pipes.common.pipe.EnumPipeMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
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
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static jp.crafterkina.pipes.api.PipesConstants.MOD_ID;

/**
 * Created by Kina on 2016/12/14.
 */
public class BlockPipe extends BlockContainer{
    public static final PropertyBool[] CONNECT = Arrays.stream(EnumFacing.VALUES).map(f -> PropertyBool.create("c_" + f.getName())).toArray(PropertyBool[]::new);
    public static final PropertyBool COVERED = PropertyBool.create("covered");
    public static final PropertyEnum<EnumPipeMaterial.TextureType> TEX_TYPE = PropertyEnum.create("type", EnumPipeMaterial.TextureType.class);
    private static final AxisAlignedBB CORE = new AxisAlignedBB(5.5 / 16d, 5.5 / 16d, 5.5 / 16d, 10.5 / 16d, 10.5 / 16d, 10.5 / 16d);
    private static final AxisAlignedBB[] PIPE = {new AxisAlignedBB(6 / 16d, 0d, 6 / 16d, 10 / 16d, 5.5 / 16d, 10 / 16d), new AxisAlignedBB(6 / 16d, 10.5 / 16d, 6 / 16d, 10 / 16d, 1d, 10 / 16d), new AxisAlignedBB(6 / 16d, 6 / 16d, 0d, 10 / 16d, 10 / 16d, 5.5 / 16d), new AxisAlignedBB(6 / 16d, 6 / 16d, 10.5 / 16d, 10 / 16d, 10 / 16d, 1d), new AxisAlignedBB(0d, 6 / 16d, 6 / 16d, 5.5 / 16d, 10 / 16d, 10 / 16d), new AxisAlignedBB(10.5 / 16d, 10 / 16d, 10 / 16d, 1d, 6 / 16d, 6 / 16d)};

    @SuppressWarnings("deprecation")
    public BlockPipe(){
        super(Material.GLASS);
        setUnlocalizedName(MOD_ID + ".pipe");
        EnumCreativeTab.PIPE.setCreativeTab(this);
        setHardness(0.2f);
        setResistance(0.1f);
        IBlockState state = getBlockState().getBaseState();
        for(EnumFacing f : EnumFacing.VALUES){
            state = state.withProperty(CONNECT[f.getIndex()], false);
        }
        state = state.withProperty(COVERED, false);
        state = state.withProperty(TEX_TYPE, EnumPipeMaterial.TextureType.NOISE);
        setDefaultState(state);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static int getColor(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex){
        if(worldIn == null || pos == null) return 0xFFFFFF;
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return 0xFFFFFF;
        TileEntityPipe pipe = (TileEntityPipe) te;
        return tintIndex == 0 ? pipe.getFrameColor() : pipe.coverColor;
    }

    @Override
    public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems){
        for(EnumPipeMaterial material : EnumPipeMaterial.VALUES){
            {
                subItems.add(ItemPipe.createPipeStack(new ItemStack(this), material));
            }
            for(EnumDyeColor color : EnumDyeColor.values()){
                subItems.add(ItemPipe.createPipeStack(new ItemStack(this), material, color));
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
        TileEntity te = worldIn.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return;
        TileEntityPipe pipe = (TileEntityPipe) te;
        pipe.onBlockPlacedBy(state, placer, stack);
    }


    @Override
    public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nonnull ItemStack stack){
        if(te instanceof TileEntityPipe){
            TileEntityPipe pipe = (TileEntityPipe) te;
            spawnAsEntity(worldIn, pos, ItemPipe.createPipeStack(new ItemStack(this), pipe.getMaterial(), pipe.covered(), pipe.coverColor));
        }else{
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }

    @Override
    public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune){
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityPipe){
            TileEntityPipe pipe = (TileEntityPipe) te;
            drops.add(ItemPipe.createPipeStack(new ItemStack(this), pipe.getMaterial(), pipe.covered(), pipe.coverColor));
        }
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

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    protected void drawOutlineBox(DrawBlockHighlightEvent event){
        World world = FMLClientHandler.instance().getWorldClient();
        if(event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK) return;
        BlockPos pos = event.getTarget().getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() != this) return;

        EntityPlayer player = event.getPlayer();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        float f1 = 0.002f;

        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

        //noinspection deprecation
        Arrays.stream(EnumFacing.VALUES).mapToInt(EnumFacing::getIndex).filter(i -> world.getBlockState(pos).getBlock().getActualState(state, world, pos).getValue(CONNECT[i])).forEach(i -> RenderGlobal.drawSelectionBoundingBox(PIPE[i].offset(pos).grow(f1).offset(-d0, -d1, -d2), 0, 0, 0, 0.4f));

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
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
    @SideOnly(Side.CLIENT)
    @Deprecated
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos){
        return CORE.offset(pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Deprecated
    public void addCollisionBoxToList(IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_){
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CORE);
        Arrays.stream(EnumFacing.VALUES).filter(f -> worldIn.getBlockState(pos).getBlock().getActualState(stateIn, worldIn, pos).getValue(CONNECT[f.getIndex()])).forEach(f -> addCollisionBoxToList(pos, entityBox, collidingBoxes, PIPE[f.getIndex()]));
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end){
        return Optional.ofNullable(rayTrace(pos, start, end, CORE)).filter(p -> p.typeOfHit != RayTraceResult.Type.MISS).orElse(Arrays.stream(EnumFacing.VALUES).filter(f -> worldIn.getBlockState(pos).getBlock().getActualState(state, worldIn, pos).getValue(CONNECT[f.getIndex()])).map(f -> Optional.ofNullable(rayTrace(pos, start, end, PIPE[f.getIndex()])).map(r -> new RayTraceResult(r.typeOfHit, r.hitVec, f, r.getBlockPos())).filter(r -> r.typeOfHit != RayTraceResult.Type.MISS)).filter(Optional::isPresent).map(Optional::get).findFirst().orElse(null));
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
        if(!pipe.isMaterialAvailable()) return state;
        state = state.withProperty(TEX_TYPE, pipe.getMaterial().TYPE);
        return state;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityPipe && ((TileEntityPipe) te).canBeConnectedTo(facing);
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

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player){
        ItemStack stack = new ItemStack(this);
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof TileEntityPipe)) return stack;
        TileEntityPipe pipe = (TileEntityPipe) te;
        NBTTagCompound compound = new NBTTagCompound();

        stack.setTagCompound(compound);
        compound.setString("material", pipe.getMaterial().name());
        compound.setBoolean("covered", pipe.covered());
        compound.setInteger("color", pipe.coverColor);

        return stack;
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
        return new BlockStateContainer(this, Arrays.stream(new IProperty<?>[][]{CONNECT, {COVERED, TEX_TYPE}}).flatMap(Arrays::stream).toArray(IProperty[]::new));
    }
}
