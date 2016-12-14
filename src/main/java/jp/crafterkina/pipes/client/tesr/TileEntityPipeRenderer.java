package jp.crafterkina.pipes.client.tesr;

import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Kina on 2016/12/14.
 */
@SideOnly(Side.CLIENT)
public class TileEntityPipeRenderer extends TileEntitySpecialRenderer<TileEntityPipe>{
    @Override
    public void renderTileEntityAt(TileEntityPipe te, double x, double y, double z, float partialTicks, int destroyStage){

    }
}
