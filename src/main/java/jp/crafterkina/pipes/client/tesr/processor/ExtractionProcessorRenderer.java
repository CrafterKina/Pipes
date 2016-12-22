package jp.crafterkina.pipes.client.tesr.processor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Created by Kina on 2016/12/22.
 */
public class ExtractionProcessorRenderer extends TileEntitySpecialRenderer<TileEntity>{
    private final EnumFacing facing;
    private ItemStack stack;

    public ExtractionProcessorRenderer(ItemStack stack, EnumFacing facing){
        this.stack = stack;
        this.facing = facing.rotateAround(EnumFacing.Axis.Y).rotateAround(EnumFacing.Axis.Y).rotateAround(EnumFacing.Axis.Y);
        System.out.println(facing + "," + this.facing);
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage){
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.25, 0.25, 0.25);
        GlStateManager.rotate(90, facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
        if(facing == EnumFacing.DOWN)
            GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate((te.getWorld().getTotalWorldTime() + partialTicks) * 1, 0, 1, 0);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderItemStack(stack);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack){
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
    }
}
