package jp.crafterkina.pipes.api.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Supplier for {@link TileEntitySpecialRenderer}.
 *
 * @author Kina
 * @since 1.0
 * @version 1.0
 */
public interface SpecialRendererSupplier{
    @SideOnly(Side.CLIENT)
    TileEntitySpecialRenderer<TileEntity> getSpecialRenderer();
}