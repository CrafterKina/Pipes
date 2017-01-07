package jp.crafterkina.pipes.client.tesr.processor;

import jp.crafterkina.pipes.api.render.ISpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Kina on 2016/12/21.
 */
@SideOnly(Side.CLIENT)
public class AccelerationProcessorRenderer implements ISpecialRenderer{
    private final ItemStack stack;
    private final double acceleration;

    public AccelerationProcessorRenderer(ItemStack stack, double acceleration){
        this.stack = stack;
        this.acceleration = acceleration;
    }

    @Override
    public void renderAt(TileEntity te, double x, double y, double z, float partialTicks, TileEntityRendererDispatcher dispatcher, int destroyStage){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.rotate((float) ((te.getWorld().getTotalWorldTime() + partialTicks) * Math.pow(acceleration, 2)), 0, 1, 0);
        ISpecialRenderer.bindTexture(dispatcher, TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderItemStack(stack);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
