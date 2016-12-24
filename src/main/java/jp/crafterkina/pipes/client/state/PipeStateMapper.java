package jp.crafterkina.pipes.client.state;

import com.google.common.collect.Maps;
import jp.crafterkina.pipes.common.block.BlockPipe;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created by Kina on 2016/12/23.
 */
public class PipeStateMapper extends StateMapperBase{
    @Nonnull
    @Override
    protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state){
        Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
        String s = String.format("%s:%s", Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain(), "pipe" + (state.getValue(BlockPipe.COVERED) ? "_covered" : ""));
        map.remove(BlockPipe.COVERED);
        return new ModelResourceLocation(s, this.getPropertyString(map));
    }
}
