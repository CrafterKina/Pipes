package jp.crafterkina.pipes.client.tesr.processor;

import jp.crafterkina.pipes.common.block.entity.TileEntityPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Function;

/**
 * Created by Kina on 2016/12/21.
 */
@SideOnly(Side.CLIENT)
public class DefaultProcessorRenderer extends TileEntitySpecialRenderer<TileEntity>{
    private final Function<TileEntityPipe, ItemStack> stack;

    public DefaultProcessorRenderer(Function<TileEntityPipe, ItemStack> stack){
        this.stack = stack;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.rotate((te.getWorld().getTotalWorldTime() + partialTicks) * 1, 1, 1, -1);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderItemStack(stack.apply((TileEntityPipe) te));
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
