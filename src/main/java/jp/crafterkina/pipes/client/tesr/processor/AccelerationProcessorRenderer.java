package jp.crafterkina.pipes.client.tesr.processor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Kina on 2016/12/21.
 */
public class AccelerationProcessorRenderer extends TileEntitySpecialRenderer<TileEntity>{
    private final ItemStack stack;
    private final double acceleration;

    public AccelerationProcessorRenderer(ItemStack stack, double acceleration){
        this.stack = stack;
        this.acceleration = acceleration;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.rotate((float) ((te.getWorld().getTotalWorldTime() + partialTicks) * Math.pow(acceleration, 2)), 0, 1, 0);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderItemStack(stack);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
