package jp.crafterkina.pipes.api.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Kina on 2016/12/21.
 */
public interface SpecialRendererSupplier{
    @SideOnly(Side.CLIENT)
    TileEntitySpecialRenderer<TileEntity> getSpecialRenderer();
}