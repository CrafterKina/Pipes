package jp.crafterkina.pipes.common.block;

import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Kina on 2016/12/14.
 */
public class BlockPipe extends BlockContainer{
    public BlockPipe(){
        super(Material.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta){
        return new TileEntityPipe();
    }
}
